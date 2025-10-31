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
import org.dromara.biz.domain.vo.BizSystemReserveSummaryVo;
import org.dromara.biz.domain.bo.BizSystemReserveSummaryBo;
import org.dromara.biz.service.IBizSystemReserveSummaryService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 系统储备金汇总
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/systemReserveSummary")
public class BizSystemReserveSummaryController extends BaseController {

    private final IBizSystemReserveSummaryService bizSystemReserveSummaryService;

    /**
     * 查询系统储备金汇总列表
     */
    @SaCheckPermission("biz:systemReserveSummary:list")
    @GetMapping("/list")
    public TableDataInfo<BizSystemReserveSummaryVo> list(BizSystemReserveSummaryBo bo, PageQuery pageQuery) {
        return bizSystemReserveSummaryService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出系统储备金汇总列表
     */
    @SaCheckPermission("biz:systemReserveSummary:export")
    @Log(title = "系统储备金汇总", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizSystemReserveSummaryBo bo, HttpServletResponse response) {
        List<BizSystemReserveSummaryVo> list = bizSystemReserveSummaryService.queryList(bo);
        ExcelUtil.exportExcel(list, "系统储备金汇总", BizSystemReserveSummaryVo.class, response);
    }

    /**
     * 获取系统储备金汇总详细信息
     *
     * @param summaryId 主键
     */
    @SaCheckPermission("biz:systemReserveSummary:query")
    @GetMapping("/{summaryId}")
    public R<BizSystemReserveSummaryVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Integer summaryId) {
        return R.ok(bizSystemReserveSummaryService.queryById(summaryId));
    }

    /**
     * 新增系统储备金汇总
     */
    @SaCheckPermission("biz:systemReserveSummary:add")
    @Log(title = "系统储备金汇总", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizSystemReserveSummaryBo bo) {
        return toAjax(bizSystemReserveSummaryService.insertByBo(bo));
    }

    /**
     * 修改系统储备金汇总
     */
    @SaCheckPermission("biz:systemReserveSummary:edit")
    @Log(title = "系统储备金汇总", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizSystemReserveSummaryBo bo) {
        return toAjax(bizSystemReserveSummaryService.updateByBo(bo));
    }

    /**
     * 删除系统储备金汇总
     *
     * @param summaryIds 主键串
     */
    @SaCheckPermission("biz:systemReserveSummary:remove")
    @Log(title = "系统储备金汇总", businessType = BusinessType.DELETE)
    @DeleteMapping("/{summaryIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Integer[] summaryIds) {
        return toAjax(bizSystemReserveSummaryService.deleteWithValidByIds(List.of(summaryIds), true));
    }
}
