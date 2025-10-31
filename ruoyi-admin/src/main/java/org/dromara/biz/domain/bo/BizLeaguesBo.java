package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizLeagues;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 聯賽信息業務對象 biz_leagues
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizLeagues.class, reverseConvertGenerate = false)
public class BizLeaguesBo extends BaseEntity {

    /**
     * 聯賽ID (來自JSON中的leagueId), 主鍵
     */
    @NotBlank(message = "聯賽ID (來自JSON中的leagueId), 主鍵不能爲空", groups = { EditGroup.class })
    private String leagueId;

    /**
     * 聯賽全稱 (來自JSON中的leagueAllName)
     */
    private String name;

    /**
     * 球隊簡稱
     */
    private String abbrName;

    /**
     * 聯賽背景顏色 (來自JSON中的leagueBackColor)
     */
    private String backColor;


}
