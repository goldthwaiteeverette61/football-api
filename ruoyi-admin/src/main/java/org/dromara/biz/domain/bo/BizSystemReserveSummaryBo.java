package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizSystemReserveSummary;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 系統儲備金彙總業務對象 biz_system_reserve_summary
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizSystemReserveSummary.class, reverseConvertGenerate = false)
public class BizSystemReserveSummaryBo extends BaseEntity {

    /**
     * 彙總ID (主鍵)
     */
    @NotNull(message = "彙總ID (主鍵)不能爲空", groups = { EditGroup.class })
    private Long summaryId;

    /**
     * 系統儲備金總額
     */
    @NotNull(message = "系統儲備金總額不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal totalReserveAmount;

    /**
     * 最後一次計算彙總的時間
     */
    private Date lastCalculationTime;

    /**
     * 備註
     */
    private String remark;


}
