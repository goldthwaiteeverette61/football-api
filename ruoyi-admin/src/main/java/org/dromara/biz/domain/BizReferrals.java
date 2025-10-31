package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 好友推荐关系对象 biz_referrals
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_referrals")
public class BizReferrals extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 推荐人ID
     */
    private Long referrerUserId;

    /**
     * 被推荐人ID
     */
    private Long referredUserId;

    /**
     * 状态(例如被推荐人完成首次存款或投注后变为completed)
     */
    private String status;

    /**
     * 是否已发放奖励
     */
    private Long bonusAwarded;

    /**
     * 
     */
    private Date createdAt;


}
