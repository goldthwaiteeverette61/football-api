package org.dromara.biz.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 方案期数详情对象 biz_scheme_period_details
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_scheme_period_details")
public class BizSchemePeriodDetails extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 详情ID, 主键
     */
    @TableId(value = "detail_id")
    private Long detailId;

    /**
     * 所属期数ID (关联 biz_scheme_periods)
     */
    private Long periodId;

    /**
     * 比赛ID (关联 biz_matches)
     */
    private Long matchId;

    /**
     * 玩法代码 (例如: HAD, HHAD)
     */
    private String poolCode;

    /**
     * 投注选择 (例如: H, A, D)
     */
    private String selection;

    /**
     * 选择时的赔率
     */
    private BigDecimal odds;

    /**
     * 让球数
     */
    private String goalLine;

    private String matchName;

}
