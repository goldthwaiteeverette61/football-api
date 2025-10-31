package org.dromara.biz.service;

import org.dromara.biz.domain.vo.TransactionDetailsVo;

public interface ITransactionDetailsService {

    /**
     * 根据交易ID获取完整的交易详情
     * @param transactionId 交易流水ID
     * @return 包含所有关联信息的详情视图对象
     */
    TransactionDetailsVo getTransactionDetails(Long transactionId);
}
