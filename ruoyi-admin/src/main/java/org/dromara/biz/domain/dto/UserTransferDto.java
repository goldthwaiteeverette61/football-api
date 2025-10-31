package org.dromara.biz.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserTransferDto {

    @NotNull(message = "收款人用戶名稱不能爲空")
    private String toUserName;

    @NotNull(message = "轉賬金額不能爲空")
    @DecimalMin(value = "0.01", message = "轉賬金額必須大於0")
    private BigDecimal amount;

    private String remark;

    @NotNull(message = "支付密碼不能爲空")
    private String payPassword;
}
