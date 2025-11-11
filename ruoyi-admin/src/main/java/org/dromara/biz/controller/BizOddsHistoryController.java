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
import org.dromara.biz.domain.vo.BizOddsHistoryVo;
import org.dromara.biz.domain.bo.BizOddsHistoryBo;
import org.dromara.biz.service.IBizOddsHistoryService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 比赛赔率历史
 *
 * @author Lion Li
 * @date 2025-11-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/oddsHistory")
public class BizOddsHistoryController extends BaseController {

    private final IBizOddsHistoryService bizOddsHistoryService;

    /**
     * 查询比赛赔率历史列表
     */
    @SaCheckPermission("biz:oddsHistory:list")
    @GetMapping("/list")
    public TableDataInfo<BizOddsHistoryVo> list(BizOddsHistoryBo bo, PageQuery pageQuery) {
        return bizOddsHistoryService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出比赛赔率历史列表
     */
    @SaCheckPermission("biz:oddsHistory:export")
    @Log(title = "比赛赔率历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizOddsHistoryBo bo, HttpServletResponse response) {
        List<BizOddsHistoryVo> list = bizOddsHistoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "比赛赔率历史", BizOddsHistoryVo.class, response);
    }

    /**
     * 获取比赛赔率历史详细信息
     *
     * @param historyId 主键
     */
    @SaCheckPermission("biz:oddsHistory:query")
    @GetMapping("/{historyId}")
    public R<BizOddsHistoryVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long historyId) {
        return R.ok(bizOddsHistoryService.queryById(historyId));
    }

    /**
     * 新增比赛赔率历史
     */
    @SaCheckPermission("biz:oddsHistory:add")
    @Log(title = "比赛赔率历史", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizOddsHistoryBo bo) {
        return toAjax(bizOddsHistoryService.insertByBo(bo));
    }

    /**
     * 修改比赛赔率历史
     */
    @SaCheckPermission("biz:oddsHistory:edit")
    @Log(title = "比赛赔率历史", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizOddsHistoryBo bo) {
        return toAjax(bizOddsHistoryService.updateByBo(bo));
    }

    /**
     * 删除比赛赔率历史
     *
     * @param historyIds 主键串
     */
    @SaCheckPermission("biz:oddsHistory:remove")
    @Log(title = "比赛赔率历史", businessType = BusinessType.DELETE)
    @DeleteMapping("/{historyIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] historyIds) {
        return toAjax(bizOddsHistoryService.deleteWithValidByIds(List.of(historyIds), true));
    }
}
