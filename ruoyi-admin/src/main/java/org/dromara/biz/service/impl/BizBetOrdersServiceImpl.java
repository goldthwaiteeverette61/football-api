package org.dromara.biz.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizBetOrders;
import org.dromara.biz.domain.bo.BizBetOrderDetailsBo;
import org.dromara.biz.domain.bo.BizBetOrdersBo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.dto.BatchUpdateOddsDto;
import org.dromara.biz.domain.vo.BizBetOrderDetailsVo;
import org.dromara.biz.domain.vo.BizBetOrdersVo;
import org.dromara.biz.domain.vo.BizMatchResultsVo;
import org.dromara.biz.mapper.BizBetOrdersMapper;
import org.dromara.biz.service.IBizBetOrderDetailsService;
import org.dromara.biz.service.IBizBetOrdersService;
import org.dromara.biz.service.IBizMatchResultsService;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 使用者投注訂單Service业务层处理
 *
 * @author Lion Li
 * @date 2025-10-11
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BizBetOrdersServiceImpl extends BaseImpl<BizBetOrders, BizBetOrdersVo> implements IBizBetOrdersService {

    private final BizBetOrdersMapper baseMapper;
    private final IBizBetOrderDetailsService bizBetOrderDetailsService;
    private final IBizTransactionsService bizTransactionsService;
    private final ISysUserService sysUserService;
    private final IBizMatchResultsService bizMatchResultsService;
    // 【新增】自我注入以確保事務方法被代理
    @Autowired
    @Lazy
    private IBizBetOrdersService self;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 【新增】處理超時草稿訂單的業務實現
     */
    @Override
    @Transactional
    public void processExpiredDraftOrders() {
        // 1. 查找所有狀態為 'draft' 且過期時間早於當前的訂單
        Date now = new Date();
        List<BizBetOrdersVo> expiredOrders = this.queryList(new BizBetOrdersBo()
            .setStatus(BizBetOrders.STATUS_DRAFT)
            .setLtExpirationTime(now)
        );

        if (CollUtil.isEmpty(expiredOrders)) {
            return;
        }

        // 2. 遍歷並逐一處理
        for (BizBetOrdersVo order : expiredOrders) {
            try {
                // 透過 self 呼叫，確保 cancelOrderAndRefund 方法的事務性生效
                this.cancelOrderAndRefund(order);
            } catch (Exception e) {
                // 捕獲單個訂單處理失敗的異常，記錄日誌後繼續處理下一個
                log.error("【定時任務-取消訂單】處理超時訂單 {} 時發生異常，將在下次任務重試。", order.getOrderId(), e);
            }
        }
    }

    /**
     * 【新增】以獨立事務處理單個訂單的取消和退款
     */
    @Override
    public void cancelOrderAndRefund(BizBetOrdersVo order) {
        // 安全校驗：再次確認訂單狀態，防止重複處理
        BizBetOrdersVo currentOrder = this.queryById(order.getOrderId());
        if (!BizBetOrders.STATUS_DRAFT.equals(currentOrder.getStatus())) {
            log.warn("訂單 {} 的狀態已不再是 'draft'，跳過取消操作。", order.getOrderId());
            return;
        }

        // 1. 返還用戶餘額
        boolean refundSuccess = sysUserService.addBalance(order.getUserId(), order.getBetAmount());
        if (!refundSuccess) {
            throw new ServiceException("為用戶 " + order.getUserId() + " 返還金額 " + order.getBetAmount() + " 失敗");
        }

        // 2. 創建退款交易流水
        BizTransactionsBo transactionBo = new BizTransactionsBo();
        transactionBo.setUserId(order.getUserId());
        transactionBo.setUserName(order.getUserName());
        transactionBo.setAmount(order.getBetAmount()); // 正數金額表示退款
        transactionBo.setTransactionType("REFUND");
        transactionBo.setStatus("CONFIRMED");
        transactionBo.setSourceId(order.getOrderId().toString());
        transactionBo.setRemarks("超時訂單取消退款: " + order.getOrderId());
        bizTransactionsService.insertByBo(transactionBo);

        // 3. 更新訂單狀態為 'void' (作廢)
        BizBetOrdersBo orderUpdateBo = new BizBetOrdersBo();
        orderUpdateBo.setOrderId(order.getOrderId());
        orderUpdateBo.setStatus(BizBetOrders.STATUS_VOID);
        this.updateByBo(orderUpdateBo);

        log.info("訂單 {} 已被自動取消並成功退款。", order.getOrderId());
    }

    /**
     * 【已重構】結算待開獎訂單的業務邏輯 (由 SettlementJob 定時觸發)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settlePendingOrdersJob() {
        // 1. 找出所有“待開獎”狀態的訂單
        List<BizBetOrdersVo> pendingOrders = this.queryList(new BizBetOrdersBo().setStatus(BizBetOrders.STATUS_PENDING));
        if (CollUtil.isEmpty(pendingOrders)) {
            log.info("【定時任務】沒有需要結算的訂單。");
            return;
        }

        // --- 2. 一次性獲取所有相關數據 ---
        // a. 收集所有訂單ID
        List<Long> allOrderIds = pendingOrders.stream().map(BizBetOrdersVo::getOrderId).collect(Collectors.toList());

        // b. 一次性查詢所有訂單的詳情
        List<BizBetOrderDetailsVo> allDetails = bizBetOrderDetailsService.queryList(new BizBetOrderDetailsBo().setOrderIds(allOrderIds));
        if (CollUtil.isEmpty(allDetails)) {
            log.warn("【定時任務】無法找到 {} 個訂單的任何詳情記錄。", pendingOrders.size());
            return;
        }

        // c. 收集所有詳情中涉及的比賽ID
        List<Long> allMatchIds = allDetails.stream().map(BizBetOrderDetailsVo::getMatchId).distinct().collect(Collectors.toList());

        // d. 一次性查詢所有相關比賽的結果
        List<BizMatchResultsVo> allMatchResults = bizMatchResultsService.queryListByMatchIds(allMatchIds);

        // --- 3. 將數據轉換為方便查找的 Map 結構 ---
        Map<Long, List<BizBetOrderDetailsVo>> detailsByOrderId = allDetails.stream().collect(Collectors.groupingBy(BizBetOrderDetailsVo::getOrderId));
        Map<String, String> winningSelectionsMap = allMatchResults.stream()
            .collect(Collectors.toMap(
                result -> result.getMatchId() + ":" + result.getPoolCode().toUpperCase(),
                BizMatchResultsVo::getCombination
            ));

        // --- 4. 遍歷訂單，使用已獲取的數據進行結算 ---
        for (BizBetOrdersVo order : pendingOrders) {
            List<BizBetOrderDetailsVo> currentOrderDetails = detailsByOrderId.get(order.getOrderId());
            if (CollUtil.isEmpty(currentOrderDetails)) {
                log.warn("【定時任務】訂單 {} 的詳情數據缺失，跳過。", order.getOrderId());
                continue;
            }

            // 檢查該訂單的所有比賽是否都已有賽果
            boolean allResultsAvailable = currentOrderDetails.stream()
                .allMatch(detail -> winningSelectionsMap.containsKey(detail.getMatchId() + ":" + detail.getPoolCode()));

            if (allResultsAvailable) {
                try {
                    // 如果賽果齊全，則執行結算
                    performSettlement(order, currentOrderDetails, winningSelectionsMap);
                } catch (Exception e) {
                    log.error("【定時任務】結算訂單 {} 失敗。錯誤信息: {}", order.getOrderId(), e.getMessage(), e);
                }
            } else {
                log.info("【定時任務】訂單 {} 因部分比賽結果未出，本次跳過結算。", order.getOrderId());
            }
        }
    }

    /**
     * 【已重構】批量更新赔率，拼接賠率描述，并确认订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateOdds(BatchUpdateOddsDto dto) {
        if (CollUtil.isEmpty(dto.getDetails())) {
            return true;
        }

        // --- 第 1 步：验证主订单状态 ---
        BizBetOrderDetailsVo firstDetail = bizBetOrderDetailsService.queryById(dto.getDetails().get(0).getDetailId());
        if (firstDetail == null) {
            throw new ServiceException("订单详情不存在");
        }
        Long orderId = firstDetail.getOrderId();

        BizBetOrdersVo order = this.queryById(orderId);
        if (order == null) {
            throw new ServiceException("主订单不存在");
        }
        if (!BizBetOrders.STATUS_DRAFT.equals(order.getStatus()) && !BizBetOrders.STATUS_PENDING.equals(order.getStatus())) {
            throw new ServiceException("只有'等待投注'或'待开奖'状态的订单才能修改赔率");
        }

        // --- 第 2 步：校验并更新所有详情的赔率，并收集赔率值 ---
        List<String> oddsList = new ArrayList<>();
        for (BatchUpdateOddsDto.Detail detail : dto.getDetails()) {
            if (detail.getOdds() == null || detail.getOdds().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("赔率必须大于0，请为所有投注项设置有效赔率");
            }

            BizBetOrderDetailsBo bo = new BizBetOrderDetailsBo();
            bo.setDetailId(detail.getDetailId());
            bo.setOdds(detail.getOdds());

            if (!bizBetOrderDetailsService.updateByBo(bo)) {
                throw new ServiceException("更新订单详情 " + detail.getDetailId() + " 的赔率失败");
            }
            // 【已修正】將更新後的賠率格式化為兩位小數的字串
            oddsList.add(String.format("%.2f", detail.getOdds()));
        }

        // --- 第 3 步：拼接赔率描述字符串并更新主订单 ---
        String oddsDesc = String.join(" , ", oddsList);

        BizBetOrdersBo bizBetOrdersBo = new BizBetOrdersBo();
        bizBetOrdersBo.setOrderId(orderId);
        bizBetOrdersBo.setOddsDesc(oddsDesc); // 设置拼接好的赔率描述

        // 只有当订单的原始状态是'draft'时，才将其更新为'pending'
        if (BizBetOrders.STATUS_DRAFT.equals(order.getStatus())) {
            bizBetOrdersBo.setStatus(BizBetOrders.STATUS_PENDING);
        }
        this.updateByBo(bizBetOrdersBo);

        return true;
    }


    /**
     * 【私有】執行結算的核心邏輯
     */
    private void performSettlement(BizBetOrdersVo order, List<BizBetOrderDetailsVo> details, Map<String, String> winningSelectionsMap) {
        // --- 1. 更新各投注項的輸贏狀態 ---
        for (BizBetOrderDetailsVo detail : details) {
            String resultMapKey = detail.getMatchId() + ":" + detail.getPoolCode();
            String winningSelection = winningSelectionsMap.get(resultMapKey);

            int winningStatus = (winningSelection != null && winningSelection.equals(detail.getSelection())) ? 1 : 2; // 1=贏, 2=輸

            BizBetOrderDetailsBo detailUpdateBo = new BizBetOrderDetailsBo();
            detailUpdateBo.setDetailId(detail.getDetailId());
            detailUpdateBo.setIsWinning(winningStatus);
            try{
                detailUpdateBo.setMatchScore(winningSelectionsMap.get(detail.getMatchId() + ":CRS"));
            }catch (Exception e){
                log.info(e.getMessage());
            }
            bizBetOrderDetailsService.updateByBo(detailUpdateBo);
            detail.setIsWinning(winningStatus); // 同步更新記憶體中對象的狀態
        }

        // --- 2. 按 (比賽+玩法) 對投注項進行分組 ---
        Map<String, List<BizBetOrderDetailsVo>> legs = details.stream()
            .collect(Collectors.groupingBy(d -> d.getMatchId() + ":" + d.getPoolCode()));

        // --- 3. 計算總注數和單注金額 ---
        long numberOfCombinations = 1L;
        for (List<BizBetOrderDetailsVo> legDetails : legs.values()) {
            numberOfCombinations *= legDetails.size();
        }
        if (numberOfCombinations == 0) {
            throw new ServiceException("計算出的注數為0，數據異常");
        }
        BigDecimal stakePerCombination = order.getBetAmount().divide(new BigDecimal(numberOfCombinations), 4, RoundingMode.HALF_UP);

        // --- 4. 生成所有投注組合，並計算總派彩金額 ---
        BigDecimal totalPayout = BigDecimal.ZERO;
        List<List<BizBetOrderDetailsVo>> allCombinations = generateCombinations(new ArrayList<>(legs.values()));

        for (List<BizBetOrderDetailsVo> combination : allCombinations) {
            boolean isCombinationWinner = combination.stream().allMatch(detail -> detail.getIsWinning() == 1);

            if (isCombinationWinner) {
                BigDecimal combinationOdds = combination.stream()
                    .map(BizBetOrderDetailsVo::getOdds)
                    .reduce(BigDecimal.ONE, BigDecimal::multiply);
                BigDecimal combinationPayout = stakePerCombination.multiply(combinationOdds);
                totalPayout = totalPayout.add(combinationPayout);
            }
        }

        // --- 5. 更新主訂單，返還獎金，並記錄流水 ---
        BizBetOrdersBo orderUpdateBo = new BizBetOrdersBo();
        orderUpdateBo.setOrderId(order.getOrderId());
        orderUpdateBo.setPayoutAmount(totalPayout.setScale(2, RoundingMode.HALF_UP));
        orderUpdateBo.setStatus(totalPayout.compareTo(BigDecimal.ZERO) > 0 ? BizBetOrders.STATUS_WON : BizBetOrders.STATUS_LOST);
        this.updateByBo(orderUpdateBo);

        if (totalPayout.compareTo(BigDecimal.ZERO) > 0) {
            sysUserService.addBalance(order.getUserId(), orderUpdateBo.getPayoutAmount());
            BizTransactionsBo transactionBo = new BizTransactionsBo();
            transactionBo.setUserId(order.getUserId());
            transactionBo.setUserName(order.getUserName());
            transactionBo.setAmount(orderUpdateBo.getPayoutAmount());
            transactionBo.setTransactionType("BONUS");
            transactionBo.setStatus("CONFIRMED");
            transactionBo.setSourceId(order.getOrderId().toString());
            transactionBo.setRemarks("訂單派獎: " + order.getOrderId());
            bizTransactionsService.insertByBo(transactionBo);
        }
        log.info("訂單 {} 結算完成，派彩金額: {}", order.getOrderId(), orderUpdateBo.getPayoutAmount());
    }

    /**
     * 递归辅助方法，用于生成所有可能的投注组合（笛卡尔积）
     * @param groups 投注项的分组列表
     * @return 所有组合的列表
     */
    private List<List<BizBetOrderDetailsVo>> generateCombinations(List<List<BizBetOrderDetailsVo>> groups) {
        List<List<BizBetOrderDetailsVo>> result = new ArrayList<>();
        generateCombinationsRecursive(groups, result, 0, new ArrayList<>());
        return result;
    }

    private void generateCombinationsRecursive(
        List<List<BizBetOrderDetailsVo>> groups,
        List<List<BizBetOrderDetailsVo>> result,
        int groupIndex,
        List<BizBetOrderDetailsVo> currentCombination) {

        if (groupIndex == groups.size()) {
            result.add(new ArrayList<>(currentCombination));
            return;
        }

        List<BizBetOrderDetailsVo> currentGroup = groups.get(groupIndex);
        for (BizBetOrderDetailsVo detail : currentGroup) {
            currentCombination.add(detail);
            generateCombinationsRecursive(groups, result, groupIndex + 1, currentCombination);
            currentCombination.remove(currentCombination.size() - 1); // Backtrack
        }
    }


    @Override
    public LambdaQueryWrapper<BizBetOrders> getLqw() {
        return super.lqw();
    }

    @Override
    public BizBetOrdersVo queryById(Long orderId) {
        return baseMapper.selectVoById(orderId);
    }

    @Override
    public TableDataInfo<BizBetOrdersVo> queryPageList(BizBetOrdersBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizBetOrders> lqw = buildQueryWrapper(bo);
        Page<BizBetOrdersVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<BizBetOrdersVo> queryList(BizBetOrdersBo bo) {
        LambdaQueryWrapper<BizBetOrders> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizBetOrders> buildQueryWrapper(BizBetOrdersBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizBetOrders> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(BizBetOrders::getOrderId);
        lqw.eq(bo.getUserId() != null, BizBetOrders::getUserId, bo.getUserId());
        lqw.eq(bo.getBetAmount() != null, BizBetOrders::getBetAmount, bo.getBetAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getCombinationType()), BizBetOrders::getCombinationType, bo.getCombinationType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizBetOrders::getStatus, bo.getStatus());
        lqw.eq(bo.getPayoutAmount() != null, BizBetOrders::getPayoutAmount, bo.getPayoutAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getOddsDesc()), BizBetOrders::getOddsDesc, bo.getOddsDesc());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), BizBetOrders::getUserName, bo.getUserName());

        lqw.lt(bo.getLtExpirationTime() != null, BizBetOrders::getExpirationTime, bo.getLtExpirationTime());
        return lqw;
    }

    @Override
    public Boolean insertByBo(BizBetOrdersBo bo) {
        BizBetOrders add = MapstructUtils.convert(bo, BizBetOrders.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setOrderId(add.getOrderId());
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(BizBetOrdersBo bo) {
        BizBetOrders update = MapstructUtils.convert(bo, BizBetOrders.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    private void validEntityBeforeSave(BizBetOrders entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public List<BizBetOrdersVo> queryList(LambdaQueryWrapper<BizBetOrders> lqw) {
        return super.queryList(lqw);
    }

    @Override
    public BizBetOrdersVo queryOne(LambdaQueryWrapper<BizBetOrders> lqw) {
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizBetOrdersBo bo) {
        BizBetOrders update = MapstructUtils.convert(bo, BizBetOrders.class);
        return baseMapper.saveOrUpdate(update);
    }
}

