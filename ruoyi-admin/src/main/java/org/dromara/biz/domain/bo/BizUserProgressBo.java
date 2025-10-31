package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizUserProgress;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 用戶跟投進度業務對象 biz_user_progress
 *
 * @author Lion Li
 * @date 2025-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizUserProgress.class, reverseConvertGenerate = false)
public class BizUserProgressBo extends BaseEntity {

    /**
     * 用戶名稱
     */
    private String userName;

    /**
     * 進度記錄的唯一ID, 主鍵
     */
    @NotNull(message = "進度記錄的唯一ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long progressId;

    /**
     * 關聯的用戶ID (sys_user.user_id)
     */
    @NotNull(message = "關聯的用戶ID (sys_user.user_id)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 當前的連續失敗次數，默認爲 0
     */
    @NotNull(message = "當前的連續失敗次數，默認爲 0不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Integer consecutiveLosses;

    /**
     * 新增：是否可領取理賠金
     */
    private Integer canClaimReward;

    /**
     * 累計連輸金額
     */
    private BigDecimal consecutiveLossesAmount;

    /**
     * 投注類型:normal、double
     */
    private String betType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;


    /**
     * 本輪投注
     */
    private BigDecimal betAmount;

    /**
     * 上一次投注金額
     */
    private BigDecimal lastBetAmount;


}
