
// ===================================================================================
// 模块: VO (View Object)
// 描述: 用于向前端展示统一格式的交易流水。
// ===================================================================================

// 文件路径: org/dromara/biz/domain/vo/TransactionHistoryVo.java
package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionHistoryVo {
    @ExcelProperty(value = "记录ID")
    private Long id;
    @ExcelProperty(value = "交易类型")
    private String transactionType;
    @ExcelProperty(value = "金额")
    private BigDecimal amount;
    @ExcelProperty(value = "对方用户")
    private String otherPartyUsername; // 用于转账时显示对方昵称
    @ExcelProperty(value = "备注")
    private String remarks;
    @ExcelProperty(value = "状态")
    private String status;
    @ExcelProperty(value = "时间")
    private Date createdAt;
}
