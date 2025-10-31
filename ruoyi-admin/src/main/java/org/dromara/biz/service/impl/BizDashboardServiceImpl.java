package org.dromara.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizSystemReserveSummaryBo;
import org.dromara.biz.domain.vo.BizSystemReserveSummaryVo;
import org.dromara.biz.domain.vo.BizUserProgressVo;
import org.dromara.biz.domain.vo.SchemeDashboardVo;
import org.dromara.biz.service.*;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.service.ISysConfigService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BizDashboardServiceImpl implements IBizDashboardService {

    private final IBizSystemReserveSummaryService iBizSystemReserveSummaryService;
    private final IBizUserProgressService bizUserProgressService;
    private final ISysConfigService iSysConfigService;// 2. 注入服务

    @Override
    public SchemeDashboardVo getSchemeDashboardData() {
        Long userId = LoginHelper.getUserId();
        SchemeDashboardVo dashboardVo = new SchemeDashboardVo();

        // 1. 获取系统储备金
        List<BizSystemReserveSummaryVo> summaries = iBizSystemReserveSummaryService.queryList(new BizSystemReserveSummaryBo());
        BigDecimal reserveAmount = summaries.isEmpty() ? BigDecimal.ZERO : summaries.get(0).getTotalReserveAmount();
        dashboardVo.setSystemReserveAmount(reserveAmount);

        // 2. 核心优化：直接从 biz_user_progress 表获取用户的连输数据
        BizUserProgressVo progress = bizUserProgressService.findByUserId(userId);
        dashboardVo.setCumulativeLostAmountSinceWin(progress.getConsecutiveLossesAmount());
        dashboardVo.setCumulativeLostBetCountSinceWin(progress.getConsecutiveLosses());
        dashboardVo.setCommissionRate(progress.getCommissionRate());
        dashboardVo.setBetAmount(progress.getBetAmount());
        dashboardVo.setCurrentPeriodFollowAmount(progress.getBetAmount());
        dashboardVo.setCompensationStatus(iSysConfigService.lossesThresholdForReward().compareTo(dashboardVo.getCumulativeLostBetCountSinceWin()) != 1);
        dashboardVo.setBetType(progress.getBetType());

        return dashboardVo;
    }

}
