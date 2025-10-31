package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizUserFollowDetails;
import org.dromara.biz.domain.BizUserFollows;
import org.dromara.biz.domain.bo.*;
import org.dromara.biz.domain.vo.*;
import org.dromara.biz.mapper.BizUserFollowsMapper;
import org.dromara.biz.service.*;
import org.dromara.biz.utils.CodeUtils;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.SysUser;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户跟投记录Service业务层处理
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BizUserFollowsServiceImpl extends BaseImpl<BizUserFollows,BizUserFollowsVo> implements IBizUserFollowsService {

    private final BizUserFollowsMapper baseMapper;
    private final IBizSchemePeriodsService bizSchemePeriodsService;
    private final IBizUserFollowDetailsService bizUserFollowDetailsService;
    private final IBizMatchesService bizMatchesService;
    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;
    private final ISysConfigService iSysConfigService;
    private final IBizUserProgressService bizUserProgressService;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 【核心重構】批量确认功能，增加返佣逻辑
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchConfirmFollows(List<Long> followIds) {
        if (CollUtil.isEmpty(followIds)) {
            return;
        }

        List<BizUserFollows> followsToConfirm = baseMapper.selectList(
            Wrappers.<BizUserFollows>lambdaQuery().in(BizUserFollows::getFollowId, followIds)
        );

        List<BizUserFollows> processableFollows = followsToConfirm.stream()
            .filter(f -> "in_cart".equalsIgnoreCase(f.getStatus()))
            .collect(Collectors.toList());

        if (CollUtil.isEmpty(processableFollows)) {
            log.info("提供的跟投ID均不是待确认状态，无需处理。");
            return;
        }

        LambdaUpdateWrapper<BizUserFollows> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.in(BizUserFollows::getFollowId, processableFollows.stream().map(BizUserFollows::getFollowId).collect(Collectors.toList()))
            .set(BizUserFollows::getStatus, "bought");
        baseMapper.update(null, updateWrapper);

        // --- 开始处理三级返佣逻辑 ---

        // 1. 一次性获取所有相关的用户信息 (投注者、邀请人、代理人)
        List<Long> userIds = processableFollows.stream().map(BizUserFollows::getUserId).distinct().collect(Collectors.toList());
        Map<Long, SysUser> userMap = sysUserService.selectUserListByIds(userIds).stream()
            .collect(Collectors.toMap(SysUser::getUserId, Function.identity()));

        List<Long> inviterIds = userMap.values().stream()
            .map(SysUser::getInviterId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, SysUser> inviterMap = sysUserService.selectUserListByIds(inviterIds).stream()
            .collect(Collectors.toMap(SysUser::getUserId, Function.identity()));

        // 【核心新增】获取所有代理人信息
        List<Long> agentIds = userMap.values().stream()
            .map(SysUser::getAgentUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, SysUser> agentMap = sysUserService.selectUserListByIds(agentIds).stream()
            .collect(Collectors.toMap(SysUser::getUserId, Function.identity()));


        // 2. 遍历处理每一笔投注的返佣
        for (BizUserFollows follow : processableFollows) {
            SysUser bettor = userMap.get(follow.getUserId());
            if (bettor == null) {
                continue;
            }

            // --- 邀请人返佣 & 投注者本人返佣 (依赖于邀请人存在) ---
            if (bettor.getInviterId() != null) {
                SysUser inviter = inviterMap.get(bettor.getInviterId());
                if (inviter != null) {
                    // a. 邀请人获得返佣
                    BigDecimal inviterRate = new BigDecimal(iSysConfigService.selectConfigByKey("sys.biz.invitInviter"));
                    BigDecimal inviterAmount = follow.getBetAmount().multiply(inviterRate).setScale(2, RoundingMode.DOWN);
                    if (inviterAmount.compareTo(BigDecimal.ZERO) > 0) {
                        sysUserService.addBalance(inviter.getUserId(), inviterAmount);
                        createCommissionTransaction(follow, inviterAmount, "下级投注返佣", inviter.getUserId());
                    }

                    // b. 投注者本人获得返佣
                    BigDecimal myRate = new BigDecimal(iSysConfigService.selectConfigByKey("sys.biz.invitMy"));
                    BigDecimal myAmount = follow.getBetAmount().multiply(myRate).setScale(2, RoundingMode.DOWN);
                    if (myAmount.compareTo(BigDecimal.ZERO) > 0) {
                        sysUserService.addBalance(follow.getUserId(), myAmount);
                        createCommissionTransaction(follow, myAmount, "投注返佣", follow.getUserId());
                    }
                }
            }

            // --- 【核心新增】代理人返佣 ---
            if (bettor.getAgentUserId() != null) {
                SysUser agent = agentMap.get(bettor.getAgentUserId());
                if (agent != null) {
                    BigDecimal agentRate = new BigDecimal(iSysConfigService.selectConfigByKey("sys.biz.invitAgent"));
                    BigDecimal agentAmount = follow.getBetAmount().multiply(agentRate).setScale(2, RoundingMode.DOWN);
                    if (agentAmount.compareTo(BigDecimal.ZERO) > 0) {
                        sysUserService.addBalance(agent.getUserId(), agentAmount);
                        createCommissionTransaction(follow, agentAmount, "代理返佣", agent.getUserId());
                    }
                }
            }
        }

        log.info("成功确认了 {} 条跟投记录的状态，并处理了返佣。", processableFollows.size());
    }

    /**
     * 辅助方法：创建返佣交易记录
     */
    private void createCommissionTransaction(BizUserFollows follow, BigDecimal amount, String remarks, Long recipientUserId) {
        BizTransactionsBo transactionBo = new BizTransactionsBo();
        transactionBo.setUserId(recipientUserId);
        transactionBo.setAmount(amount);
        transactionBo.setTransactionType("COMMISSION");
        transactionBo.setStatus("CONFIRMED");
        transactionBo.setSourceId(follow.getFollowId().toString());
        transactionBo.setRemarks(remarks);
        bizTransactionsService.insertByBo(transactionBo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchFail(BatchFailBo bo) {
        List<Long> followIds = bo.getFollowIds();
        if (CollUtil.isEmpty(followIds)) {
            return;
        }

        // 1. 查询所有需要处理的跟投记录
        List<BizUserFollows> followsToFail = baseMapper.selectList(
            Wrappers.<BizUserFollows>lambdaQuery().in(BizUserFollows::getFollowId, followIds)
        );

        // 2. 筛选出可以被标记为失败的记录 (例如，状态不是 'settled')
        List<BizUserFollows> processableFollows = followsToFail.stream()
            .filter(f -> !"settled".equalsIgnoreCase(f.getStatus()))
            .collect(Collectors.toList());

        if (CollUtil.isEmpty(processableFollows)) {
            log.info("提供的跟投ID均已结算，无法标记为失败。");
            return;
        }

        // 3. 遍历处理每一条记录：退款并创建交易流水
        for (BizUserFollows follow : processableFollows) {
            // 3.1 将金额退还给用户账户
            sysUserService.addBalance(follow.getUserId(), follow.getBetAmount());

            // 3.2 创建一笔退款交易记录
            BizTransactionsBo transactionBo = new BizTransactionsBo();
            transactionBo.setUserId(follow.getUserId());
            transactionBo.setUserName(follow.getUserName());
            transactionBo.setAmount(follow.getBetAmount());
            transactionBo.setTransactionType("REFUND"); // 交易类型为退款
            transactionBo.setStatus("CONFIRMED");
            transactionBo.setSourceId(follow.getFollowId().toString());
            transactionBo.setRemarks("跟投失败退款：" + bo.getRemark());
            bizTransactionsService.insertByBo(transactionBo);

            //本次投注金额改为0
            BizUserProgressVo bizUserProgressVo = bizUserProgressService.findByUserId(follow.getUserId());
            BizUserProgressBo bizUserProgressBo = new BizUserProgressBo();
            bizUserProgressBo.setProgressId(bizUserProgressVo.getProgressId());
            bizUserProgressBo.setBetAmount(BigDecimal.ZERO);
            bizUserProgressService.updateByBo(bizUserProgressBo);
        }

        // 4. 批量更新这些记录的状态和备注
        LambdaUpdateWrapper<BizUserFollows> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.in(BizUserFollows::getFollowId, processableFollows.stream().map(BizUserFollows::getFollowId).collect(Collectors.toList()))
            .set(BizUserFollows::getStatus, "failed")
            .set(BizUserFollows::getRemark, bo.getRemark()); // 假设 BizUserFollows 实体有 remark 字段
        baseMapper.update(null, updateWrapper);



        log.info("成功将 {} 条跟投记录标记为失败并退款。", processableFollows.size());
    }

    @Override
    @Transactional
    public void saveFollowDetails(UserFollowDetailsSaveBo bo) {
        List<Long> followIds = bo.getFollowIds();
        List<UserFollowDetailsSaveBo.DetailItem> allSelections = bo.getCombinations();

        if (CollUtil.isEmpty(followIds) || CollUtil.isEmpty(allSelections)) {
            return;
        }

        // 1. 高效获取生成描述所需的所有比赛信息
        List<Long> matchIds = allSelections.stream().map(UserFollowDetailsSaveBo.DetailItem::getMatchId).distinct().collect(Collectors.toList());
        Map<Long, BizMatchesVo> matchMap = bizMatchesService.queryMapByIds(matchIds);

        // 2. 生成可读性更强的 BetOddsDesc 字符串
        String betOddsDesc = allSelections.stream()
            .collect(Collectors.groupingBy(UserFollowDetailsSaveBo.DetailItem::getMatchId))
            .values().stream()
            .map(matchItems -> {
                BizMatchesVo matchInfo = matchMap.get(matchItems.get(0).getMatchId());
                String matchName = (matchInfo != null) ? (matchInfo.getHomeTeamName() + " vs " + matchInfo.getAwayTeamName()) : "未知比赛";

                String selectionsDesc = matchItems.stream()
                    .collect(Collectors.groupingBy(UserFollowDetailsSaveBo.DetailItem::getPoolCode))
                    .entrySet().stream()
                    .map(poolEntry -> {
                        String poolCode = poolEntry.getKey();
                        // 【核心修改】使用 CodeUtils
                        String poolName = CodeUtils.POOL_CODE_MAP.getOrDefault(poolCode, poolCode).replace("胜平负","");

                        String selectionsInPool = poolEntry.getValue().stream()
                            .map(item -> {
                                String selectionName = CodeUtils.SELECTION_MAP.getOrDefault(item.getSelection(), item.getSelection());
                                return selectionName + "@" + item.getOdds();
                            })
                            .collect(Collectors.joining(","));
                        return "("+poolName + selectionsInPool+")";
                    })
                    .collect(Collectors.joining(" "));

                return matchName+ selectionsDesc;
            })
            .collect(Collectors.joining(" ||| "));

        // 3. 保证幂等性：一次性删除所有相关跟投的旧快照
        bizUserFollowDetailsService.delete(followIds);

        // 4. 准备批量插入的数据
        List<BizUserFollowDetails> allNewDetailsToInsert = new ArrayList<>();
        for (Long followId : followIds) {
            for (UserFollowDetailsSaveBo.DetailItem item : allSelections) {
                BizUserFollowDetails detail = new BizUserFollowDetails();
                detail.setFollowId(followId);
                detail.setPeriodId(item.getPeriodId());
                detail.setPeriodDetailsId(item.getPeriodDetailsId());
                detail.setOdds(item.getOdds());

                detail.setMatchId(item.getMatchId());
                detail.setPoolCode(item.getPoolCode());
                detail.setMatchName(item.getMatchName());
                detail.setSelection(item.getSelection());
                detail.setGoalLine(item.getGoalLine());
                allNewDetailsToInsert.add(detail);
            }
        }

        // 5. 批量插入详情快照
        if (CollUtil.isNotEmpty(allNewDetailsToInsert)) {
            bizUserFollowDetailsService.insertBatch(allNewDetailsToInsert);
        }

        // 6. 批量更新主跟投记录的描述字符串
        this.updateBetOddsDesc(followIds, betOddsDesc);
    }

    @Override
    @Transactional
    public void updateBetOddsDesc(List<Long> followIds, String betOddsDesc) {
        if (CollUtil.isEmpty(followIds)) return;
        LambdaUpdateWrapper<BizUserFollows> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.in(BizUserFollows::getFollowId, followIds)
            .set(BizUserFollows::getBetOddsDesc, betOddsDesc);
        baseMapper.update(null, updateWrapper);
    }



    @Override
    public BigDecimal sumAmountByPeriodIdAndUserId(Long periodId, Long userId) {
        if (periodId == null || userId == null) return BigDecimal.ZERO;
        BigDecimal total = baseMapper.sumAmountByPeriodIdAndUserId(periodId, userId);
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public BizUserFollowsVo queryDetailsById(Long followId) {
        // 1. 先查询出跟投记录的基础信息
        BizUserFollowsVo follow = this.queryById(followId);
        if (follow == null) {
            return null;
        }

        // 2. 根据 periodId 查询出方案的基础信息
        BizSchemePeriodsVo period = bizSchemePeriodsService.queryById(follow.getPeriodId());
        if (period == null) {
            return follow; // 如果方案找不到，至少返回跟投信息
        }

        // 3. 为方案填充比赛详情
        bizSchemePeriodsService.fillDetailsForPeriods(Collections.singletonList(period));

        // 4. 将包含完整比赛信息的方案对象，设置到跟投记录中
        follow.setBizSchemePeriodsVo(period);

        return follow;
    }

    /**
     * 查询用户跟投记录
     *
     * @param followId 主键
     * @return 用户跟投记录
     */
    @Override
    public BizUserFollowsVo queryById(Long followId){
        return baseMapper.selectVoById(followId);
    }

    /**
     * 分页查询用户跟投记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户跟投记录分页列表
     */
    @Override
    public TableDataInfo<BizUserFollowsVo> queryPageList(BizUserFollowsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizUserFollows> lqw = buildQueryWrapper(bo);
        Page<BizUserFollowsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);

        // 3. 构建返回结果
        TableDataInfo<BizUserFollowsVo> dataInfo = TableDataInfo.build(result);

        // 1. 【核心新增】执行统计查询
        Map<String, BigDecimal> summary = baseMapper.selectSumBetAmount(lqw); // 接收 Map
        BigDecimal totalBetAmount = summary.get("totalBetAmount");
        BigDecimal totalPayoutAmount = summary.get("totalPayoutAmount");

        // 4. 【核心新增】将统计结果放入 extra 字段
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("totalBetAmount", totalBetAmount != null ? totalBetAmount : BigDecimal.ZERO);
        extraData.put("totalPayoutAmount", totalPayoutAmount != null ? totalPayoutAmount : BigDecimal.ZERO);
        dataInfo.setExtra(extraData);

        return dataInfo;
    }

    /**
     * 查询符合条件的用户跟投记录列表
     *
     * @param bo 查询条件
     * @return 用户跟投记录列表
     */
    @Override
    public List<BizUserFollowsVo> queryList(BizUserFollowsBo bo) {
        LambdaQueryWrapper<BizUserFollows> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizUserFollows> buildQueryWrapper(BizUserFollowsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizUserFollows> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(BizUserFollows::getFollowId);
        lqw.eq(bo.getUserId() != null, BizUserFollows::getUserId, bo.getUserId());
        lqw.eq(bo.getPeriodId() != null, BizUserFollows::getPeriodId, bo.getPeriodId());
        lqw.eq(bo.getBetAmount() != null, BizUserFollows::getBetAmount, bo.getBetAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizUserFollows::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getBetType()), BizUserFollows::getBetType, bo.getBetType());
        lqw.eq(bo.getPayoutAmount() != null, BizUserFollows::getPayoutAmount, bo.getPayoutAmount());
        lqw.eq(bo.getCreatedAt() != null, BizUserFollows::getCreatedAt, bo.getCreatedAt());
        return lqw;
    }

    /**
     * 新增用户跟投记录
     *
     * @param bo 用户跟投记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizUserFollowsBo bo) {
        BizUserFollows add = MapstructUtils.convert(bo, BizUserFollows.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setFollowId(add.getFollowId());
        }
        return flag;
    }

    /**
     * 修改用户跟投记录
     *
     * @param bo 用户跟投记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizUserFollowsBo bo) {
        BizUserFollows update = MapstructUtils.convert(bo, BizUserFollows.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizUserFollows entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户跟投记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    @Override
    public Boolean insertOrUpdate(BizUserFollowsBo bo) {
        if(bo.getFollowId() != null && bo.getFollowId() > 0){
            return this.updateByBo(bo);
        }

        LambdaQueryWrapper<BizUserFollows> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizUserFollows::getFollowId, bo.getFollowId());
        BizUserFollowsVo vo = baseMapper.selectVoOne(lqw);

        if(vo != null){
            bo.setFollowId(vo.getFollowId());
            return this.updateByBo(bo);
        }else {
            return this.insertByBo(bo);
        }
    }

    @Override
    public TableDataInfo<BizUserFollowsVo> queryMyPageList(BizUserFollowsBo bo, PageQuery pageQuery) {
        // 1. 设置当前用户ID并执行基础分页查询
        bo.setUserId(LoginHelper.getUserId());
        Page<BizUserFollowsVo> pageResult = baseMapper.selectMyFollowsPage(pageQuery.build(), bo);
        List<BizUserFollowsVo> follows = pageResult.getRecords();

        if (CollUtil.isNotEmpty(follows)) {
            // 2. 提取所有不重复的 periodId 和 followId
            List<Long> periodIds = follows.stream().map(BizUserFollowsVo::getPeriodId).distinct().collect(Collectors.toList());
            List<Long> followIds = follows.stream().map(BizUserFollowsVo::getFollowId).collect(Collectors.toList());

            // 3. 一次性查询出所有相关的方案信息，并填充好比赛数据
            Map<Long, BizSchemePeriodsVo> periodsMap = bizSchemePeriodsService.queryMapByIds(periodIds);
            bizSchemePeriodsService.fillDetailsForPeriods(new ArrayList<>(periodsMap.values()));

            // 4. 一次性查询出所有相关的用户赔率快照
            List<BizUserFollowDetailsVo> userFollowDetails = bizUserFollowDetailsService.queryByFollowIds(followIds);
            // 将用户赔率快照按 followId 分组
            Map<Long, List<BizUserFollowDetailsVo>> userDetailsMap = userFollowDetails.stream()
                .collect(Collectors.groupingBy(BizUserFollowDetailsVo::getFollowId));

            // 5. 将数据组装到最终的返回结果中
            follows.forEach(follow -> {
                BizSchemePeriodsVo bizSchemePeriodsVo = periodsMap.get(follow.getPeriodId());
                if(bizSchemePeriodsVo != null){
                    follow.setPeriodName(bizSchemePeriodsVo.getName());
                }

                follow.setBizUserFollowDetailsVos(userDetailsMap.get(follow.getFollowId()));
            });
        }

        return TableDataInfo.build(pageResult);
    }


    @Override
    public BizUserFollowsVo queryUserLastFollow(Long userId) {
        return baseMapper.selectVoOne(
            this.lqw().eq(BizUserFollows::getUserId,userId)
                .orderByDesc(BizUserFollows::getFollowId).last("limit 1"));
    }

    @Override
    public BigDecimal getMinimumBetAmount() {
        Long userId = LoginHelper.getUserId();
        BizUserProgressVo progress = bizUserProgressService.findByUserId(userId);

        // 【核心修正】檢查這是否是一個新輪次的開始 (betAmount為0或null)
        if (progress.getLastBetAmount() == null || progress.getLastBetAmount().compareTo(BigDecimal.ZERO) == 0) {
            // 如果是，則最低投注額為系統設定的基礎金額
            return iSysConfigService.baseBetAmount();
        } else {
            // 否則 (在連敗輪次中)，最低投注額為上一筆的兩倍
            return progress.getLastBetAmount().multiply(new BigDecimal("2.00"));
        }
    }

    /**
     * 【核心重構】跟投校验逻辑
     */
    @Override
    public boolean followVerify(Long userId, BigDecimal betAmount, Long periodId) {
        // 1. 校验本期是否已跟投
        long count = baseMapper.selectCount(new LambdaQueryWrapper<BizUserFollows>()
            .eq(BizUserFollows::getUserId, userId)
            .ne(BizUserFollows::getStatus, "failed")
            .eq(BizUserFollows::getPeriodId, periodId));
        if (count > 0) {
            throw new ServiceException("本期您已跟投，请勿重复操作");
        }

        BigDecimal minimumBetAmount = this.getMinimumBetAmount();
        if(minimumBetAmount.compareTo(betAmount) == 1){
            throw new ServiceException("最少投注为："+minimumBetAmount);
        }

        return true;
    }

    @Override
    public BizUserFollowsVo queryUserLastSettledFollow(Long userId) {
        return baseMapper.selectUserLastSettledFollow(userId);
    }

}

