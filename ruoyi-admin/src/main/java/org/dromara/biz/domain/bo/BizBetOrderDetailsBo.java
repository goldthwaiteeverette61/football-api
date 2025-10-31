package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.biz.domain.BizBetOrderDetails;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 投注訂單詳情业务对象 biz_bet_order_details
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AutoMapper(target = BizBetOrderDetails.class, reverseConvertGenerate = false)
public class BizBetOrderDetailsBo extends BaseEntity {

    /**
     * 詳情ID, 主鍵
     */
    @NotNull(message = "詳情ID, 主鍵不能为空", groups = { EditGroup.class })
    private Long detailId;

    /**
     * 所屬訂單ID (關聯 biz_bet_orders)
     */
    @NotNull(message = "所屬訂單ID (關聯 biz_bet_orders)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long orderId;

    /**
     * 比賽ID
     */
    @NotNull(message = "比賽ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long matchId;

    /**
     * 玩法代碼
     */
    @NotBlank(message = "玩法代碼不能为空", groups = { AddGroup.class, EditGroup.class })
    private String poolCode;

    /**
     * 使用者的投注選擇
     */
    @NotBlank(message = "使用者的投注選擇不能为空", groups = { AddGroup.class, EditGroup.class })
    private String selection;

    /**
     * 下注時的賠率 (快照)
     */
    @NotNull(message = "下注時的賠率 (快照)不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal odds;

    /**
     * 賽果判定結果: NULL-待定, 1-命中, 0-未命中, 2-走水/作廢
     */
    private Integer isWinning;

    private List<Long> orderIds;

    private String matchScore;

}
