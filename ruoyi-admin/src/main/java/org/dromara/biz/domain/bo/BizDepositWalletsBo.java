package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizDepositWallets;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 平臺充值錢包業務對象 biz_deposit_wallets
 *
 * @author Lion Li
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizDepositWallets.class, reverseConvertGenerate = false)
public class BizDepositWalletsBo extends BaseEntity {

    private int synced;

    /**
     * 用戶名稱
     */
    private String userName;

    /**
     * 充值錢包ID, 主鍵
     */
    @NotNull(message = "充值錢包ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long walletId;

    /**
     * 錢包名稱
     */
    @NotBlank(message = "錢包名稱不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String walletName;

    /**
     * 錢包地址
     */
    @NotBlank(message = "錢包地址不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String walletAddress;

    /**
     * 狀態 (active, inactive)
     */
    @NotBlank(message = "狀態 (active, inactive)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 二維碼圖片URL
     */
    private String qrCodeUrl;

    /**
     * 用戶ID
     */
    private Long userId;

    private Long lastScannedBlock;

    private int hasBalance;

}
