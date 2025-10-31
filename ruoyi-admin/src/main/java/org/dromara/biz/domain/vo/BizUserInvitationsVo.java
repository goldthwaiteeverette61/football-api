package org.dromara.biz.domain.vo;

import org.dromara.biz.domain.BizUserInvitations;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 用户邀请记录视图对象 biz_user_invitations
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizUserInvitations.class)
public class BizUserInvitationsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邀请记录ID, 主键
     */
    @ExcelProperty(value = "邀请记录ID, 主键")
    private Long invitationId;

    /**
     * 邀请人ID (关联 sys_user)
     */
    @ExcelProperty(value = "邀请人ID (关联 sys_user)")
    private Long inviterId;

    /**
     * 被邀请人ID (新注册的用户, 关联 sys_user)
     */
    @ExcelProperty(value = "被邀请人ID (新注册的用户, 关联 sys_user)")
    private Long inviteeId;

    /**
     * 注册时使用的邀请码
     */
    @ExcelProperty(value = "注册时使用的邀请码")
    private String invitationCodeUsed;

    /**
     * 邀请状态 (例如: registered-已注册, rewarded-已奖励)
     */
    @ExcelProperty(value = "邀请状态 (例如: registered-已注册, rewarded-已奖励)")
    private String status;


}
