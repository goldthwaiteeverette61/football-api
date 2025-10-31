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
import org.dromara.biz.domain.vo.BizUserProgressVo;
import org.dromara.biz.domain.bo.BizUserProgressBo;
import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户跟投进度
 *
 * @author Lion Li
 * @date 2025-08-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/userProgress")
public class BizUserProgressController extends BaseController {

    private final IBizUserProgressService bizUserProgressService;

    /**
     * 查询用户跟投进度列表
     */
    @SaCheckPermission("biz:userProgress:list")
    @GetMapping("/list")
    public TableDataInfo<BizUserProgressVo> list(BizUserProgressBo bo, PageQuery pageQuery) {
        return bizUserProgressService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户跟投进度列表
     */
    @SaCheckPermission("biz:userProgress:export")
    @Log(title = "用户跟投进度", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizUserProgressBo bo, HttpServletResponse response) {
        List<BizUserProgressVo> list = bizUserProgressService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户跟投进度", BizUserProgressVo.class, response);
    }

    /**
     * 获取用户跟投进度详细信息
     *
     * @param progressId 主键
     */
    @SaCheckPermission("biz:userProgress:query")
    @GetMapping("/{progressId}")
    public R<BizUserProgressVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long progressId) {
        return R.ok(bizUserProgressService.queryById(progressId));
    }

    /**
     * 新增用户跟投进度
     */
    @SaCheckPermission("biz:userProgress:add")
    @Log(title = "用户跟投进度", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizUserProgressBo bo) {
        return toAjax(bizUserProgressService.insertByBo(bo));
    }

    /**
     * 修改用户跟投进度
     */
    @SaCheckPermission("biz:userProgress:edit")
    @Log(title = "用户跟投进度", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizUserProgressBo bo) {
        return toAjax(bizUserProgressService.updateByBo(bo));
    }

    /**
     * 删除用户跟投进度
     *
     * @param progressIds 主键串
     */
    @SaCheckPermission("biz:userProgress:remove")
    @Log(title = "用户跟投进度", businessType = BusinessType.DELETE)
    @DeleteMapping("/{progressIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] progressIds) {
        return toAjax(bizUserProgressService.deleteWithValidByIds(List.of(progressIds), true));
    }
}
