package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 预生成邀请码池对象 biz_invitation_codes
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_invitation_codes")
public class BizInvitationCodes extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邀请码ID, 主键
     */
    @TableId(value = "code_id")
    private Long codeId;

    /**
     * 唯一的邀请码
     */
    private String invitationCode;

    /**
     * 状态 (available-可用, assigned-已分配)
     */
    private String status;

    /**
     * 被分配的用户ID (关联 sys_user)
     */
    private Long assigneeUserId;

    /**
     * 分配时间
     */
    private Date assignTime;


}
