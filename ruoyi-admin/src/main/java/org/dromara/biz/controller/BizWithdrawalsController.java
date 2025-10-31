package org.dromara.biz.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.biz.domain.bo.WithdrawalAuditBo;
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
import org.dromara.biz.domain.vo.BizWithdrawalsVo;
import org.dromara.biz.domain.bo.BizWithdrawalsBo;
import org.dromara.biz.service.IBizWithdrawalsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户提现申请
 *
 * @author Lion Li
 * @date 2025-08-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/withdrawals")
public class BizWithdrawalsController extends BaseController {

    private final IBizWithdrawalsService bizWithdrawalsService;

    /**
     * 后台审核提现申请
     */
    @SaCheckPermission("biz:withdrawals:audit") // 确保只有管理员能调用
    @Log(title = "提现申请审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public R<Void> audit(@Validated @RequestBody WithdrawalAuditBo bo) {
        bizWithdrawalsService.auditWithdrawal(bo);
        return R.ok("审核操作成功");
    }

    /**
     * 查询用户提现申请列表
     */
    @SaCheckPermission("biz:withdrawals:list")
    @GetMapping("/list")
    public TableDataInfo<BizWithdrawalsVo> list(BizWithdrawalsBo bo, PageQuery pageQuery) {
        return bizWithdrawalsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户提现申请列表
     */
    @SaCheckPermission("biz:withdrawals:export")
    @Log(title = "用户提现申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizWithdrawalsBo bo, HttpServletResponse response) {
        List<BizWithdrawalsVo> list = bizWithdrawalsService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户提现申请", BizWithdrawalsVo.class, response);
    }

    /**
     * 获取用户提现申请详细信息
     *
     * @param withdrawalId 主键
     */
    @SaCheckPermission("biz:withdrawals:query")
    @GetMapping("/{withdrawalId}")
    public R<BizWithdrawalsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long withdrawalId) {
        return R.ok(bizWithdrawalsService.queryById(withdrawalId));
    }

    /**
     * 新增用户提现申请
     */
    @SaCheckPermission("biz:withdrawals:add")
    @Log(title = "用户提现申请", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizWithdrawalsBo bo) {
        return toAjax(bizWithdrawalsService.insertByBo(bo));
    }

    /**
     * 修改用户提现申请
     */
    @SaCheckPermission("biz:withdrawals:edit")
    @Log(title = "用户提现申请", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizWithdrawalsBo bo) {
        return toAjax(bizWithdrawalsService.updateByBo(bo));
    }

    /**
     * 删除用户提现申请
     *
     * @param withdrawalIds 主键串
     */
    @SaCheckPermission("biz:withdrawals:remove")
    @Log(title = "用户提现申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{withdrawalIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] withdrawalIds) {
        return toAjax(bizWithdrawalsService.deleteWithValidByIds(List.of(withdrawalIds), true));
    }
}
