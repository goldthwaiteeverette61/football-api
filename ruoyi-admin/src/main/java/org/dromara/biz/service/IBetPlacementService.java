package org.dromara.biz.service;

import org.dromara.biz.domain.dto.BetOrderDto;

/**
 * 投注流程服务接口
 *
 * @author Lion Li
 */
public interface IBetPlacementService {

    /**
     * 处理用户提交的投注订单
     *
     * @param betOrderDto 包含订单所有信息的DTO
     */
    void placeBetOrder(BetOrderDto betOrderDto);

}
