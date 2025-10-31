package org.dromara.biz.domain.bo;

import lombok.experimental.Accessors;
import org.dromara.biz.domain.BizBetOrders;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 使用者投注訂單业务对象 biz_bet_orders
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizBetOrders.class, reverseConvertGenerate = false)
@Accessors(chain = true)
public class BizBetOrdersBo extends BaseEntity {

    /**
     * 訂單ID, 主鍵
     */
    @NotNull(message = "訂單ID, 主鍵不能为空", groups = { EditGroup.class })
    private Long orderId;

    /**
     * 下注使用者ID
     */
    @NotNull(message = "下注使用者ID不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "總投注金額不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal betAmount;

    /**
     * 過關類型 (例如: "1x1"-單關, "2x1", "3x1")
     */
    @NotBlank(message = "過關類型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String combinationType;

    /**
     * 訂單狀態: pending-待開獎, won-已中獎, lost-未中獎, void-作廢
     */
    @NotBlank(message = "訂單狀態: pending-待開獎, won-已中獎, lost-未中獎, void-作廢不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 派彩金額 (中獎後更新)
     */
    private BigDecimal payoutAmount;

    /**
     * 投注失效过期时间
     */
    private Date expirationTime;

    /**
     * 【新增】查询条件：小于此过期时间
     */
    private Date ltExpirationTime;

}
