package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 平台充值钱包对象 biz_deposit_wallets
 *
 * @author Lion Li
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_deposit_wallets")
public class BizDepositWallets extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long lastScannedBlock;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 充值钱包ID, 主键
     */
    @TableId(value = "wallet_id")
    private Long walletId;

    /**
     * 钱包名称
     */
    private String walletName;

    /**
     * 钱包地址
     */
    private String walletAddress;

    /**
     * 状态 (active, inactive)
     */
    private String status;

    /**
     * 二维码图片URL
     */
    private String qrCodeUrl;

    /**
     * 用户ID
     */
    private Long userId;

    private int synced;

    private int hasBalance;

}
