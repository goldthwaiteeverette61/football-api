package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizSports;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 體育項目業務對象 biz_sports
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizSports.class, reverseConvertGenerate = false)
public class BizSportsBo extends BaseEntity {

    /**
     * 
     */
    @NotNull(message = "不能爲空", groups = { EditGroup.class })
    private Long id;

    /**
     * 體育項目名稱，如 Soccer
     */
    @NotBlank(message = "體育項目名稱，如 Soccer不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String name;


}
