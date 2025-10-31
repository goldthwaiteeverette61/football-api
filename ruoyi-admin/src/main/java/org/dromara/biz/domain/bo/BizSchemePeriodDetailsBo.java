package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizSchemePeriodDetails;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 方案期數詳情業務對象 biz_scheme_period_details
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizSchemePeriodDetails.class, reverseConvertGenerate = false)
public class BizSchemePeriodDetailsBo extends BaseEntity {

    /**
     * 詳情ID, 主鍵
     */
    @NotNull(message = "詳情ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long detailId;

    /**
     * 所屬期數ID (關聯 biz_scheme_periods)
     */
    @NotNull(message = "所屬期數ID (關聯 biz_scheme_periods)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long periodId;

    /**
     * 比賽ID (關聯 biz_matches)
     */
    @NotNull(message = "比賽ID (關聯 biz_matches)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long matchId;

    private String matchName;

    /**
     * 玩法代碼 (例如: HAD, HHAD)
     */
    @NotBlank(message = "玩法代碼 (例如: HAD, HHAD)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String poolCode;

    /**
     * 投注選擇 (例如: H, A, D)
     */
    @NotBlank(message = "投注選擇 (例如: H, A, D)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String selection;

    /**
     * 選擇時的賠率
     */
    @NotNull(message = "選擇時的賠率不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal odds;

    private List<Long> periodIds;

    /**
     * 讓球數
     */
    private String goalLine;
}
