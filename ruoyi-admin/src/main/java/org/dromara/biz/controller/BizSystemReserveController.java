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
import org.dromara.biz.domain.vo.BizSystemReserveVo;
import org.dromara.biz.domain.bo.BizSystemReserveBo;
import org.dromara.biz.service.IBizSystemReserveService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 系统储备金明细
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/systemReserve")
public class BizSystemReserveController extends BaseController {

    private final IBizSystemReserveService bizSystemReserveService;

    /**
     * 查询系统储备金明细列表
     */
    @SaCheckPermission("biz:systemReserve:list")
    @GetMapping("/list")
    public TableDataInfo<BizSystemReserveVo> list(BizSystemReserveBo bo, PageQuery pageQuery) {
        return bizSystemReserveService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出系统储备金明细列表
     */
    @SaCheckPermission("biz:systemReserve:export")
    @Log(title = "系统储备金明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizSystemReserveBo bo, HttpServletResponse response) {
        List<BizSystemReserveVo> list = bizSystemReserveService.queryList(bo);
        ExcelUtil.exportExcel(list, "系统储备金明细", BizSystemReserveVo.class, response);
    }

    /**
     * 获取系统储备金明细详细信息
     *
     * @param reserveId 主键
     */
    @SaCheckPermission("biz:systemReserve:query")
    @GetMapping("/{reserveId}")
    public R<BizSystemReserveVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long reserveId) {
        return R.ok(bizSystemReserveService.queryById(reserveId));
    }

    /**
     * 新增系统储备金明细
     */
    @SaCheckPermission("biz:systemReserve:add")
    @Log(title = "系统储备金明细", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizSystemReserveBo bo) {
        return toAjax(bizSystemReserveService.insertByBo(bo));
    }

    /**
     * 修改系统储备金明细
     */
    @SaCheckPermission("biz:systemReserve:edit")
    @Log(title = "系统储备金明细", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizSystemReserveBo bo) {
        return toAjax(bizSystemReserveService.updateByBo(bo));
    }

    /**
     * 删除系统储备金明细
     *
     * @param reserveIds 主键串
     */
    @SaCheckPermission("biz:systemReserve:remove")
    @Log(title = "系统储备金明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reserveIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] reserveIds) {
        return toAjax(bizSystemReserveService.deleteWithValidByIds(List.of(reserveIds), true));
    }
}
