package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
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
 * 方案期数
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/schemePeriods")
public class BizSchemePeriodsController extends BaseController {

    private final IBizSchemePeriodsService bizSchemePeriodsService;

    /**
     * 发布方案
     * @param periodId 方案ID
     */
    @SaCheckPermission("biz:schemePeriods:edit") // 复用编辑权限
    @Log(title = "发布方案", businessType = BusinessType.UPDATE)
    @PostMapping("/publish/{periodId}")
    public R<Void> publish(@PathVariable Long periodId) {
        return toAjax(bizSchemePeriodsService.publishPeriod(periodId));
    }

    /**
     * 查询方案期数列表
     */
    @SaCheckPermission("biz:schemePeriods:list")
    @GetMapping("/list-s")
    public TableDataInfo<BizSchemePeriodsVo> listS(BizSchemePeriodsBo bo, PageQuery pageQuery) {
        TableDataInfo<BizSchemePeriodsVo> page =bizSchemePeriodsService.queryPageList(bo, pageQuery);
        return page;
    }

    /**
     * 查询方案期数列表
     */
    @SaCheckPermission("biz:schemePeriods:list")
    @GetMapping("/list")
    public TableDataInfo<BizSchemePeriodsVo> list(BizSchemePeriodsBo bo, PageQuery pageQuery) {
        TableDataInfo<BizSchemePeriodsVo> page =bizSchemePeriodsService.queryPageListFull(bo, pageQuery);
        return page;
    }

    /**
     * 获取方案期数详细信息
     *
     * @param periodId 主键
     */
    @SaCheckPermission("biz:schemePeriods:query")
    @GetMapping("/{periodId}")
    public R<BizSchemePeriodsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long periodId) {
        return R.ok(bizSchemePeriodsService.queryById(periodId));
    }

    /**
     * 新增方案期数
     */
    @SaCheckPermission("biz:schemePeriods:add")
    @Log(title = "方案期数", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizSchemePeriodsBo bo) {
        bo.setStatus("pending");
        return toAjax(bizSchemePeriodsService.insertByBo(bo));
    }

    /**
     * 修改方案期数
     */
    @SaCheckPermission("biz:schemePeriods:edit")
    @Log(title = "方案期数", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizSchemePeriodsBo bo) {
        return toAjax(bizSchemePeriodsService.updateByBo(bo));
    }

    /**
     * 删除方案期数
     *
     * @param periodIds 主键串
     */
    @SaCheckPermission("biz:schemePeriods:remove")
    @Log(title = "方案期数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{periodIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] periodIds) {
        return toAjax(bizSchemePeriodsService.deleteWithValidByIds(List.of(periodIds), true));
    }

}
