package org.dromara.biz.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.biz.domain.vo.BizUserInvitationsVo;
import org.dromara.biz.domain.bo.BizUserInvitationsBo;
import org.dromara.biz.service.IBizUserInvitationsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户邀请记录
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/userInvitations")
public class BizUserInvitationsController extends BaseController {

    private final IBizUserInvitationsService bizUserInvitationsService;

    /**
     * 查询用户邀请记录列表
     */
    @SaCheckPermission("biz:userInvitations:list")
    @GetMapping("/list")
    public TableDataInfo<BizUserInvitationsVo> list(BizUserInvitationsBo bo, PageQuery pageQuery) {
        return bizUserInvitationsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户邀请记录列表
     */
    @SaCheckPermission("biz:userInvitations:export")
    @Log(title = "用户邀请记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizUserInvitationsBo bo, HttpServletResponse response) {
        List<BizUserInvitationsVo> list = bizUserInvitationsService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户邀请记录", BizUserInvitationsVo.class, response);
    }

    /**
     * 获取用户邀请记录详细信息
     *
     * @param invitationId 主键
     */
    @SaCheckPermission("biz:userInvitations:query")
    @GetMapping("/{invitationId}")
    public R<BizUserInvitationsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long invitationId) {
        return R.ok(bizUserInvitationsService.queryById(invitationId));
    }

    /**
     * 新增用户邀请记录
     */
    @SaCheckPermission("biz:userInvitations:add")
    @Log(title = "用户邀请记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizUserInvitationsBo bo) {
        return toAjax(bizUserInvitationsService.insertByBo(bo));
    }

    /**
     * 修改用户邀请记录
     */
    @SaCheckPermission("biz:userInvitations:edit")
    @Log(title = "用户邀请记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizUserInvitationsBo bo) {
        return toAjax(bizUserInvitationsService.updateByBo(bo));
    }

    /**
     * 删除用户邀请记录
     *
     * @param invitationIds 主键串
     */
    @SaCheckPermission("biz:userInvitations:remove")
    @Log(title = "用户邀请记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{invitationIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] invitationIds) {
        return toAjax(bizUserInvitationsService.deleteWithValidByIds(List.of(invitationIds), true));
    }
}
