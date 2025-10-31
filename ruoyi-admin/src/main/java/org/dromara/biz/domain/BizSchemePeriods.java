package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 方案期数对象 biz_scheme_periods
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_scheme_periods")
public class BizSchemePeriods extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_WON = "won";
    public static final String STATUS_LOST = "lost";

    /**
     * 距上次红之后连黑次数 (红了则为0)
     */
    private Integer lostStreakSinceLastWin;

    /**
     * 限制金额。-1：不限制
     */
    private BigDecimal limitAmount;

    /**
     * 当前累计金额
     */
    private BigDecimal accumulatedAmount;

    /**
     * 期数ID, 主键
     */
    @TableId(value = "period_id")
    private Long periodId;


    /**
     * 本期状态: pending-待开奖, won-已中奖, lost-未中奖
     */
    private String status;

    /**
     * 名称
     */
    private String name;

    /**
     * 更新黑红结果时间
     */
    private Date resultTime;

    /**
     * 截止投注时间
     */
    private Date deadlineTime;

}
