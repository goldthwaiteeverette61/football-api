package org.dromara.biz.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.biz.domain.BizSystemReserveSummary;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 系统储备金汇总视图对象 biz_system_reserve_summary
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizSystemReserveSummary.class)
public class BizSystemReserveSummaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 汇总ID (主键)
     */
    @ExcelProperty(value = "汇总ID (主键)")
    private Long summaryId;

    /**
     * 系统储备金总额
     */
    @ExcelProperty(value = "系统储备金总额")
    private BigDecimal totalReserveAmount;

    /**
     * 最后一次计算汇总的时间
     */
    @ExcelProperty(value = "最后一次计算汇总的时间")
    private Date lastCalculationTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
