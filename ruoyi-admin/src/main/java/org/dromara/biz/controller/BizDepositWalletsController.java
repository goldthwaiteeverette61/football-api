package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizDepositWalletsBo;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.biz.service.IBizDepositWalletsService;
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
 * 平台充值钱包
 *
 * @author Lion Li
 * @date 2025-08-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/depositWallets")
public class BizDepositWalletsController extends BaseController {

    private final IBizDepositWalletsService bizDepositWalletsService;

    /**
     * 【新增】获取所有活跃且未同步的钱包地址
     */
    @SaCheckPermission("biz:depositWallets:list")
    @GetMapping("/active-unsynced")
    public R<List<String>> getActiveUnsyncedWallets() {
        return R.ok(bizDepositWalletsService.selectWalletsAwaitingProcessing());
    }

    /**
     * 【新增】批量更新钱包的同步状态
     * @param walletAddresses 钱包地址列表
     */
    @SaCheckPermission("biz:depositWallets:edit")
    @Log(title = "批量更新钱包同步状态", businessType = BusinessType.UPDATE)
    @PutMapping("/synced-status")
    public R<Void> updateSyncedStatusForList(@RequestBody List<String> walletAddresses) {
        return toAjax(bizDepositWalletsService.updateHasBalancesForList(walletAddresses));
    }

    /**
     * 查询平台充值钱包列表
     */
    @SaCheckPermission("biz:depositWallets:list")
    @GetMapping("/list")
    public TableDataInfo<BizDepositWalletsVo> list(BizDepositWalletsBo bo, PageQuery pageQuery) {
        bo.setStatus("active");
        return bizDepositWalletsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出平台充值钱包列表
     */
    @SaCheckPermission("biz:depositWallets:export")
    @Log(title = "平台充值钱包", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizDepositWalletsBo bo, HttpServletResponse response) {
        List<BizDepositWalletsVo> list = bizDepositWalletsService.queryList(bo);
        ExcelUtil.exportExcel(list, "平台充值钱包", BizDepositWalletsVo.class, response);
    }

    /**
     * 获取平台充值钱包详细信息
     *
     * @param walletId 主键
     */
    @SaCheckPermission("biz:depositWallets:query")
    @GetMapping("/{walletId}")
    public R<BizDepositWalletsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long walletId) {
        return R.ok(bizDepositWalletsService.queryById(walletId));
    }

    /**
     * 新增平台充值钱包
     */
    @SaCheckPermission("biz:depositWallets:add")
    @Log(title = "平台充值钱包", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizDepositWalletsBo bo) {
        return toAjax(bizDepositWalletsService.insertByBo(bo));
    }

    /**
     * 修改平台充值钱包
     */
    @SaCheckPermission("biz:depositWallets:edit")
    @Log(title = "平台充值钱包", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizDepositWalletsBo bo) {
        return toAjax(bizDepositWalletsService.updateByBo(bo));
    }

    /**
     * 删除平台充值钱包
     *
     * @param walletIds 主键串
     */
    @SaCheckPermission("biz:depositWallets:remove")
    @Log(title = "平台充值钱包", businessType = BusinessType.DELETE)
    @DeleteMapping("/{walletIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] walletIds) {
        return toAjax(bizDepositWalletsService.deleteWithValidByIds(List.of(walletIds), true));
    }
}
