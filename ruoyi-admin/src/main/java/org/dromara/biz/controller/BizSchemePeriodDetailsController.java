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
import org.dromara.biz.domain.vo.BizSchemePeriodDetailsVo;
import org.dromara.biz.domain.bo.BizSchemePeriodDetailsBo;
import org.dromara.biz.service.IBizSchemePeriodDetailsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 方案期数详情
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/schemePeriodDetails")
public class BizSchemePeriodDetailsController extends BaseController {

    private final IBizSchemePeriodDetailsService bizSchemePeriodDetailsService;

    /**
     * 核心接口：批量修改方案详情（赔率）
     */
    @SaCheckPermission("biz:schemePeriodDetails:edit")
    @Log(title = "方案详情", businessType = BusinessType.UPDATE)
    @PutMapping("/batch")
    public R<Void> updateBatch(@RequestBody List<BizSchemePeriodDetailsBo> boList) {
        bizSchemePeriodDetailsService.updateBatch(boList);
        return R.ok("赔率更新成功");
    }

    /**
     * 查询方案期数详情列表
     */
    @SaCheckPermission("biz:schemePeriodDetails:list")
    @GetMapping("/list")
    public TableDataInfo<BizSchemePeriodDetailsVo> list(BizSchemePeriodDetailsBo bo, PageQuery pageQuery) {
        return bizSchemePeriodDetailsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出方案期数详情列表
     */
    @SaCheckPermission("biz:schemePeriodDetails:export")
    @Log(title = "方案期数详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizSchemePeriodDetailsBo bo, HttpServletResponse response) {
        List<BizSchemePeriodDetailsVo> list = bizSchemePeriodDetailsService.queryList(bo);
        ExcelUtil.exportExcel(list, "方案期数详情", BizSchemePeriodDetailsVo.class, response);
    }

    /**
     * 获取方案期数详情详细信息
     *
     * @param detailId 主键
     */
    @SaCheckPermission("biz:schemePeriodDetails:query")
    @GetMapping("/{detailId}")
    public R<BizSchemePeriodDetailsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long detailId) {
        return R.ok(bizSchemePeriodDetailsService.queryById(detailId));
    }

    /**
     * 新增方案期数详情
     */
    @SaCheckPermission("biz:schemePeriodDetails:add")
    @Log(title = "方案期数详情", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizSchemePeriodDetailsBo bo) {
        return toAjax(bizSchemePeriodDetailsService.insertByBo(bo));
    }

    /**
     * 修改方案期数详情
     */
    @SaCheckPermission("biz:schemePeriodDetails:edit")
    @Log(title = "方案期数详情", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizSchemePeriodDetailsBo bo) {
        return toAjax(bizSchemePeriodDetailsService.updateByBo(bo));
    }

    /**
     * 删除方案期数详情
     *
     * @param detailIds 主键串
     */
    @SaCheckPermission("biz:schemePeriodDetails:remove")
    @Log(title = "方案期数详情", businessType = BusinessType.DELETE)
    @DeleteMapping("/{detailIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] detailIds) {
        return toAjax(bizSchemePeriodDetailsService.deleteWithValidByIds(List.of(detailIds), true));
    }
}
