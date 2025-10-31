package org.dromara.system.controller;

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
import org.dromara.system.domain.vo.BizProductsSourceVo;
import org.dromara.system.domain.bo.BizProductsSourceBo;
import org.dromara.system.service.IBizProductsSourceService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 商品货源
 *
 * @author Lion Li
 * @date 2025-03-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/productsSource")
public class BizProductsSourceController extends BaseController {

    private final IBizProductsSourceService bizProductsSourceService;

    /**
     * 查询商品货源列表
     */
    @SaCheckPermission("biz:productsSource:list")
    @GetMapping("/list")
    public TableDataInfo<BizProductsSourceVo> list(BizProductsSourceBo bo, PageQuery pageQuery) {
        return bizProductsSourceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品货源列表
     */
    @SaCheckPermission("biz:productsSource:export")
    @Log(title = "商品货源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizProductsSourceBo bo, HttpServletResponse response) {
        List<BizProductsSourceVo> list = bizProductsSourceService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品货源", BizProductsSourceVo.class, response);
    }

    /**
     * 获取商品货源详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:productsSource:query")
    @GetMapping("/{id}")
    public R<BizProductsSourceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizProductsSourceService.queryById(id));
    }

    /**
     * 新增商品货源
     */
    @SaCheckPermission("biz:productsSource:add")
    @Log(title = "商品货源", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizProductsSourceBo bo) {
        return toAjax(bizProductsSourceService.insertByBo(bo));
    }

    /**
     * 修改商品货源
     */
    @SaCheckPermission("biz:productsSource:edit")
    @Log(title = "商品货源", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizProductsSourceBo bo) {
        return toAjax(bizProductsSourceService.updateByBo(bo));
    }

    /**
     * 删除商品货源
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:productsSource:remove")
    @Log(title = "商品货源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizProductsSourceService.deleteWithValidByIds(List.of(ids), true));
    }
}
