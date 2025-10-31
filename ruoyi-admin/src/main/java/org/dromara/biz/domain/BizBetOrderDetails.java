package org.dromara.biz.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;

/**
 * 投注訂單詳情对象 biz_bet_order_details
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_bet_order_details")
public class BizBetOrderDetails extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 詳情ID, 主鍵
     */
    @TableId(value = "detail_id")
    private Long detailId;

    /**
     * 所屬訂單ID (關聯 biz_bet_orders)
     */
    private Long orderId;

    /**
     * 比賽ID
     */
    private Long matchId;

    /**
     * 玩法代碼
     */
    private String poolCode;

    /**
     * 使用者的投注選擇
     */
    private String selection;

    /**
     * 下注時的賠率 (快照)
     */
    private BigDecimal odds;

    /**
     * 賽果判定結果: NULL-待定, 1-命中, 0-未命中, 2-走水/作廢
     */
    private Integer isWinning;

    private String matchScore;

}
