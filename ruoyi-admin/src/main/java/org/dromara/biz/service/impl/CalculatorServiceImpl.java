package org.dromara.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizSchemePeriodDetailsBo;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.dto.UpdatePeriodDto;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.service.IBizSchemePeriodDetailsService;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.biz.service.ICalculatorService;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CalculatorServiceImpl implements ICalculatorService {

    private final IBizSchemePeriodsService schemePeriodsService;
    private final IBizSchemePeriodDetailsService schemePeriodDetailsService;

    @Override
    @Transactional
    public void updatePeriodDetails(UpdatePeriodDto dto) {
        // 1. 校验方案状态
        BizSchemePeriodsVo period = schemePeriodsService.queryById(dto.getPeriodId());
        if (period == null) {
            throw new ServiceException("方案不存在");
        }
        if (!"draft".equalsIgnoreCase(period.getStatus())) {
            throw new ServiceException("只有草稿状态的方案才能选择比赛");
        }

        // 2. 清空旧的比赛详情
        schemePeriodDetailsService.deleteByPeriodId(dto.getPeriodId());

        // 3. 插入新的比赛详情
        for (UpdatePeriodDto.SchemePeriodDetailDto detailDto : dto.getDetails()) {
            BizSchemePeriodDetailsBo detailBo = new BizSchemePeriodDetailsBo();
            detailBo.setPeriodId(dto.getPeriodId());
            detailBo.setMatchId(detailDto.getMatchId());
            detailBo.setMatchName(detailDto.getMatchName());
            detailBo.setPoolCode(detailDto.getPoolCode());
            detailBo.setSelection(detailDto.getSelection());
            detailBo.setOdds(detailDto.getOdds());
            detailBo.setGoalLine(detailDto.getGoalLine());
            schemePeriodDetailsService.insertByBo(detailBo);
        }

        // 4. 核心修改：更新方案的过关方式
        BizSchemePeriodsBo periodUpdateBo = new BizSchemePeriodsBo();
        periodUpdateBo.setPeriodId(dto.getPeriodId());
        schemePeriodsService.updateByBo(periodUpdateBo);
    }

}
