package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户提现申请对象 biz_withdrawals
 *
 * @author Lion Li
 * @date 2025-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_withdrawals")
public class BizWithdrawals extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 提现申请的唯一ID, 主键
     */
    @TableId(value = "withdrawal_id")
    private Long withdrawalId;

    /**
     * 申请提现的用户ID, 关联 sys_user 表
     */
    private Long userId;

    /**
     * 申请提现的 USDT 金额
     */
    private BigDecimal amount;

    /**
     * 提现时扣除的网络手续费（矿工费）
     */
    private BigDecimal networkFee;

    /**
     * 用户最终实际收到的金额 (amount - network_fee)
     */
    private BigDecimal finalAmount;

    /**
     * 收款的TRC20钱包地址
     */
    private String toWalletAddress;

    /**
     * 提现申请的状态
     */
    private String status;

    /**
     * 用户提交申请的时间
     */
    private Date requestTime;

    /**
     * 审核该笔申请的管理员ID
     */
    private Long auditBy;

    /**
     * 审核操作的时间
     */
    private Date auditTime;

    /**
     * 审核备注, 特别是拒绝时需要填写原因
     */
    private String auditRemarks;

    /**
     * TRON链上的交易哈希 (Transaction Hash), 用于核对
     */
    private String txHash;

    /**
     * 提现完成（资金到账）的时间
     */
    private Date completionTime;


}
