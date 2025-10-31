package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 球队信息对象 biz_teams
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_teams")
public class BizTeams extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 球队ID (来自JSON中的homeTeamId/awayTeamId), 主键
     */
    @TableId(value = "team_id")
    private Long teamId;

    /**
     * 球队全称 (来自JSON中的allHomeTeam/allAwayTeam)
     */
    private String fullName;

    /**
     * 球队简称 (来自JSON中的homeTeam/awayTeam)
     */
    private String abbrName;

    /**
     * 球队排名
     */
    private String ranks;


    /**
     * 球队logo图标地址
     */
    private String logo;

    private String code;

    private Integer liveSportId;

}
