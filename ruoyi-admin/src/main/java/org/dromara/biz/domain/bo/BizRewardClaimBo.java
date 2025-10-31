package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizRewardClaim;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 理賠申請業務對象 biz_reward_claim
 *
 * @author Lion Li
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizRewardClaim.class, reverseConvertGenerate = false)
public class BizRewardClaimBo extends BaseEntity {

    /**
     * 用戶名稱
     */
    private String userName;

    /**
     * 主鍵
     */
    @NotNull(message = "主鍵不能爲空", groups = { EditGroup.class })
    private Long id;

    /**
     * 申請用戶ID
     */
    @NotNull(message = "申請用戶ID不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 申請金額
     */
    @NotNull(message = "申請金額不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;

    /**
     * 貨幣類型
     */
    private String currency;

    /**
     * 狀態 (PENDING, APPROVED, REJECTED)
     */
    @NotBlank(message = "狀態 (PENDING, APPROVED, REJECTED)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 備註
     */
    private String remarks;

    /**
     * 失敗次數
     */
    private Integer lostCount;

    /**
     * 業務編號
     */
    private String bizCode;
}
