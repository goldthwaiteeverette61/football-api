package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizUserFollowDetails;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;



/**
 * 用户跟投详情视图对象 biz_user_follow_details
 *
 * @author Lion Li
 * @date 2025-08-26
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizUserFollowDetails.class)
public class BizUserFollowDetailsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 详情ID, 主键
     */
    @ExcelProperty(value = "详情ID, 主键")
    private Long followDetailId;

    /**
     * 所属跟投ID (关联 biz_user_follows)
     */
    @ExcelProperty(value = "所属跟投ID (关联 biz_user_follows)")
    private Long followId;

    /**
     * 方案期数id
     */
    @ExcelProperty(value = "方案期数id")
    private Long periodId;

    /**
     * biz_scheme_period_details表的id
     */
    @ExcelProperty(value = "biz_scheme_period_details表的id")
    private Long periodDetailsId;

    /**
     * 选择时的赔率
     */
    @ExcelProperty(value = "选择时的赔率")
    private BigDecimal odds;

    /**
     * 比赛ID (关联 biz_matches)
     */
    private Long matchId;

    /**
     * 比赛名称 (冗余)
     */
    private String matchName;

    /**
     * 玩法代码 (冗余)
     */
    private String poolCode;

    /**
     * 投注选择 (冗余)
     */
    private String selection;

    /**
     * 让球数 (冗余)
     */
    private String goalLine;
}
