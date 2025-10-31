package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizWithdrawals;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用戶提現申請業務對象 biz_withdrawals
 *
 * @author Lion Li
 * @date 2025-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizWithdrawals.class, reverseConvertGenerate = false)
public class BizWithdrawalsBo extends BaseEntity {

    /**
     * 用戶名稱
     */
    private String userName;

    /**
     * 提現申請的唯一ID, 主鍵
     */
    @NotNull(message = "提現申請的唯一ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long withdrawalId;

    /**
     * 申請提現的用戶ID, 關聯 sys_user 表
     */
    @NotNull(message = "申請提現的用戶ID, 關聯 sys_user 表不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 申請提現的 USDT 金額
     */
    @NotNull(message = "申請提現的 USDT 金額不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;

    /**
     * 提現時扣除的網絡手續費（礦工費）
     */
    @NotNull(message = "提現時扣除的網絡手續費（礦工費）不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal networkFee;

    /**
     * 用戶最終實際收到的金額 (amount - network_fee)
     */
    @NotNull(message = "用戶最終實際收到的金額 (amount - network_fee)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal finalAmount;

    /**
     * 收款的TRC20錢包地址
     */
    @NotBlank(message = "收款的TRC20錢包地址不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String toWalletAddress;

    /**
     * 提現申請的狀態 (PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, FAILED)
     */
    @NotBlank(message = "提現申請的狀態 (PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, FAILED)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 用戶提交申請的時間
     */
    @NotNull(message = "用戶提交申請的時間不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Date requestTime;

    /**
     * 審覈該筆申請的管理員ID
     */
    private Long auditBy;

    /**
     * 審覈操作的時間
     */
    private Date auditTime;

    /**
     * 審覈備註, 特別是拒絕時需要填寫原因
     */
    private String auditRemarks;

    /**
     * TRON鏈上的交易哈希 (Transaction Hash), 用於覈對
     */
    private String txHash;

    /**
     * 提現完成（資金到賬）的時間
     */
    private Date completionTime;


}
