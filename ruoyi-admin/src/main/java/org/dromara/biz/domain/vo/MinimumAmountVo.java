package org.dromara.biz.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinimumAmountVo {
    /**
     * 本次最小投注金额
     */
    private BigDecimal minimumBetAmount;

    /**
     * 系统最小投注金额
     */
    private BigDecimal baseBetAmount;
}
