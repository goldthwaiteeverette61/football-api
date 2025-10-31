package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 用户跟投进度对象 biz_user_progress
 *
 * @author Lion Li
 * @date 2025-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_progress")
public class BizUserProgress extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String BET_TYPE_NORMAL = "normal";
    public static final String BET_TYPE_DOUBLE = "double";

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 进度记录的唯一ID, 主键
     */
    @TableId(value = "progress_id")
    private Long progressId;

    /**
     * 关联的用户ID (sys_user.user_id)
     */
    private Long userId;

    /**
     * 当前的连续失败次数，默认为 0
     */
    private Integer consecutiveLosses;

    /**
     * 新增：是否可领取理赔金 (0=否, 1=是)
     */
    private Integer canClaimReward;

    /**
     * 累计连输金额
     */
    private BigDecimal consecutiveLossesAmount;

    /**
     * 投注类型:normal、double
     */
    private String betType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    /**
     * 本轮投注
     */
    private BigDecimal betAmount;

    /**
     * 上一次投注金额
     */
    private BigDecimal lastBetAmount;

}
