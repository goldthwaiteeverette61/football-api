package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import org.dromara.system.domain.SysUser;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 用户信息视图对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
@AutoMapper(target = SysUser.class)
public class SysUserFontVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

//    /**
//     * 手机号码
//     */
//    private String phonenumber;


    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 余额锁定
     */
    private BigDecimal balanceLock;

    /**
     * tron钱包地址
     */
    private String walletAddressTron;

    /**
     * 核心修改：新增字段
     * TRON钱包地址的二维码 (Base64格式)
     */
    private String walletAddressTronQrCode;

    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private Long avatar;

    private String invitationCode;

    private Long inviterId;

    /**
     * 0，支付密码未设置；1，支付密码已设置。
     */
    private Integer payPasswordSeted;

}
