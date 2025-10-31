package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.biz.domain.BizTransactions;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizTransactions.class)
@Accessors(chain = true)
public class BizTransactionsBo extends BaseEntity {

    private Long id;

    /**
     * 用戶名稱
     */
    private String userName;

    @NotNull(message = "用戶ID不能爲空")
    private Long userId;

    @NotNull(message = "交易金額不能爲空")
    private BigDecimal amount;

    @NotNull(message = "幣種不能爲空")
    private String currency;

    @NotNull(message = "交易類型不能爲空")
    private String transactionType;

    private String status;
    private String referenceId;
    private String remarks;
    private String blockchainNetwork;
    private String transactionHash;
    private String fromAddress;
    private String toAddress;
    private String sourceId;
}
