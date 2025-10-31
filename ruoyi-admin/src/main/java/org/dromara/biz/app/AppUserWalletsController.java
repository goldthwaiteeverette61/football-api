package org.dromara.biz.app;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizUserWalletsBo;
import org.dromara.biz.domain.vo.BizUserWalletsVo;
import org.dromara.biz.service.IBizUserWalletsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用戶錢包地址
 *
 * @author Lion Li
 * @date 2025-08-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/userWallets")
@Tag(name = "wallet", description = "錢包")
public class AppUserWalletsController extends BaseController {

    private final IBizUserWalletsService bizUserWalletsService;

    /**
     * 查詢用戶錢包地址列表
     */
    @GetMapping("/list")
    public TableDataInfo<BizUserWalletsVo> list(BizUserWalletsBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        return bizUserWalletsService.queryPageList(bo, pageQuery);
    }

    /**
     * 獲取用戶錢包地址詳細信息
     *
     * @param walletId 主鍵
     */
    @GetMapping("/{walletId}")
    public R<BizUserWalletsVo> getInfo(@NotNull(message = "主鍵不能爲空")
                                     @PathVariable Long walletId) {
        return R.ok(bizUserWalletsService.queryById(walletId));
    }

    /**
     * 新增用戶錢包地址
     */
    @Log(title = "用戶錢包地址", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizUserWalletsBo bo) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();
        String username = loginUser.getUsername();

        // 此處可以增加對 toWalletAddress 格式的校驗，例如BSC地址以'0x'開頭，長度爲42位
        if (bo.getAddress() == null || !bo.getAddress().startsWith("0x") || bo.getAddress().length() != 42) {
            throw new ServiceException("無效的BSC錢包地址");
        }

        bo.setUserId(userId);
        bo.setUserName(username);
        return toAjax(bizUserWalletsService.insertByBo(bo));
    }

    /**
     * 修改用戶錢包地址
     */
    @Log(title = "用戶錢包地址", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizUserWalletsBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        return toAjax(bizUserWalletsService.updateByBo(bo));
    }

    /**
     * 刪除用戶錢包地址
     *
     * @param walletIds 主鍵串
     */
    @Log(title = "用戶錢包地址", businessType = BusinessType.DELETE)
    @DeleteMapping("/{walletIds}")
    public R<Void> remove(@NotEmpty(message = "主鍵不能爲空")
                          @PathVariable Long[] walletIds) {
        return toAjax(bizUserWalletsService.deleteWithValidByIds(List.of(walletIds), true));
    }
}
