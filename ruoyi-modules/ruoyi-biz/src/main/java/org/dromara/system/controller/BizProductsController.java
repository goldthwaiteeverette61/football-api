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
import org.dromara.system.domain.vo.BizProductsVo;
import org.dromara.system.domain.bo.BizProductsBo;
import org.dromara.system.service.IBizProductsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 商品信息
 *
 * @author Lion Li
 * @date 2025-02-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/products")
public class BizProductsController extends BaseController {

    private final IBizProductsService bizProductsService;

    /**
     * 查询商品信息列表
     */
    @SaCheckPermission("biz:products:list")
    @GetMapping("/list")
    public TableDataInfo<BizProductsVo> list(BizProductsBo bo, PageQuery pageQuery) {
        return bizProductsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品信息列表
     */
    @SaCheckPermission("biz:products:export")
    @Log(title = "商品信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizProductsBo bo, HttpServletResponse response) {
        List<BizProductsVo> list = bizProductsService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品信息", BizProductsVo.class, response);
    }

    /**
     * 获取商品信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:products:query")
    @GetMapping("/{id}")
    public R<BizProductsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizProductsService.queryById(id));
    }

    /**
     * 新增商品信息
     */
    @SaCheckPermission("biz:products:add")
    @Log(title = "商品信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizProductsBo bo) {
        return toAjax(bizProductsService.insertByBo(bo));
    }

    /**
     * 修改商品信息
     */
    @SaCheckPermission("biz:products:edit")
    @Log(title = "商品信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizProductsBo bo) {
        return toAjax(bizProductsService.updateByBo(bo));
    }

    /**
     * 删除商品信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:products:remove")
    @Log(title = "商品信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizProductsService.deleteWithValidByIds(List.of(ids), true));
    }


//    @SaCheckPermission("biz:products:remove")
//    @Log(title = "根据id复制", businessType = BusinessType.DELETE)
    @GetMapping("/{id}/copy")
    public R<Void> copy(@PathVariable Long id,String imageType,String target_language) {
        return toAjax(bizProductsService.copyById(id,imageType, target_language));
    }

    //处理zip
    @GetMapping("/{id}/handlerZip")
    public R<Void> handlerZip(@PathVariable Long id) throws Exception {
        return toAjax(bizProductsService.handlerZip(id));
    }



}
