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
import org.dromara.biz.domain.vo.BizOddsVo;
import org.dromara.biz.domain.bo.BizOddsBo;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 比赛赔率
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/odds")
public class BizOddsController extends BaseController {

    private final IBizOddsService bizOddsService;

    /**
     * 查询比赛赔率列表
     */
    @SaCheckPermission("biz:odds:list")
    @GetMapping("/list")
    public TableDataInfo<BizOddsVo> list(BizOddsBo bo, PageQuery pageQuery) {
        return bizOddsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出比赛赔率列表
     */
    @SaCheckPermission("biz:odds:export")
    @Log(title = "比赛赔率", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizOddsBo bo, HttpServletResponse response) {
        List<BizOddsVo> list = bizOddsService.queryList(bo);
        ExcelUtil.exportExcel(list, "比赛赔率", BizOddsVo.class, response);
    }

    /**
     * 获取比赛赔率详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:odds:query")
    @GetMapping("/{id}")
    public R<BizOddsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizOddsService.queryById(id));
    }

    /**
     * 新增比赛赔率
     */
    @SaCheckPermission("biz:odds:add")
    @Log(title = "比赛赔率", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizOddsBo bo) {
        return toAjax(bizOddsService.insertByBo(bo));
    }

    /**
     * 修改比赛赔率
     */
    @SaCheckPermission("biz:odds:edit")
    @Log(title = "比赛赔率", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizOddsBo bo) {
        return toAjax(bizOddsService.updateByBo(bo));
    }

    /**
     * 删除比赛赔率
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:odds:remove")
    @Log(title = "比赛赔率", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizOddsService.deleteWithValidByIds(List.of(ids), true));
    }
}
