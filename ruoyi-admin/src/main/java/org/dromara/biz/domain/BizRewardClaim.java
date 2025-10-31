package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;

/**
 * 理赔申请对象 biz_reward_claim
 *
 * @author Lion Li
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_reward_claim")
public class BizRewardClaim extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 申请用户ID
     */
    private Long userId;

    /**
     * 申请金额
     */
    private BigDecimal amount;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 状态 (PENDING, APPROVED, REJECTED)
     */
    private String status;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 失败次数
     */
    private Integer lostCount;

    /**
     * 业务编号
     */
    private String bizCode;
}
