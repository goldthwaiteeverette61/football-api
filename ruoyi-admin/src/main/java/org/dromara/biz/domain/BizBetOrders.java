package org.dromara.biz.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;
import java.util.Date;

/**
 * 使用者投注訂單对象 biz_bet_orders
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_bet_orders")
public class BizBetOrders extends BaseEntity {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_WON = "won";
    public static final String STATUS_LOST = "lost";
    public static final String STATUS_VOID = "void";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 訂單ID, 主鍵
     */
    @TableId(value = "order_id")
    private Long orderId;

    /**
     * 下注使用者ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 赔率描述
     */
    private String oddsDesc;

    /**
     * 總投注金額
     */
    private BigDecimal betAmount;

    /**
     * 過關類型 (例如: "1x1"-單關, "2x1", "3x1")
     */
    private String combinationType="";

    /**
     * 訂單狀態: pending-待開獎, won-已中獎, lost-未中獎, void-作廢
     */
    private String status;

    /**
     * 派彩金額 (中獎後更新)
     */
    private BigDecimal payoutAmount;

    /**
     * 投注失效过期时间
     */
    private Date expirationTime;

}
