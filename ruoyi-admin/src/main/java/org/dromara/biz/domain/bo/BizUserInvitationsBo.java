package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizUserInvitations;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 用戶邀請記錄業務對象 biz_user_invitations
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizUserInvitations.class, reverseConvertGenerate = false)
public class BizUserInvitationsBo extends BaseEntity {

    /**
     * 邀請記錄ID, 主鍵
     */
    @NotNull(message = "邀請記錄ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long invitationId;

    /**
     * 邀請人ID (關聯 sys_user)
     */
    @NotNull(message = "邀請人ID (關聯 sys_user)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long inviterId;

    /**
     * 被邀請人ID (新註冊的用戶, 關聯 sys_user)
     */
    @NotNull(message = "被邀請人ID (新註冊的用戶, 關聯 sys_user)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long inviteeId;

    /**
     * 註冊時使用的邀請碼
     */
    @NotBlank(message = "註冊時使用的邀請碼不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String invitationCodeUsed;

    /**
     * 邀請狀態 (例如: registered-已註冊, rewarded-已獎勵)
     */
    private String status;


}
