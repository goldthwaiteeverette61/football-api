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
import org.dromara.biz.domain.vo.BizDeviceUpdatesVo;
import org.dromara.biz.domain.bo.BizDeviceUpdatesBo;
import org.dromara.biz.service.IBizDeviceUpdatesService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备更新记录
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/deviceUpdates")
public class BizDeviceUpdatesController extends BaseController {

    private final IBizDeviceUpdatesService bizDeviceUpdatesService;

    /**
     * 查询设备更新记录列表
     */
    @SaCheckPermission("biz:deviceUpdates:list")
    @GetMapping("/list")
    public TableDataInfo<BizDeviceUpdatesVo> list(BizDeviceUpdatesBo bo, PageQuery pageQuery) {
        return bizDeviceUpdatesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备更新记录列表
     */
    @SaCheckPermission("biz:deviceUpdates:export")
    @Log(title = "设备更新记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizDeviceUpdatesBo bo, HttpServletResponse response) {
        List<BizDeviceUpdatesVo> list = bizDeviceUpdatesService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备更新记录", BizDeviceUpdatesVo.class, response);
    }

    /**
     * 获取设备更新记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:deviceUpdates:query")
    @GetMapping("/{id}")
    public R<BizDeviceUpdatesVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Integer id) {
        return R.ok(bizDeviceUpdatesService.queryById(id));
    }

    /**
     * 新增设备更新记录
     */
    @SaCheckPermission("biz:deviceUpdates:add")
    @Log(title = "设备更新记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizDeviceUpdatesBo bo) {
        return toAjax(bizDeviceUpdatesService.insertByBo(bo));
    }

    /**
     * 修改设备更新记录
     */
    @SaCheckPermission("biz:deviceUpdates:edit")
    @Log(title = "设备更新记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizDeviceUpdatesBo bo) {
        return toAjax(bizDeviceUpdatesService.updateByBo(bo));
    }

    /**
     * 删除设备更新记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:deviceUpdates:remove")
    @Log(title = "设备更新记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Integer[] ids) {
        return toAjax(bizDeviceUpdatesService.deleteWithValidByIds(List.of(ids), true));
    }
}
