package org.dromara.biz.app;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.service.impl.BizDepositWalletsServiceImpl;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.MimeTypeUtils;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.helper.DataPermissionHelper;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.bo.SetPayPasswordBo;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.bo.SysUserPasswordBo;
import org.dromara.system.domain.bo.SysUserProfileShortBo;
import org.dromara.system.domain.vo.AvatarVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.domain.vo.SysUserFontVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysOssService;
import org.dromara.system.service.ISysUserService;
import org.dromara.web.service.SysRegisterService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;


/**
 * 用戶信息前端
 *
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/users")
@Tag(name = "user", description = "用戶")
public class AppUsers {

    private final ISysUserService iSysUserService;
    private final SysRegisterService registerService;
    private final BizDepositWalletsServiceImpl bizDepositWalletsService;
    private final ISysOssService ossService;


    /**
     * 設置或修改支付密碼
     */
    @PostMapping("/setPayPassword")
    public R<Void> setPayPassword(@Validated @RequestBody SetPayPasswordBo bo) {
        iSysUserService.setPayPassword(bo);
        return R.ok("操作成功");
    }

    /**
     * 獲取用戶信息詳細信息
     *
     */
    @GetMapping("userInfo")
    public R<SysUserFontVo> getInfo() {
        SysUserVo sysUserVo = iSysUserService.selectUserById(LoginHelper.getUserId());
        sysUserVo.setWalletAddressTron(bizDepositWalletsService.applyDepositWallet());
//        BizDepositWalletsVo bizDepositWalletsVo = bizDepositWalletsService.queryOne(bizDepositWalletsService.lqw().eq(BizDepositWallets::getUserId,LoginHelper.getUserId()));
//        if(bizDepositWalletsVo != null) {
//            sysUserVo.setWalletAddressTron(bizDepositWalletsVo.getWalletAddress());
////            sysUserVo.setWalletAddressTronQrCode(FileUtils.generateQrCodeBase64(sysUserVo.getWalletAddressTron()));
//        }

        SysUserFontVo sysUserFontVo = new SysUserFontVo();
        BeanUtil.copyProperties(sysUserVo,sysUserFontVo);

        return R.ok(sysUserFontVo);
    }

    /**
     * 用戶申请钱包
     */
    @PostMapping("/applyDepositWallet")
    public R<String> applyDepositWallet() {
        return R.ok("成功",bizDepositWalletsService.applyDepositWallet());
    }

    /**
     * 頭像上傳
     *
     * @param avatarfile 用戶頭像
     */
    @RepeatSubmit
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<AvatarVo> avatar(@RequestPart("avatarfile") MultipartFile avatarfile) {
        if (!avatarfile.isEmpty()) {
            String extension = FileUtil.extName(avatarfile.getOriginalFilename());
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
                return R.fail("文件格式不正確，請上傳" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
            }
            SysOssVo oss = ossService.upload(avatarfile);
            String avatar = oss.getUrl();
            boolean updateSuccess = DataPermissionHelper.ignore(() -> iSysUserService.updateUserAvatar(LoginHelper.getUserId(), oss.getOssId()));
            if (updateSuccess) {
                AvatarVo avatarVo = new AvatarVo();
                avatarVo.setImgUrl(avatar);
                return R.ok(avatarVo);
            }
        }
        return R.fail("上傳圖片異常");
    }

    /**
     * 修改用戶信息
     */
    @RepeatSubmit
    @PutMapping
    public R<Void> updateProfile(@Validated @RequestBody SysUserProfileShortBo profile) {
        SysUserBo user = BeanUtil.toBean(profile, SysUserBo.class);
        user.setUserId(LoginHelper.getUserId());
        String username = LoginHelper.getUsername();
//        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
//            return R.fail("修改用戶'" + username + "'失敗，手機號碼已存在");
//        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !iSysUserService.checkEmailUnique(user)) {
            return R.fail("修改用戶'" + username + "'失敗，郵箱賬號已存在");
        }
        int rows = DataPermissionHelper.ignore(() -> iSysUserService.updateUserProfile(user));
        if (rows > 0) {
            return R.ok();
        }
        return R.fail("修改個人信息異常");
    }

    /**
     *  修改密碼
     *
     * @param bo 新舊密碼
     */
    @RepeatSubmit
    @PutMapping("/updatePwd")
    public R<Void> updatePwd(@Validated @RequestBody SysUserPasswordBo bo) {
        SysUserVo user = iSysUserService.selectUserById(LoginHelper.getUserId());
        String password = user.getPassword();
        if (!BCrypt.checkpw(bo.getOldPassword(), password)) {
            return R.fail("修改密碼失敗，舊密碼錯誤");
        }
        if (BCrypt.checkpw(bo.getNewPassword(), password)) {
            return R.fail("新密碼不能與舊密碼相同");
        }
        int rows = DataPermissionHelper.ignore(() -> iSysUserService.resetUserPwd(user.getUserId(), BCrypt.hashpw(bo.getNewPassword())));
        if (rows > 0) {
            return R.ok();
        }
        return R.fail("修改密碼異常，請聯繫管理員");
    }
}
