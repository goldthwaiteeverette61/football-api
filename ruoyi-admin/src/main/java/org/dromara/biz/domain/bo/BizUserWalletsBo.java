package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizUserWallets;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 用戶錢包地址業務對象 biz_user_wallets
 *
 * @author Lion Li
 * @date 2025-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizUserWallets.class, reverseConvertGenerate = false)
public class BizUserWalletsBo extends BaseEntity {

    /**
     * 用戶名稱
     */
    private String userName;

    /**
     * 錢包ID, 主鍵
     */
    @NotNull(message = "錢包ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long walletId;

    /**
     * 用戶ID (關聯 sys_user.user_id)
     */
    private Long userId;

    /**
     * TRON地址 (Base58格式)
     */
    @NotBlank(message = "TRON地址 (Base58格式)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String address;

    /**
     * 加密後的私鑰 (如果由系統生成)
     */
    private String privateKeyEncrypted;

    /**
     * 創建時間
     */
    private Date createdAt;

    /**
     * 名稱
     */
    private String name;

    /**
     * 備註
     */
    private String note;
}
