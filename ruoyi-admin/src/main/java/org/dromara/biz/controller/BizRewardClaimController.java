package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizRewardClaimBo;
import org.dromara.biz.domain.vo.BizRewardClaimVo;
import org.dromara.biz.service.IBizRewardClaimService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
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
 * 理赔申请
 *
 * @author Lion Li
 * @date 2025-08-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/rewardClaim")
public class BizRewardClaimController extends BaseController {

    private final IBizRewardClaimService bizRewardClaimService;

    /**
     * 批准理赔申请
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:rewardClaim:approve") // 建议为此操作设置专门的权限
    @Log(title = "理赔申请审核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{id}")
    public R<Void> approveClaim(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        bizRewardClaimService.approveClaim(id);
        return R.ok("理赔申请已批准");
    }

    /**
     * 拒绝理赔申请
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:rewardClaim:reject") // 建议为此操作设置专门的权限
    @Log(title = "理赔申请审核", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{id}")
    public R<Void> rejectClaim(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        bizRewardClaimService.rejectClaim(id);
        return R.ok("理赔申请已拒绝");
    }

    /**
     * 查询理赔申请列表
     */
    @SaCheckPermission("biz:rewardClaim:list")
    @GetMapping("/list")
    public TableDataInfo<BizRewardClaimVo> list(BizRewardClaimBo bo, PageQuery pageQuery) {
        return bizRewardClaimService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出理赔申请列表
     */
    @SaCheckPermission("biz:rewardClaim:export")
    @Log(title = "理赔申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizRewardClaimBo bo, HttpServletResponse response) {
        List<BizRewardClaimVo> list = bizRewardClaimService.queryList(bo);
        ExcelUtil.exportExcel(list, "理赔申请", BizRewardClaimVo.class, response);
    }

    /**
     * 获取理赔申请详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:rewardClaim:query")
    @GetMapping("/{id}")
    public R<BizRewardClaimVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizRewardClaimService.queryById(id));
    }

    /**
     * 新增理赔申请
     */
    @SaCheckPermission("biz:rewardClaim:add")
    @Log(title = "理赔申请", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizRewardClaimBo bo) {
        return toAjax(bizRewardClaimService.insertByBo(bo));
    }

    /**
     * 修改理赔申请
     */
    @SaCheckPermission("biz:rewardClaim:edit")
    @Log(title = "理赔申请", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizRewardClaimBo bo) {
        return toAjax(bizRewardClaimService.updateByBo(bo));
    }

    /**
     * 删除理赔申请
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:rewardClaim:remove")
    @Log(title = "理赔申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizRewardClaimService.deleteWithValidByIds(List.of(ids), true));
    }
}
