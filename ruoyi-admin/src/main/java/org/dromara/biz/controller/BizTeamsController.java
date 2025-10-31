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
import org.dromara.biz.domain.vo.BizTeamsVo;
import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.service.IBizTeamsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 球队信息
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/teams")
public class BizTeamsController extends BaseController {

    private final IBizTeamsService bizTeamsService;

    /**
     * 查询球队信息列表
     */
    @SaCheckPermission("biz:teams:list")
    @GetMapping("/list")
    public TableDataInfo<BizTeamsVo> list(BizTeamsBo bo, PageQuery pageQuery) {
        return bizTeamsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出球队信息列表
     */
    @SaCheckPermission("biz:teams:export")
    @Log(title = "球队信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizTeamsBo bo, HttpServletResponse response) {
        List<BizTeamsVo> list = bizTeamsService.queryList(bo);
        ExcelUtil.exportExcel(list, "球队信息", BizTeamsVo.class, response);
    }

    /**
     * 获取球队信息详细信息
     *
     * @param teamId 主键
     */
    @SaCheckPermission("biz:teams:query")
    @GetMapping("/{teamId}")
    public R<BizTeamsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long teamId) {
        return R.ok(bizTeamsService.queryById(teamId));
    }

    /**
     * 新增球队信息
     */
    @SaCheckPermission("biz:teams:add")
    @Log(title = "球队信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizTeamsBo bo) {
        return toAjax(bizTeamsService.insertByBo(bo));
    }

    /**
     * 修改球队信息
     */
    @SaCheckPermission("biz:teams:edit")
    @Log(title = "球队信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizTeamsBo bo) {
        return toAjax(bizTeamsService.updateByBo(bo));
    }

    /**
     * 删除球队信息
     *
     * @param teamIds 主键串
     */
    @SaCheckPermission("biz:teams:remove")
    @Log(title = "球队信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{teamIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] teamIds) {
        return toAjax(bizTeamsService.deleteWithValidByIds(List.of(teamIds), true));
    }
}
