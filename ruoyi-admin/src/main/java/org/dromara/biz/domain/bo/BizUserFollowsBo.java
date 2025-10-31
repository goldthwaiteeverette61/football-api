package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizUserFollows;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用戶跟投記錄業務對象 biz_user_follows
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizUserFollows.class, reverseConvertGenerate = false)
public class BizUserFollowsBo extends BaseEntity {

    /**
     * 用戶名稱
     */
    private String userName;

    /**
     * 跟投ID, 主鍵
     */
    @NotNull(message = "跟投ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long followId;

    /**
     * 跟投用戶ID (關聯 biz_users)
     */
    @NotNull(message = "跟投用戶ID (關聯 biz_users)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 跟投的期數ID (關聯 biz_scheme_periods)
     */
    @NotNull(message = "跟投的期數ID (關聯 biz_scheme_periods)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long periodId;

    private String periodName;

    /**
     * 跟投金額 (根據期數固定)
     */
    @NotNull(message = "跟投金額 (根據期數固定)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal betAmount;

    /**
     * 跟投狀態: bought-已買, failed-買入失敗, settled-已結算
     */
    @NotBlank(message = "跟投狀態: bought-已買, failed-買入失敗, settled-已結算不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 派獎金額 (中獎後更新)
     */
    private BigDecimal payoutAmount;

    /**
     * 跟投時間
     */
    @NotNull(message = "跟投時間不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Date createdAt;

    /**
     * 結算時賠率
     */
    private BigDecimal payoutOdds;

    /**
     * 跟投時賠率詳情 (例如: 1.38 * 1.45)
     */
    private String betOddsDesc;

    /**
     * 結算時賠率詳情
     */
    private String payoutOddsDesc;

    /**
     * 新增：結算結果狀態 (won, lost)
     */
    private String resultStatus;

    private String remark;

    /**
     * 投注類型:normal、double
     */
    private String betType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;
}
