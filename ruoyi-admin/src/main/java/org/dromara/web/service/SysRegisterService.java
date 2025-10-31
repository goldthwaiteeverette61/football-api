package org.dromara.web.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizInvitationCodesBo;
import org.dromara.biz.domain.bo.BizUserInvitationsBo;
import org.dromara.biz.domain.vo.BizInvitationCodesVo;
import org.dromara.biz.service.IBizInvitationCodesService;
import org.dromara.biz.service.IBizUserInvitationsService;
import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.biz.service.impl.BizDepositWalletsServiceImpl;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.model.RegisterBody;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.exception.user.CaptchaException;
import org.dromara.common.core.exception.user.CaptchaExpireException;
import org.dromara.common.core.exception.user.UserException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.log.event.LogininforEvent;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.web.config.properties.CaptchaProperties;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.vo.SysDictDataVo;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.ISysDictDataService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 注册校验方法
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SysRegisterService {

    private final ISysUserService userService;
    private final SysUserMapper userMapper;
    private final CaptchaProperties captchaProperties;
    private final BizDepositWalletsServiceImpl bizDepositWalletsService;
    private final IBizInvitationCodesService bizInvitationCodesService;
    private final IBizUserInvitationsService bizUserInvitationsService;
    private final ISysDictDataService dictDataService;
    private final IBizUserProgressService iBizUserProgressService;

    /**
     * 注册
     */
    @Transactional
    public void register(RegisterBody registerBody) {
        if(registerBody.getInvitationCode() != null)
            registerBody.setInvitationCode(registerBody.getInvitationCode().trim());
        String tenantId = registerBody.getTenantId();
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        // 验证码校验
        if (captchaProperties.getEnable()) {
            validateCaptcha(tenantId, username, registerBody.getCode(), registerBody.getUuid());
        }

        SysUserBo sysUser = new SysUserBo();

        // --- 邀请码处理开始 ---
        Long inviterId = null;
        Long agentId = null;
        if (StringUtils.isNotBlank(registerBody.getInvitationCode())) {
            // 根据邀请码查找邀请人
            SysUser inviter = userService.selectUserByInvitationCode(registerBody.getInvitationCode());
            if (inviter == null) {
                throw new ServiceException("请输入有效的邀请码");
            }
            inviterId = inviter.getUserId();
            agentId = inviter.getAgentUserId();

            // 设置邀请人ID
            sysUser.setInviterId(inviterId);
            sysUser.setAgentUserId(agentId);
        }

        Long randomAvatarId = 1962723770180399106l;
        List<SysDictDataVo> defaultAvatars = dictDataService.selectDefaultUserAvatars();
        if (CollUtil.isNotEmpty(defaultAvatars)) {
            // 随机选择一个头像
            SysDictDataVo randomAvatar = defaultAvatars.get(new Random().nextInt(defaultAvatars.size()));
            try {
                randomAvatarId = Long.parseLong(randomAvatar.getDictValue());
            } catch (NumberFormatException e) {
                log.error("默认头像字典值配置错误，无法转换为Long: {}", randomAvatar.getDictValue());
            }
        } else {
            log.warn("未找到 'default_user_logo' 类型的字典数据，新用户将没有默认头像。");
        }

        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setAvatar(randomAvatarId);
        sysUser.setPassword(BCrypt.hashpw(password));
        sysUser.setUserType(userType);
        sysUser.setEmail(registerBody.getEmail());
        sysUser.setUserId(IdUtil.getSnowflake().nextId());

        boolean exist = userMapper.exists(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, sysUser.getUserName()).or().eq(SysUser::getEmail,sysUser.getEmail()));
        if (exist) {
            throw new ServiceException("注册失败，"+username+"账号或者"+sysUser.getEmail()+"已存在！");
        }

        boolean regFlag = userService.registerUserFront(sysUser);
        if (!regFlag) {
            throw new UserException("user.register.error");
        }

        if (inviterId != null) {
            BizUserInvitationsBo invitationBo = new BizUserInvitationsBo();
            invitationBo.setInviterId(inviterId);
            invitationBo.setInviteeId(sysUser.getUserId());
            invitationBo.setInvitationCodeUsed(registerBody.getInvitationCode());
            bizUserInvitationsService.insertByBo(invitationBo);
        }

        // 2. 为新用户分配一个专属的邀请码
        BizInvitationCodesVo availableCode = bizInvitationCodesService.findOneAvailableCode();
        if (availableCode == null) {
            throw new ServiceException("系统邀请码库存不足，注册失败。");
        }
        // 2.1 更新用户表，写入邀请码
        SysUserBo userUpdateBo = new SysUserBo();
        userUpdateBo.setUserId(sysUser.getUserId());
        userUpdateBo.setInvitationCode(availableCode.getInvitationCode());
        userService.updateUser(userUpdateBo);
        // 2.2 更新邀请码池，标记为已分配
        BizInvitationCodesBo codeUpdateBo = new BizInvitationCodesBo();
        codeUpdateBo.setCodeId(availableCode.getCodeId());
        codeUpdateBo.setStatus("assigned");
        codeUpdateBo.setAssigneeUserId(sysUser.getUserId());
        codeUpdateBo.setAssignTime(new Date());
        bizInvitationCodesService.updateByBo(codeUpdateBo);

        iBizUserProgressService.createInitialProgress(sysUser.getUserId(),sysUser.getUserName());

        recordLogininfor(tenantId, username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }


    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String tenantId, String username, String code, String uuid) {
        //测试专用
        if(code.equals("9999l9999")){
            return;
        }
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    private void recordLogininfor(String tenantId, String username, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(tenantId);
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }

}
