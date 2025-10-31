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
import org.dromara.biz.domain.vo.BizUserWalletsVo;
import org.dromara.biz.domain.bo.BizUserWalletsBo;
import org.dromara.biz.service.IBizUserWalletsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户钱包地址
 *
 * @author Lion Li
 * @date 2025-08-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/userWallets")
public class BizUserWalletsController extends BaseController {

    private final IBizUserWalletsService bizUserWalletsService;

    /**
     * 查询用户钱包地址列表
     */
    @SaCheckPermission("biz:userWallets:list")
    @GetMapping("/list")
    public TableDataInfo<BizUserWalletsVo> list(BizUserWalletsBo bo, PageQuery pageQuery) {
        return bizUserWalletsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户钱包地址列表
     */
    @SaCheckPermission("biz:userWallets:export")
    @Log(title = "用户钱包地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizUserWalletsBo bo, HttpServletResponse response) {
        List<BizUserWalletsVo> list = bizUserWalletsService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户钱包地址", BizUserWalletsVo.class, response);
    }

    /**
     * 获取用户钱包地址详细信息
     *
     * @param walletId 主键
     */
    @SaCheckPermission("biz:userWallets:query")
    @GetMapping("/{walletId}")
    public R<BizUserWalletsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long walletId) {
        return R.ok(bizUserWalletsService.queryById(walletId));
    }

    /**
     * 新增用户钱包地址
     */
    @SaCheckPermission("biz:userWallets:add")
    @Log(title = "用户钱包地址", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizUserWalletsBo bo) {
        return toAjax(bizUserWalletsService.insertByBo(bo));
    }

    /**
     * 修改用户钱包地址
     */
    @SaCheckPermission("biz:userWallets:edit")
    @Log(title = "用户钱包地址", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizUserWalletsBo bo) {
        return toAjax(bizUserWalletsService.updateByBo(bo));
    }

    /**
     * 删除用户钱包地址
     *
     * @param walletIds 主键串
     */
    @SaCheckPermission("biz:userWallets:remove")
    @Log(title = "用户钱包地址", businessType = BusinessType.DELETE)
    @DeleteMapping("/{walletIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] walletIds) {
        return toAjax(bizUserWalletsService.deleteWithValidByIds(List.of(walletIds), true));
    }
}
