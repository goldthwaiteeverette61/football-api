package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizSystemReserve;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 系統儲備金明細業務對象 biz_system_reserve
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizSystemReserve.class, reverseConvertGenerate = false)
public class BizSystemReserveBo extends BaseEntity {

    /**
     * 儲備金記錄ID
     */
    @NotNull(message = "儲備金記錄ID不能爲空", groups = { EditGroup.class })
    private Long reserveId;

    /**
     * 來源類型 (例如: scheme_payout_commission)
     */
    @NotBlank(message = "來源類型 (例如: scheme_payout_commission)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String sourceType;

    /**
     * 來源ID (例如: biz_user_follows表的follow_id)
     */
    @NotBlank(message = "來源ID (例如: biz_user_follows表的follow_id)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String sourceId;

    /**
     * 關聯的用戶ID
     */
    @NotNull(message = "關聯的用戶ID不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 本次存入的儲備金金額 (佣金)
     */
    @NotNull(message = "本次存入的儲備金金額 (佣金)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;

    /**
     * 當時的佣金率 (例如: 0.1000)
     */
    @NotNull(message = "當時的佣金率 (例如: 0.1000)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal commissionRate;

    /**
     * 用戶原始應得獎金 (扣除佣金前)
     */
    @NotNull(message = "用戶原始應得獎金 (扣除佣金前)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal originalPayout;

    /**
     * 備註
     */
    private String remark;


}
