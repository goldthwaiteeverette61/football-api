package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_transactions")
public class BizTransactions extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名称
     */
    private String userName;

    private Long userId;

    private BigDecimal amount;

    private String currency;

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
