package org.dromara.biz.service;

import org.dromara.biz.domain.dto.UpdatePeriodDetailsDto;
import org.dromara.biz.domain.dto.UpdatePeriodDto;

public interface ICalculatorService {

    /**
     * 更新某一期的投注详情 (*** 新增方法 ***)
     * @param dto 包含期数ID和投注详情列表的数据
     */
    void updatePeriodDetails(UpdatePeriodDto dto);
}
