package org.dromara.biz.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.biz.domain.BizInvitationCodes;
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
 * 预生成邀请码池视图对象 biz_invitation_codes
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizInvitationCodes.class)
public class BizInvitationCodesVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邀请码ID, 主键
     */
    @ExcelProperty(value = "邀请码ID, 主键")
    private Long codeId;

    /**
     * 唯一的邀请码
     */
    @ExcelProperty(value = "唯一的邀请码")
    private String invitationCode;

    /**
     * 状态 (available-可用, assigned-已分配)
     */
    @ExcelProperty(value = "状态 (available-可用, assigned-已分配)")
    private String status;

    /**
     * 被分配的用户ID (关联 sys_user)
     */
    @ExcelProperty(value = "被分配的用户ID (关联 sys_user)")
    private Long assigneeUserId;

    /**
     * 分配时间
     */
    @ExcelProperty(value = "分配时间")
    private Date assignTime;


}
