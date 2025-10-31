package org.dromara.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizBetOrders;
import org.dromara.biz.domain.bo.BizBetOrderDetailsBo;
import org.dromara.biz.domain.bo.BizBetOrdersBo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.dto.BetOrderDto;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.service.*;
import org.dromara.biz.utils.PoolCodeUtils;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.service.ISysUserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 投注流程服务实现类
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BetPlacementServiceImpl implements IBetPlacementService {

    private final IBizBetOrdersService bizBetOrdersService;
    @Lazy
    private final IBizBetOrderDetailsService bizBetOrderDetailsService;
    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;
    // 【新增】注入比赛服务
    private final IBizMatchesService bizMatchesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void placeBetOrder(BetOrderDto betOrderDto) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();

        PoolCodeUtils.validate(betOrderDto);
        BigDecimal totalBetAmount = betOrderDto.getBetAmount();

        // 1. 扣除用户余额 (作为前置校验，失败则直接抛异常)
        if (!sysUserService.deductBalance(userId, totalBetAmount)) {
            throw new ServiceException("余额不足，投注失败");
        }

        // a. 收集所有比赛ID
        List<Long> matchIds = betOrderDto.getDetails().stream()
            .map(BetOrderDto.BetDetailDto::getMatchId)
            .distinct()
            .collect(Collectors.toList());

        // b. 查询所有相关比赛以获取开赛时间
        List<BizMatchesVo> matches = bizMatchesService.queryListByIds(matchIds);
        if (matches.size() != matchIds.size()) {
            throw new ServiceException("部分比赛信息不存在，无法投注");
        }

        // c. 找到最早的比赛开始时间
        Date earliestMatchTime = matches.stream()
            .map(BizMatchesVo::getMatchDatetime)
            .min(Date::compareTo)
            .orElseThrow(() -> new ServiceException("无法确定比赛开始时间"));

        // 2. 创建主订单
        BizBetOrdersBo orderBo = new BizBetOrdersBo();
        orderBo.setUserId(userId);
        orderBo.setUserName(loginUser.getUsername());
        orderBo.setBetAmount(totalBetAmount);
        orderBo.setCombinationType(betOrderDto.getCombinationType());
        orderBo.setExpirationTime(earliestMatchTime);
        orderBo.setStatus(BizBetOrders.STATUS_DRAFT);
        bizBetOrdersService.insertByBo(orderBo);
        Long newOrderId = orderBo.getOrderId();
        if (newOrderId == null) {
            throw new ServiceException("创建订单失败，请重试");
        }

        // 3. 循环处理并插入订单详情
        for (BetOrderDto.BetDetailDto detailDto : betOrderDto.getDetails()) {
            // 3.3 创建并插入订单详情Bo
            BizBetOrderDetailsBo detailBo = new BizBetOrderDetailsBo();
            PoolCodeUtils.formatDate(detailDto);
            detailBo.setOrderId(newOrderId);
            detailBo.setSelection(detailDto.getSelection());
            detailBo.setMatchId(detailDto.getMatchId());
            detailBo.setPoolCode(detailDto.getPoolCode());
            detailBo.setOdds(BigDecimal.ZERO); //为0，为了后面，强制检查

            bizBetOrderDetailsService.insertByBo(detailBo);
        }

        // 4. 创建交易流水
        BizTransactionsBo transactionBo = new BizTransactionsBo();
        transactionBo.setUserId(userId);
        transactionBo.setAmount(totalBetAmount.negate());
        transactionBo.setTransactionType("BET");
        transactionBo.setStatus("CONFIRMED");
        transactionBo.setSourceId(newOrderId.toString());
        transactionBo.setRemarks("投注订单: " + newOrderId);
        bizTransactionsService.insertByBo(transactionBo);

        log.info("用户 {} 成功创建投注订单 {}，金额: {}", userId, newOrderId, totalBetAmount);
    }

}
