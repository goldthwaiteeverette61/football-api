package org.dromara.biz.domain.bo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawalApplyBo {

    @NotNull(message = "提現金額不能爲空")
    @DecimalMin(value = "0.01", message = "提現金額必須大於0")
    private BigDecimal amount;

    @NotBlank(message = "收款錢包地址不能爲空")
    private String toWalletAddress;

    @NotBlank(message = "支付密碼")
    private String payPassword;
}
