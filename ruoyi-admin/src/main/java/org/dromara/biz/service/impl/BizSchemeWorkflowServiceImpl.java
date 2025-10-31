package org.dromara.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizUserProgress;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.bo.BizUserFollowsBo;
import org.dromara.biz.domain.bo.BizUserProgressBo;
import org.dromara.biz.domain.dto.FollowSchemeDto;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.domain.vo.BizUserProgressVo;
import org.dromara.biz.service.*;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class BizSchemeWorkflowServiceImpl implements IBizSchemeWorkflowService {

    private final IBizSchemePeriodsService bizSchemePeriodsService;
    private final IBizUserFollowsService bizUserFollowsService;
    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;
    private final IBizUserProgressService iBizUserProgressService;
    private final ISysConfigService configService;

    /**
     * 【核心重构】用户跟投方案 - 最终修复版
     * 该版本集成了基于 `biz_user_progress` 的轮次模式逻辑
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followScheme(FollowSchemeDto dto) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();
        String username = loginUser.getUsername();
        Long periodId = dto.getPeriodId();
        BigDecimal betAmount = dto.getBetAmount();

        // 1. 获取用户当前的投注模式 (bet_type)，这是整个轮次的权威来源
        BizUserProgressVo progress = iBizUserProgressService.findByUserId(userId);

        // 2. 校验方案状态
        BizSchemePeriodsVo periodVo = bizSchemePeriodsService.queryById(periodId);
        if (periodVo == null || !"pending".equalsIgnoreCase(periodVo.getStatus())) {
            throw new ServiceException("本期已截止跟投");
        }

        // 3. 校验方案截止时间
        if (periodVo.getDeadlineTime() != null && new Date().after(periodVo.getDeadlineTime())) {
            throw new ServiceException("本期已截止跟投");
        }

        // 1. 获取最近一次的开奖记录
        BizSchemePeriodsVo lastSettledPeriod = bizSchemePeriodsService.lastWonOrLost();

        int lastStreak = 0;
        if (lastSettledPeriod != null && lastSettledPeriod.getLostStreakSinceLastWin() != null) {
            lastStreak = lastSettledPeriod.getLostStreakSinceLastWin();
        }

        int periodsLostCountLimt = Integer.parseInt(configService.selectConfigByKey("sys.biz.periodsLostCountLimt"));
        if (periodsLostCountLimt > lastStreak) {
            throw new ServiceException("需要连黑"+periodsLostCountLimt+"次，才能继续跟投");
        }

        // 4. 【核心修改】校验累计投注金额是否超过方案总限制 (-1代表不限制)
        if (periodVo.getLimitAmount() != null && periodVo.getLimitAmount().compareTo(BigDecimal.valueOf(-1)) != 0) {
            BigDecimal currentAccumulated = periodVo.getAccumulatedAmount() == null ? BigDecimal.ZERO : periodVo.getAccumulatedAmount();
            BigDecimal remainingAmount = periodVo.getLimitAmount().subtract(currentAccumulated);

            if (betAmount.compareTo(remainingAmount) > 0) {
                throw new ServiceException("投注失败，本期方案剩余可投金额为：" + remainingAmount);
            }
        }

        // 4. 【修复】调用包含了 betType 参数的校验逻辑
        bizUserFollowsService.followVerify(userId, betAmount, periodId);

        BizUserProgressBo bizUserProgressBo = new BizUserProgressBo();
        bizUserProgressBo.setBetAmount(betAmount);
        bizUserProgressBo.setProgressId(progress.getProgressId());
        //这里用来决定最后是否抽取他的比例，不抽取。则为0
        if(progress.getBetType().equals(BizUserProgress.BET_TYPE_DOUBLE)){
            bizUserProgressBo.setCommissionRate(configService.rewardPersent());
        }else{
            bizUserProgressBo.setCommissionRate(configService.rewardPersentNormal());
        }
        iBizUserProgressService.updateByBo(bizUserProgressBo);

        // 5. 扣除用户余额
        boolean deducted = sysUserService.deductBalance(userId, betAmount);
        if (!deducted) {
            throw new ServiceException("余额不足，跟投失败");
        }

        // 7. 【新增】更新方案的累计投注金额
        BizSchemePeriodsBo periodsBo = new BizSchemePeriodsBo();
        periodsBo.setPeriodId(periodId);
        periodsBo.setAccumulatedAmount(periodVo.getAccumulatedAmount().add(betAmount));
        bizSchemePeriodsService.updateByBo(periodsBo);

        // 6. 【修复】创建跟投记录时，记录下本次投注使用的模式
        BizUserFollowsBo followBo = new BizUserFollowsBo();
        followBo.setUserId(userId);
        followBo.setPeriodId(periodId);
        followBo.setBetAmount(betAmount);
        followBo.setUserName(username);
        followBo.setStatus("in_cart");
        followBo.setBetType(progress.getBetType()); // 关键：记录快照
        followBo.setCommissionRate(bizUserProgressBo.getCommissionRate());
        bizUserFollowsService.insertByBo(followBo);

        // 7. 创建交易流水
        BizTransactionsBo transactionBo = new BizTransactionsBo();
        transactionBo.setUserId(userId);
        transactionBo.setAmount(betAmount.negate());
        transactionBo.setCurrency("USDT");
        transactionBo.setUserName(username);
        transactionBo.setTransactionType("FOLLOW_BET");
        transactionBo.setStatus("CONFIRMED");
        transactionBo.setSourceId(followBo.getFollowId().toString());
        transactionBo.setRemarks(String.format("跟投方案: %s", periodVo.getName()));
        bizTransactionsService.insertByBo(transactionBo);
    }
}

