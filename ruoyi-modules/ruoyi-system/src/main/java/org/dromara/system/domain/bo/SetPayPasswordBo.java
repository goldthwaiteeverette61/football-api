package org.dromara.system.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetPayPasswordBo {

    /**
     * 登录密码 (用于身份校验)
     */
    @NotBlank(message = "登录密码不能为空")
    private String password;

    /**
     * 旧的支付密码 (仅在修改时需要)
     */
    private String oldPayPassword;

    /**
     * 新的支付密码
     */
    @NotBlank(message = "新支付密码不能为空")
    private String newPayPassword;
}
