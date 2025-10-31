package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;

/**
 * 用户跟投详情对象 biz_user_follow_details
 *
 * @author Lion Li
 * @date 2025-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_follow_details")
public class BizUserFollowDetails extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 详情ID, 主键
     */
    @TableId(value = "follow_detail_id")
    private Long followDetailId;

    /**
     * 所属跟投ID (关联 biz_user_follows)
     */
    private Long followId;

    /**
     * 方案期数id
     */
    private Long periodId;

    /**
     * biz_scheme_period_details表的id
     */
    private Long periodDetailsId;

    /**
     * 选择时的赔率
     */
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
