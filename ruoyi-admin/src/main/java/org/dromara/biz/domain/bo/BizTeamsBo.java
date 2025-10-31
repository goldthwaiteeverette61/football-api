package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizTeams;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 球隊信息業務對象 biz_teams
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizTeams.class, reverseConvertGenerate = false)
public class BizTeamsBo extends BaseEntity {

    /**
     * 球隊ID (來自JSON中的homeTeamId/awayTeamId), 主鍵
     */
    @NotNull(message = "球隊ID (來自JSON中的homeTeamId/awayTeamId), 主鍵不能爲空", groups = { EditGroup.class })
    private Long teamId;

    /**
     * 球隊全稱 (來自JSON中的allHomeTeam/allAwayTeam)
     */
    private String fullName;

    /**
     * 球隊簡稱 (來自JSON中的homeTeam/awayTeam)
     */
    private String abbrName;

    /**
     * 球隊排名
     */
    private String ranks;

    /**
     * 球隊logo圖標地址
     */
    private String logo;

    private String code;

    private Integer liveSportId;

}
