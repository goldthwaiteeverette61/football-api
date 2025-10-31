package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizUserFollowDetailsBo;
import org.dromara.biz.domain.bo.UserFollowDetailsSaveBo;
import org.dromara.biz.domain.vo.BizUserFollowDetailsVo;
import org.dromara.biz.service.IBizUserFollowDetailsService;
import org.dromara.biz.service.IBizUserFollowsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户跟投详情
 *
 * @author Lion Li
 * @date 2025-08-25
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/userFollowDetails")
public class BizUserFollowDetailsController extends BaseController {

    private final IBizUserFollowDetailsService bizUserFollowDetailsService;
    private final IBizUserFollowsService iBizUserFollowsService;

    /**
     * 核心接口：保存跟投快照
     */
    @SaCheckPermission("biz:userFollows:edit") // 权限跟随主表
    @Log(title = "保存跟投详情快照", businessType = BusinessType.INSERT)
    @PostMapping("/save")
    public R<Void> saveFollowDetails(@RequestBody UserFollowDetailsSaveBo bo) {
        iBizUserFollowsService.saveFollowDetails(bo);
        return R.ok("跟投快照保存成功");
    }

    /**
     * 查询用户跟投详情列表
     */
    @SaCheckPermission("biz:userFollowDetails:list")
    @GetMapping("/list")
    public TableDataInfo<BizUserFollowDetailsVo> list(BizUserFollowDetailsBo bo, PageQuery pageQuery) {
        return bizUserFollowDetailsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户跟投详情列表
     */
    @SaCheckPermission("biz:userFollowDetails:export")
    @Log(title = "用户跟投详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizUserFollowDetailsBo bo, HttpServletResponse response) {
        List<BizUserFollowDetailsVo> list = bizUserFollowDetailsService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户跟投详情", BizUserFollowDetailsVo.class, response);
    }

    /**
     * 获取用户跟投详情详细信息
     *
     * @param followDetailId 主键
     */
    @SaCheckPermission("biz:userFollowDetails:query")
    @GetMapping("/{followDetailId}")
    public R<BizUserFollowDetailsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long followDetailId) {
        return R.ok(bizUserFollowDetailsService.queryById(followDetailId));
    }

    /**
     * 新增用户跟投详情
     */
    @SaCheckPermission("biz:userFollowDetails:add")
    @Log(title = "用户跟投详情", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizUserFollowDetailsBo bo) {
        return toAjax(bizUserFollowDetailsService.insertByBo(bo));
    }

    /**
     * 修改用户跟投详情
     */
    @SaCheckPermission("biz:userFollowDetails:edit")
    @Log(title = "用户跟投详情", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizUserFollowDetailsBo bo) {
        return toAjax(bizUserFollowDetailsService.updateByBo(bo));
    }

    /**
     * 删除用户跟投详情
     *
     * @param followDetailIds 主键串
     */
    @SaCheckPermission("biz:userFollowDetails:remove")
    @Log(title = "用户跟投详情", businessType = BusinessType.DELETE)
    @DeleteMapping("/{followDetailIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] followDetailIds) {
        return toAjax(bizUserFollowDetailsService.deleteWithValidByIds(List.of(followDetailIds), true));
    }
}
