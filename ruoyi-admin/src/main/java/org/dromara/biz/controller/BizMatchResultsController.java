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
import org.dromara.biz.domain.vo.BizMatchResultsVo;
import org.dromara.biz.domain.bo.BizMatchResultsBo;
import org.dromara.biz.service.IBizMatchResultsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 比赛赛果
 *
 * @author Lion Li
 * @date 2025-08-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/matchResults")
public class BizMatchResultsController extends BaseController {

    private final IBizMatchResultsService bizMatchResultsService;

//    /**
//     * 查询比赛赛果列表
//     */
//    @SaCheckPermission("biz:matchResults:list")
//    @GetMapping("/list")
//    public TableDataInfo<BizMatchResultsVo> list(BizMatchResultsBo bo, PageQuery pageQuery) {
//        return bizMatchResultsService.queryPageList(bo, pageQuery);
//    }

    /**
     * 导出比赛赛果列表
     */
    @SaCheckPermission("biz:matchResults:export")
    @Log(title = "比赛赛果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizMatchResultsBo bo, HttpServletResponse response) {
        List<BizMatchResultsVo> list = bizMatchResultsService.queryList(bo);
        ExcelUtil.exportExcel(list, "比赛赛果", BizMatchResultsVo.class, response);
    }

    /**
     * 获取比赛赛果详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:matchResults:query")
    @GetMapping("/{id}")
    public R<BizMatchResultsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizMatchResultsService.queryById(id));
    }

    /**
     * 新增比赛赛果
     */
    @SaCheckPermission("biz:matchResults:add")
    @Log(title = "比赛赛果", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizMatchResultsBo bo) {
        return toAjax(bizMatchResultsService.insertByBo(bo));
    }

    /**
     * 修改比赛赛果
     */
    @SaCheckPermission("biz:matchResults:edit")
    @Log(title = "比赛赛果", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizMatchResultsBo bo) {
        return toAjax(bizMatchResultsService.updateByBo(bo));
    }

    /**
     * 删除比赛赛果
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:matchResults:remove")
    @Log(title = "比赛赛果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizMatchResultsService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 查询比赛赛果列表
     */
    @GetMapping("/list")
    public TableDataInfo<BizMatchResultsVo> list(BizMatchResultsBo bo, PageQuery pageQuery) {
        return bizMatchResultsService.queryPageListWithDetails(bo, pageQuery);
    }
}
