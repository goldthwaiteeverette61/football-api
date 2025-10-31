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
import org.dromara.biz.domain.vo.BizSportsVo;
import org.dromara.biz.domain.bo.BizSportsBo;
import org.dromara.biz.service.IBizSportsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 体育项目
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/sports")
public class BizSportsController extends BaseController {

    private final IBizSportsService bizSportsService;

    /**
     * 查询体育项目列表
     */
    @SaCheckPermission("biz:sports:list")
    @GetMapping("/list")
    public TableDataInfo<BizSportsVo> list(BizSportsBo bo, PageQuery pageQuery) {
        return bizSportsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出体育项目列表
     */
    @SaCheckPermission("biz:sports:export")
    @Log(title = "体育项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizSportsBo bo, HttpServletResponse response) {
        List<BizSportsVo> list = bizSportsService.queryList(bo);
        ExcelUtil.exportExcel(list, "体育项目", BizSportsVo.class, response);
    }

    /**
     * 获取体育项目详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:sports:query")
    @GetMapping("/{id}")
    public R<BizSportsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizSportsService.queryById(id));
    }

    /**
     * 新增体育项目
     */
    @SaCheckPermission("biz:sports:add")
    @Log(title = "体育项目", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizSportsBo bo) {
        return toAjax(bizSportsService.insertByBo(bo));
    }

    /**
     * 修改体育项目
     */
    @SaCheckPermission("biz:sports:edit")
    @Log(title = "体育项目", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizSportsBo bo) {
        return toAjax(bizSportsService.updateByBo(bo));
    }

    /**
     * 删除体育项目
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:sports:remove")
    @Log(title = "体育项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizSportsService.deleteWithValidByIds(List.of(ids), true));
    }
}
