package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizSchemePeriodDetails;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;



/**
 * 方案期数详情视图对象 biz_scheme_period_details
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizSchemePeriodDetails.class)
public class BizSchemePeriodDetailsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 详情ID, 主键
     */
    @ExcelProperty(value = "详情ID, 主键")
    private Long detailId;

    /**
     * 所属期数ID (关联 biz_scheme_periods)
     */
    @ExcelProperty(value = "所属期数ID (关联 biz_scheme_periods)")
    private Long periodId;

    /**
     * 比赛ID (关联 biz_matches)
     */
    @ExcelProperty(value = "比赛ID (关联 biz_matches)")
    private Long matchId;

    /**
     * 玩法代码 (例如: HAD, HHAD)
     */
    @ExcelProperty(value = "玩法代码 (例如: HAD, HHAD)")
    private String poolCode;

    /**
     * 投注选择 (例如: H, A, D)
     */
    @ExcelProperty(value = "投注选择 (例如: H, A, D)")
    private String selection;

    /**
     * 选择时的赔率
     */
    @ExcelProperty(value = "选择时的赔率")
    private BigDecimal odds;

    private BizMatchesVo bizMatchesVo;

    /**
     * 让球数
     */
    private String goalLine;

    private String matchName;

}
