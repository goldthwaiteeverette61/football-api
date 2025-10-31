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
import org.dromara.system.domain.vo.BizProductImagesVo;
import org.dromara.system.domain.bo.BizProductImagesBo;
import org.dromara.system.service.IBizProductImagesService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 商品图片
 *
 * @author Lion Li
 * @date 2025-02-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/productImages")
public class BizProductImagesController extends BaseController {

    private final IBizProductImagesService bizProductImagesService;

    /**
     * 查询商品图片列表
     */
    @SaCheckPermission("biz:productImages:list")
    @GetMapping("/list")
    public TableDataInfo<BizProductImagesVo> list(BizProductImagesBo bo, PageQuery pageQuery) {
        return bizProductImagesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品图片列表
     */
    @SaCheckPermission("biz:productImages:export")
    @Log(title = "商品图片", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizProductImagesBo bo, HttpServletResponse response) {
        List<BizProductImagesVo> list = bizProductImagesService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品图片", BizProductImagesVo.class, response);
    }

    /**
     * 获取商品图片详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:productImages:query")
    @GetMapping("/{id}")
    public R<BizProductImagesVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizProductImagesService.queryById(id));
    }

    /**
     * 新增商品图片
     */
    @SaCheckPermission("biz:productImages:add")
    @Log(title = "商品图片", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizProductImagesBo bo) {
        return toAjax(bizProductImagesService.insertByBo(bo));
    }

    /**
     * 修改商品图片
     */
    @SaCheckPermission("biz:productImages:edit")
    @Log(title = "商品图片", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizProductImagesBo bo) {
        return toAjax(bizProductImagesService.updateByBo(bo));
    }

    /**
     * 删除商品图片
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:productImages:remove")
    @Log(title = "商品图片", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizProductImagesService.deleteWithValidByIds(List.of(ids), true));
    }
}
