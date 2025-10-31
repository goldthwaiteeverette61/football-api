package org.dromara.biz.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SchemeDashboardVo implements Serializable {

    /**
     * 系统储备金 (理赔金)
     */
    private BigDecimal systemReserveAmount;

    /**
     * 核心修改：重命名字段。
     * 含义：自上次中奖后，所有失败方案的累计跟投金额。
     */
    private BigDecimal cumulativeLostAmountSinceWin;

    /**
     * 本次进行中方案，已跟投的总金额
     */
    private BigDecimal currentPeriodFollowAmount;

    /**
     * 自上次中奖后，所有失败方案的累计跟投单数
     */
    private Integer cumulativeLostBetCountSinceWin;

    /**
     * 理赔金激活状态
     */
    private Boolean compensationStatus;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    /**
     * 上一次投注金额
     */
    private BigDecimal betAmount;

    /**
     * 投注类型:normal、double
     */
    private String betType;
}
