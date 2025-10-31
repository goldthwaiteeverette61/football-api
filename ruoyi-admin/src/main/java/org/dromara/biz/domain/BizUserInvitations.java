package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 用户邀请记录对象 biz_user_invitations
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_invitations")
public class BizUserInvitations extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邀请记录ID, 主键
     */
    @TableId(value = "invitation_id")
    private Long invitationId;

    /**
     * 邀请人ID (关联 sys_user)
     */
    private Long inviterId;

    /**
     * 被邀请人ID (新注册的用户, 关联 sys_user)
     */
    private Long inviteeId;

    /**
     * 注册时使用的邀请码
     */
    private String invitationCodeUsed;

    /**
     * 邀请状态 (例如: registered-已注册, rewarded-已奖励)
     */
    private String status;


}
