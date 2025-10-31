
// ===================================================================================
// 文件: BizSystemReserve.java (Domain Object)
// 路径: org/dromara/biz/domain/BizSystemReserve.java
// 描述: 数据库实体类，直接映射 biz_system_reserve 表。
// ===================================================================================
package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 系统储备金明细对象 biz_system_reserve
 *
 * @author Dromara
 * @date 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_system_reserve")
public class BizSystemReserve extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 储备金记录ID
     */
    @TableId(value = "reserve_id")
    private Long reserveId;

    /**
     * 来源类型 (例如: scheme_payout_commission)
     */
    private String sourceType;

    /**
     * 来源ID (例如: biz_user_follows表的follow_id)
     */
    private String sourceId;

    /**
     * 关联的用户ID
     */
    private Long userId;

    /**
     * 本次存入的储备金金额 (佣金)
     */
    private BigDecimal amount;

    /**
     * 当时的佣金率 (例如: 0.1000)
     */
    private BigDecimal commissionRate;

    /**
     * 用户原始应得奖金 (扣除佣金前)
     */
    private BigDecimal originalPayout;

    /**
     * 备注
     */
    private String remark;

}
