package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizUserFollowDetails;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 用戶跟投詳情業務對象 biz_user_follow_details
 *
 * @author Lion Li
 * @date 2025-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizUserFollowDetails.class, reverseConvertGenerate = false)
public class BizUserFollowDetailsBo extends BaseEntity {

    /**
     * 詳情ID, 主鍵
     */
    @NotNull(message = "詳情ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long followDetailId;

    /**
     * 所屬跟投ID (關聯 biz_user_follows)
     */
    @NotNull(message = "所屬跟投ID (關聯 biz_user_follows)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long followId;

    /**
     * 方案期數id
     */
    private Long periodId;

    /**
     * biz_scheme_period_details表的id
     */
    private Long periodDetailsId;

    /**
     * 選擇時的賠率
     */
    @NotNull(message = "選擇時的賠率不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal odds;

    /**
     * 比賽ID (關聯 biz_matches)
     */
    private Long matchId;

    /**
     * 比賽名稱 (冗餘)
     */
    private String matchName;

    /**
     * 玩法代碼 (冗餘)
     */
    private String poolCode;

    /**
     * 投注選擇 (冗餘)
     */
    private String selection;

    /**
     * 讓球數 (冗餘)
     */
    private String goalLine;

}
