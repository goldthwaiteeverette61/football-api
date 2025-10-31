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
import org.dromara.biz.domain.vo.BizLeaguesVo;
import org.dromara.biz.domain.bo.BizLeaguesBo;
import org.dromara.biz.service.IBizLeaguesService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 联赛信息
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/leagues")
public class BizLeaguesController extends BaseController {

    private final IBizLeaguesService bizLeaguesService;

    /**
     * 查询联赛信息列表
     */
    @SaCheckPermission("biz:leagues:list")
    @GetMapping("/list")
    public TableDataInfo<BizLeaguesVo> list(BizLeaguesBo bo, PageQuery pageQuery) {
        return bizLeaguesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出联赛信息列表
     */
    @SaCheckPermission("biz:leagues:export")
    @Log(title = "联赛信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizLeaguesBo bo, HttpServletResponse response) {
        List<BizLeaguesVo> list = bizLeaguesService.queryList(bo);
        ExcelUtil.exportExcel(list, "联赛信息", BizLeaguesVo.class, response);
    }

    /**
     * 获取联赛信息详细信息
     *
     * @param leagueId 主键
     */
    @SaCheckPermission("biz:leagues:query")
    @GetMapping("/{leagueId}")
    public R<BizLeaguesVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String leagueId) {
        return R.ok(bizLeaguesService.queryById(leagueId));
    }

    /**
     * 新增联赛信息
     */
    @SaCheckPermission("biz:leagues:add")
    @Log(title = "联赛信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizLeaguesBo bo) {
        return toAjax(bizLeaguesService.insertByBo(bo));
    }

    /**
     * 修改联赛信息
     */
    @SaCheckPermission("biz:leagues:edit")
    @Log(title = "联赛信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizLeaguesBo bo) {
        return toAjax(bizLeaguesService.updateByBo(bo));
    }

    /**
     * 删除联赛信息
     *
     * @param leagueIds 主键串
     */
    @SaCheckPermission("biz:leagues:remove")
    @Log(title = "联赛信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{leagueIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] leagueIds) {
        return toAjax(bizLeaguesService.deleteWithValidByIds(List.of(leagueIds), true));
    }
}
