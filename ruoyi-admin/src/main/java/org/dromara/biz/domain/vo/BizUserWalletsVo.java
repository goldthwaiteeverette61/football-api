package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizUserWallets;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 用户钱包地址视图对象 biz_user_wallets
 *
 * @author Lion Li
 * @date 2025-08-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizUserWallets.class)
public class BizUserWalletsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 钱包ID, 主键
     */
    @ExcelProperty(value = "钱包ID, 主键")
    private Long walletId;

    /**
     * 用户ID (关联 sys_user.user_id)
     */
    @ExcelProperty(value = "用户ID (关联 sys_user.user_id)")
    private Long userId;

    /**
     * TRON地址 (Base58格式)
     */
    @ExcelProperty(value = "TRON地址 (Base58格式)")
    private String address;

    /**
     * 加密后的私钥 (如果由系统生成)
     */
    @ExcelProperty(value = "加密后的私钥 (如果由系统生成)")
    private String privateKeyEncrypted;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
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
