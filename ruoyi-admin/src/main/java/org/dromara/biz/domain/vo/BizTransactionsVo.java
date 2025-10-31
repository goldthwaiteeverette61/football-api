package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizTransactions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
// 核心修复：添加 AutoMapper 注解，明确告知 MapStruct 源对象是 BizTransactions
@AutoMapper(target = BizTransactions.class)
public class BizTransactionsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    @ExcelProperty(value = "流水ID")
    private Long id;

    @ExcelProperty(value = "用户ID")
    private Long userId;

    @ExcelProperty(value = "交易金额")
    private BigDecimal amount;

    @ExcelProperty(value = "币种")
    private String currency;

    @ExcelProperty(value = "交易类型")
    private String transactionType;

    @ExcelProperty(value = "状态")
    private String status;

    @ExcelProperty(value = "关联ID")
    private String referenceId;

    @ExcelProperty(value = "备注")
    private String remarks;

    @ExcelProperty(value = "区块链网络")
    private String blockchainNetwork;

    @ExcelProperty(value = "交易哈希")
    private String transactionHash;

    @ExcelProperty(value = "付款方地址")
    private String fromAddress;

    @ExcelProperty(value = "收款方地址")
    private String toAddress;

    @ExcelProperty(value = "源ID")
    private String sourceId;

    private Date createTime;
}
