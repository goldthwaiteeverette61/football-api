package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizSchemePeriods;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 方案期数视图对象 biz_scheme_periods
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizSchemePeriods.class)
public class BizSchemePeriodsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    @ExcelProperty(value = "期数ID, 主键")
    private Long periodId;

    /**
     * 本期状态: pending-待开奖, won-已中奖, lost-未中奖
     */
    @ExcelProperty(value = "本期状态: pending-待开奖, won-已中奖, lost-未中奖")
    private String status;

    /**
     * 名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新黑红结果时间
     */
    private Date resultTime;

    /**
     * 截止投注时间
     */
    private Date deadlineTime;


    private List<BizSchemePeriodDetailsVo> details;
}
