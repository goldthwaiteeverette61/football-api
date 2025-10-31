package org.dromara.biz.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 确保在比较对象时也考虑父类的字段
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDetailsVo extends BizTransactionsVo {
    private static final long serialVersionUID = 1L;

    /**
     * 奖金详情 (当 transactionType = 'BONUS' 时)
     */
    private BizUserFollowsVo bonusDetails;

    /**
     * 提现详情 (当 transactionType = 'WITHDRAWAL' 时)
     */
    private BizWithdrawalsVo withdrawalDetails;

    /**
     * 转账详情 (当 transactionType = 'INTERNAL_TRANSFER_IN' 或 'INTERNAL_TRANSFER_OUT' 时)
     */
    private TransferDetailsVo transferDetails;

    // 您可以根据需要为 FEE, ADJUSTMENT 等类型添加更多的详情字段
}
