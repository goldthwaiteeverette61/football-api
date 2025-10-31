package org.dromara.biz.domain.bo;

import lombok.Data;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 交易歷史記錄查詢業務對象
 */
@Data
public class TransactionHistoryBo extends BaseEntity {

    /**
     * 交易狀態
     */
    private String status;

    /**
     * 交易類型
     */
    private String transactionType;

}
