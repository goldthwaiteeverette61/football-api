package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizBetOrders;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 使用者投注訂單视图对象 biz_bet_orders
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizBetOrders.class)
public class BizBetOrdersVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 訂單ID, 主鍵
     */
    @ExcelProperty(value = "訂單ID, 主鍵")
    private Long orderId;

    /**
     * 下注使用者ID
     */
    @ExcelProperty(value = "下注使用者ID")
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
    @ExcelProperty(value = "總投注金額")
    private BigDecimal betAmount;

    /**
     * 過關類型 (例如: "1x1"-單關, "2x1", "3x1")
     */
    @ExcelProperty(value = "過關類型")
    private String combinationType;

    /**
     * 訂單狀態: pending-待開獎, won-已中獎, lost-未中獎, void-作廢
     */
    @ExcelProperty(value = "訂單狀態: pending-待開獎, won-已中獎, lost-未中獎, void-作廢")
    private String status;

    /**
     * 派彩金額 (中獎後更新)
     */
    @ExcelProperty(value = "派彩金額 (中獎後更新)")
    private BigDecimal payoutAmount;

    /**
     * 投注失效过期时间
     */
    private Date expirationTime;

    /**
     * 创建时间
     */
    private Date createTime;

    private List<BizBetOrderDetailsVo> bizBetOrderDetailsVos;

}
