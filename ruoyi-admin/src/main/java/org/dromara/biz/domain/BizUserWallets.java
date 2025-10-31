package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 用户钱包地址对象 biz_user_wallets
 *
 * @author Lion Li
 * @date 2025-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_wallets")
public class BizUserWallets extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 钱包ID, 主键
     */
    @TableId(value = "wallet_id")
    private Long walletId;

    /**
     * 用户ID (关联 sys_user.user_id)
     */
    private Long userId;

    /**
     * TRON地址 (Base58格式)
     */
    private String address;

    /**
     * 加密后的私钥 (如果由系统生成)
     */
    private String privateKeyEncrypted;

    /**
     * 创建时间
     */
    private Date createdAt;


    /**
     * 名称
     */
    private String name;


    /**
     * 备注
     */
    private String note;
}
