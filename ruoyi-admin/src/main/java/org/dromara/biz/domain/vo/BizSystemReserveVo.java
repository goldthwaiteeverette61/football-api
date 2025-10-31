package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizSystemReserve;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 系统储备金明细视图对象 biz_system_reserve
 *
 * @author Dromara
 * @date 2025-08-01
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizSystemReserve.class)
public class BizSystemReserveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 储备金记录ID
     */
    @ExcelProperty(value = "储备金记录ID")
    private Long reserveId;

    /**
     * 来源类型 (例如: scheme_payout_commission)
     */
    @ExcelProperty(value = "来源类型")
    private String sourceType;

    /**
     * 来源ID (例如: biz_user_follows表的follow_id)
     */
    @ExcelProperty(value = "来源ID")
    private String sourceId;

    /**
     * 关联的用户ID
     */
    @ExcelProperty(value = "关联的用户ID")
    private Long userId;

    /**
     * 本次存入的储备金金额 (佣金)
     */
    @ExcelProperty(value = "储备金金额")
    private BigDecimal amount;

    /**
     * 当时的佣金率 (例如: 0.1000)
     */
    @ExcelProperty(value = "佣金率")
    private BigDecimal commissionRate;

    /**
     * 用户原始应得奖金 (扣除佣金前)
     */
    @ExcelProperty(value = "原始奖金")
    private BigDecimal originalPayout;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
