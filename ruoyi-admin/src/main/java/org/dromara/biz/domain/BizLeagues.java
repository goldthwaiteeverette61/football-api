package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 联赛信息对象 biz_leagues
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_leagues")
public class BizLeagues extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 联赛ID (来自JSON中的leagueId), 主键
     */
    @TableId(value = "league_id")
    private String leagueId;

    /**
     * 联赛全称 (来自JSON中的leagueAllName)
     */
    private String name;

    /**
     * 球队简称
     */
    private String abbrName;

    /**
     * 联赛背景颜色 (来自JSON中的leagueBackColor)
     */
    private String backColor;


}
