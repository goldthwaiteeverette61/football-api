package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 比赛信息对象 biz_matches
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_matches")
public class BizMatches extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 比赛ID (来自JSON中的matchId), 主键
     */
    @TableId(value = "match_id")
    private Long matchId;

    private Long sportteryMatchId;

    /**
     * 比赛数字编号 (来自JSON中的matchNum)
     */
    private Long matchNum;

    /**
     * 比赛编号字符串 (来自JSON中的matchNumStr, 例如 "周三001")
     */
    private String matchNumStr;

    /**
     * 比赛周次 (来自JSON中的matchWeek)
     */
    private String matchWeek;

    /**
     * 业务日期 (来自JSON中的businessDate)
     */
    private Date businessDate;

    /**
     * 比赛开赛时间 (可由 matchDate 和 matchTime 组合)
     */
    private Date matchDatetime;

    /**
     * 联赛ID (来自JSON中的leagueId)
     */
    private String leagueId;

    /**
     * 联赛简称
     */
    private String leagueName;

    /**
     * 主队ID (来自JSON中的homeTeamId)
     */
    private Long homeTeamId;

    /**
     * 客队ID (来自JSON中的awayTeamId)
     */
    private Long awayTeamId;

    /**
     * 上半场比分 (来自JSON中的sectionsNo1)
     */
    private String halfScore;

    /**
     * 全场比分 (来自JSON中的sectionsNo999)
     */
    private String fullScore;

    /**
     * 赛果标识 (来自JSON中的winFlag, H/D/A)
     */
    private String winFlag;

    /**
     * 比赛状态 (来自JSON中的matchStatus, 例如 "Define")
     */
    private String status;

    /**
     * 比赛时长
     */
    private String matchMinute;

    /**
     * 比赛状态，5：进行中；6，结束。
     */
    private String matchStatus;


    /**
     * 比赛阶段：10，中场休息
     */
    private String matchPhaseTc;

    /**
     * 比赛阶段
     */
    private String matchPhaseTcName;

    /**
     * 销售状态 (来自JSON中的sellStatus)
     */
    private String sellStatus;

    /**
     * 备注 (来自JSON中的remark)
     */
    private String remark;

    /**
     * 数据入库时间
     */
    private Date createdAt;

    /**
     * 数据更新时间
     */
    private Date updatedAt;

    /**
     * 主场队名
     */
    private String homeTeamName;

    /**
     * 客队名称
     */
    private String awayTeamName;

    private String homeTeamLogo;

    /**
     * 客队logo
     */
    private String awayTeamLogo;

    /**
     * 比赛名称
     */
    private String matchName;


}
