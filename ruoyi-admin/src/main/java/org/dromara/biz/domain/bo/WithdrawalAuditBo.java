package org.dromara.biz.domain.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawalAuditBo {

    @NotNull(message = "提現申請ID不能爲空")
    private Long withdrawalId;

    @NotNull(message = "必須提供審覈結果")
    private Boolean isApproved;

    // 拒絕時，備註爲必填項
    private String auditRemarks;

    /**
     * txId
     */
    private String txHash;
}
