package org.dromara.biz.controller;

import java.util.List;
import java.util.Map;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.biz.domain.vo.BizMatchesGroupVo;
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
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 比赛信息
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/matches")
public class BizMatchesController extends BaseController {

    private final IBizMatchesService bizMatchesService;

    /**
     * 获取足球计算器比赛列表 (支持按状态和日期筛选)，排除payout
     */
    @GetMapping("/list-nepayout")
    public TableDataInfo<BizMatchesGroupVo> getCalculatorListNePayout(BizMatchesBo bo) {
        bo.setExcludePayout(true);
        List<BizMatchesGroupVo> list = bizMatchesService.queryCalculatorList(bo);
        return TableDataInfo.build(list);
    }

    /**
     * 查询比赛信息列表
     */
    @SaCheckPermission("biz:matches:list")
    @GetMapping("/list")
    public TableDataInfo<BizMatchesVo> list(BizMatchesBo bo, PageQuery pageQuery) {
        return bizMatchesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出比赛信息列表
     */
    @SaCheckPermission("biz:matches:export")
    @Log(title = "比赛信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizMatchesBo bo, HttpServletResponse response) {
        List<BizMatchesVo> list = bizMatchesService.queryList(bo);
        ExcelUtil.exportExcel(list, "比赛信息", BizMatchesVo.class, response);
    }

    /**
     * 获取比赛信息详细信息
     *
     * @param matchId 主键
     */
    @SaCheckPermission("biz:matches:query")
    @GetMapping("/{matchId}")
    public R<BizMatchesVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long matchId) {
        return R.ok(bizMatchesService.queryById(matchId));
    }

    /**
     * 新增比赛信息
     */
    @SaCheckPermission("biz:matches:add")
    @Log(title = "比赛信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizMatchesBo bo) {
        return toAjax(bizMatchesService.insertByBo(bo));
    }

    /**
     * 修改比赛信息
     */
    @SaCheckPermission("biz:matches:edit")
    @Log(title = "比赛信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizMatchesBo bo) {
        return toAjax(bizMatchesService.updateByBo(bo));
    }

    /**
     * 删除比赛信息
     *
     * @param matchIds 主键串
     */
    @SaCheckPermission("biz:matches:remove")
    @Log(title = "比赛信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{matchIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] matchIds) {
        return toAjax(bizMatchesService.deleteWithValidByIds(List.of(matchIds), true));
    }

    /**
     * 获取足球计算器比赛列表 (支持按状态和日期筛选)
     */
    @GetMapping("/calculatorList")
    public TableDataInfo<BizMatchesGroupVo> getCalculatorList(BizMatchesBo bo) {
        List<BizMatchesGroupVo> list = bizMatchesService.queryCalculatorList(bo);
        return TableDataInfo.build(list);
    }

}
