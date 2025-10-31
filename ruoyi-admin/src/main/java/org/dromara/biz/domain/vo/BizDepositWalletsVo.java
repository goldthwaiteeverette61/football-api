package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizDepositWallets;

import java.io.Serial;
import java.io.Serializable;


/**
 * 平台充值钱包视图对象 biz_deposit_wallets
 *
 * @author Lion Li
 * @date 2025-08-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizDepositWallets.class)
public class BizDepositWalletsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int synced;

    private Long lastScannedBlock;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 充值钱包ID, 主键
     */
    @ExcelProperty(value = "充值钱包ID, 主键")
    private Long walletId;

    /**
     * 钱包名称
     */
    @ExcelProperty(value = "钱包名称")
    private String walletName;

    /**
     * 钱包地址
     */
    @ExcelProperty(value = "钱包地址")
    private String walletAddress;

    /**
     * 状态 (active, inactive)
     */
    @ExcelProperty(value = "状态 (active, inactive)")
    private String status;

    /**
     * 二维码图片URL
     */
    @ExcelProperty(value = "二维码图片URL")
    private String qrCodeUrl;

    /**
     * 用户ID
     */
    private Long userId;

    private int hasBalance;

}
