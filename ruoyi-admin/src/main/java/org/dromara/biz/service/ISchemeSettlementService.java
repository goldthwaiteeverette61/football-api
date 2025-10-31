package org.dromara.biz.service;

import org.dromara.biz.domain.vo.BizSchemePeriodsVo;

public interface ISchemeSettlementService {

    /**
     * 结算单个方案期数 (此方法由事务控制)
     * @param period 待结算的期数对象
     */
    void settlePeriod(BizSchemePeriodsVo period);
}
