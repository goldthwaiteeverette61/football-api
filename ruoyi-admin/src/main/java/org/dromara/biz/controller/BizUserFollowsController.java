package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BatchFailBo;
import org.dromara.biz.domain.bo.BizUserFollowsBo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.domain.vo.BizUserFollowsVo;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.biz.service.IBizUserFollowsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户跟投记录
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/userFollows")
public class BizUserFollowsController extends BaseController {

    private final IBizUserFollowsService bizUserFollowsService;
    private final IBizSchemePeriodsService iBizSchemePeriodsService;

    /**
     * 【核心新增】批量标记跟投为失败
     */
    @SaCheckPermission("biz:userFollows:edit")
    @Log(title = "用户跟投记录", businessType = BusinessType.UPDATE)
    @PutMapping("/batch-fail")
    public R<Void> batchFail(@RequestBody BatchFailBo bo) {
        bizUserFollowsService.batchFail(bo);
        return R.ok("操作成功");
    }

    /**
     * 核心修改：新增的批量确认投注接口
     */
    @PutMapping("/batch-confirm")
    public R<Void> batchConfirmFollows(@RequestBody List<Long> followIds) {
        bizUserFollowsService.batchConfirmFollows(followIds);
        return R.ok("投注确认成功");
    }

    /**
     * 查询用户跟投记录列表
     */
    @SaCheckPermission("biz:userFollows:list")
    @GetMapping("/list")
    public TableDataInfo<BizUserFollowsVo> list(BizUserFollowsBo bo, PageQuery pageQuery) {
        TableDataInfo<BizUserFollowsVo> bizUserFollowsVoTableDataInfo = bizUserFollowsService.queryPageList(bo, pageQuery);
        for (BizUserFollowsVo vo : bizUserFollowsVoTableDataInfo.getRows()) {
            BizSchemePeriodsVo bizSchemePeriods = iBizSchemePeriodsService.queryById(vo.getPeriodId());
            if(bizSchemePeriods != null)
                vo.setPeriodName(bizSchemePeriods.getName());
        }
        return bizUserFollowsVoTableDataInfo;
    }

    /**
     * 获取用户跟投记录详细信息
     *
     * @param followId 主键
     */
    @SaCheckPermission("biz:userFollows:query")
    @GetMapping("/{followId}")
    public R<BizUserFollowsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long followId) {
        return R.ok(bizUserFollowsService.queryById(followId));
    }

    /**
     * 新增用户跟投记录
     */
    @SaCheckPermission("biz:userFollows:add")
    @Log(title = "用户跟投记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizUserFollowsBo bo) {
        return toAjax(bizUserFollowsService.insertByBo(bo));
    }

    /**
     * 修改用户跟投记录
     */
    @SaCheckPermission("biz:userFollows:edit")
    @Log(title = "用户跟投记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizUserFollowsBo bo) {
        return toAjax(bizUserFollowsService.updateByBo(bo));
    }

    /**
     * 删除用户跟投记录
     *
     * @param followIds 主键串
     */
    @SaCheckPermission("biz:userFollows:remove")
    @Log(title = "用户跟投记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{followIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] followIds) {
        return toAjax(bizUserFollowsService.deleteWithValidByIds(List.of(followIds), true));
    }


}
