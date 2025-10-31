package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizTeams;

import java.io.Serial;
import java.io.Serializable;



/**
 * 球队信息视图对象 biz_teams
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizTeams.class)
public class BizTeamsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 球队ID (来自JSON中的homeTeamId/awayTeamId), 主键
     */
    @ExcelProperty(value = "球队ID (来自JSON中的homeTeamId/awayTeamId), 主键")
    private Long teamId;

    /**
     * 球队全称 (来自JSON中的allHomeTeam/allAwayTeam)
     */
    @ExcelProperty(value = "球队全称 (来自JSON中的allHomeTeam/allAwayTeam)")
    private String fullName;

    /**
     * 球队简称 (来自JSON中的homeTeam/awayTeam)
     */
    @ExcelProperty(value = "球队简称 (来自JSON中的homeTeam/awayTeam)")
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
