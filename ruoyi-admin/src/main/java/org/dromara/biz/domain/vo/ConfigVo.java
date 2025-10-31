package org.dromara.biz.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfigVo {
    /**
     * 最小起投
     */
    private BigDecimal baseBetAmount;

    /**
     * 理赔连黑次数
     */
    private Integer lossesThresholdForReward;

    /**
     * 提现手续费
     */
    private BigDecimal withdrawalFee;

    /**
     * 最小提现金额
     */
    private String withdrawalMin;
}
