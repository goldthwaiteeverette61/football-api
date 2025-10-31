package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizAppVersionsBo;
import org.dromara.biz.domain.vo.BizAppVersionsVo;
import org.dromara.biz.service.IBizAppVersionsService;
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
 * 应用版本信息
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/appVersions")
public class BizAppVersionsController extends BaseController {

    private final IBizAppVersionsService bizAppVersionsService;

    /**
     * 查询应用版本信息列表
     */
    @SaCheckPermission("biz:appVersions:list")
    @GetMapping("/list")
    public TableDataInfo<BizAppVersionsVo> list(BizAppVersionsBo bo, PageQuery pageQuery) {
        return bizAppVersionsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出应用版本信息列表
     */
    @SaCheckPermission("biz:appVersions:export")
    @Log(title = "应用版本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizAppVersionsBo bo, HttpServletResponse response) {
        List<BizAppVersionsVo> list = bizAppVersionsService.queryList(bo);
        ExcelUtil.exportExcel(list, "应用版本信息", BizAppVersionsVo.class, response);
    }

    /**
     * 获取应用版本信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:appVersions:query")
    @GetMapping("/{id}")
    public R<BizAppVersionsVo> getInfo(@NotNull(message = "主键不能为空")
                                       @PathVariable Long id) {
        return R.ok(bizAppVersionsService.queryById(id));
    }

    /**
     * 新增应用版本信息
     */
    @SaCheckPermission("biz:appVersions:add")
    @Log(title = "应用版本信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizAppVersionsBo bo) {
        return toAjax(bizAppVersionsService.insertByBo(bo));
    }

    /**
     * 修改应用版本信息
     */
    @SaCheckPermission("biz:appVersions:edit")
    @Log(title = "应用版本信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizAppVersionsBo bo) {
        return toAjax(bizAppVersionsService.updateByBo(bo));
    }

    /**
     * 删除应用版本信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:appVersions:remove")
    @Log(title = "应用版本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizAppVersionsService.deleteWithValidByIds(List.of(ids), true));
    }
}
