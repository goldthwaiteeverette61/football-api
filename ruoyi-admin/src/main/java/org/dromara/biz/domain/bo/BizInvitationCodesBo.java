package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizInvitationCodes;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 預生成邀請碼池業務對象 biz_invitation_codes
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizInvitationCodes.class, reverseConvertGenerate = false)
public class BizInvitationCodesBo extends BaseEntity {

    /**
     * 邀請碼ID, 主鍵
     */
    @NotNull(message = "邀請碼ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long codeId;

    /**
     * 唯一的邀請碼
     */
    @NotBlank(message = "唯一的邀請碼不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String invitationCode;

    /**
     * 狀態 (available-可用, assigned-已分配)
     */
    @NotBlank(message = "狀態 (available-可用, assigned-已分配)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 被分配的用戶ID (關聯 sys_user)
     */
    private Long assigneeUserId;

    /**
     * 分配時間
     */
    private Date assignTime;


}
