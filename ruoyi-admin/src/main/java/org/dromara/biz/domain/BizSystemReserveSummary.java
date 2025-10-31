package org.dromara.biz.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 系统储备金汇总对象 biz_system_reserve_summary
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_system_reserve_summary")
public class BizSystemReserveSummary extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 汇总ID (主键)
     */
    @TableId(value = "summary_id")
    private Long summaryId;

    /**
     * 系统储备金总额
     */
    private BigDecimal totalReserveAmount;

    /**
     * 最后一次计算汇总的时间
     */
    private Date lastCalculationTime;

    /**
     * 备注
     */
    private String remark;


}
