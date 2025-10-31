package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizBetOrderDetails;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;



/**
 * 投注訂單詳情视图对象 biz_bet_order_details
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizBetOrderDetails.class)
public class BizBetOrderDetailsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 詳情ID, 主鍵
     */
    @ExcelProperty(value = "詳情ID, 主鍵")
    private Long detailId;

    /**
     * 所屬訂單ID (關聯 biz_bet_orders)
     */
    @ExcelProperty(value = "所屬訂單ID (關聯 biz_bet_orders)")
    private Long orderId;

    /**
     * 比賽ID
     */
    @ExcelProperty(value = "比賽ID")
    private Long matchId;

    /**
     * 比賽名称
     */
    @ExcelProperty(value = "比賽名称")
    private String matchName;

    /**
     * 玩法代碼
     */
    @ExcelProperty(value = "玩法代碼")
    private String poolCode;

    /**
     * 使用者的投注選擇
     */
    @ExcelProperty(value = "使用者的投注選擇")
    private String selection;

    /**
     * 下注時的賠率 (快照)
     */
    @ExcelProperty(value = "下注時的賠率 (快照)")
    private BigDecimal odds;

    /**
     * 賽果判定結果: NULL-待定, 1-命中, 0-未命中, 2-走水/作廢
     */
    @ExcelProperty(value = "賽果判定結果: NULL-待定, 1-命中, 0-未命中, 2-走水/作廢")
    private Integer isWinning;

    private String matchScore;
}
