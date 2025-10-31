package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizSchemePeriods;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 方案期數業務對象 biz_scheme_periods
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizSchemePeriods.class, reverseConvertGenerate = false)
public class BizSchemePeriodsBo extends BaseEntity {

    /**
     * 距上次红之后连黑次数 (红了则为0)
     */
    private Integer lostStreakSinceLastWin;

    /**
     * 限制金額。-1：不限制
     */
    private BigDecimal limitAmount;

    /**
     * 當前累計金額
     */
    private BigDecimal accumulatedAmount;

    /**
     * 期數ID, 主鍵
     */
    private Long periodId;


    /**
     * 本期狀態: pending-待開獎, won-已中獎, lost-未中獎
     */
    private String status;

    /**
     * 名稱
     */
    private String name;

    /**
     * 如果爲 true, 則查詢時排除 'draft' 狀態
     */
    private Boolean excludeDraft;

    /**
     * 如果爲 true, 則查詢時排除 'pending' 狀態
     */
    private Boolean excludePending;

    /**
     * 更新黑紅結果時間
     */
    private Date resultTime;

    /**
     * 截止投注時間
     */
    private Date deadlineTime;

    private int limit;

}
