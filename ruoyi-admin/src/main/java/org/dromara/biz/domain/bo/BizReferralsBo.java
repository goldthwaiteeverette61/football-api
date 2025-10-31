package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizReferrals;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 好友推薦關係業務對象 biz_referrals
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizReferrals.class, reverseConvertGenerate = false)
public class BizReferralsBo extends BaseEntity {

    /**
     * 
     */
    @NotNull(message = "不能爲空", groups = { EditGroup.class })
    private Long id;

    /**
     * 推薦人ID
     */
    @NotNull(message = "推薦人ID不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long referrerUserId;

    /**
     * 被推薦人ID
     */
    @NotNull(message = "被推薦人ID不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long referredUserId;

    /**
     * 狀態(例如被推薦人完成首次存款或投注後變爲completed)
     */
    private String status;

    /**
     * 是否已發放獎勵
     */
    @NotNull(message = "是否已發放獎勵不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long bonusAwarded;

    /**
     * 
     */
    private Date createdAt;


}
