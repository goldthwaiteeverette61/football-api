// ===================================================================================
// 文件路径: org/dromara/biz/domain/vo/BizMatchesVo.java
// 描述: 修改 BizMatchesVo，增加联赛、球队名称、排名以及赔率对象的字段。
// ===================================================================================
package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizMatches;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizMatches.class)
public class BizMatchesVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private Long matchId;
    private Long sportteryMatchId;
    private Integer matchNum;
    private String matchNumStr;
    private String matchWeek;
    private Date businessDate;
    private Date matchDatetime;
    private String leagueId;
    private Long homeTeamId;
    private Long awayTeamId;
    private String halfScore;
    private String fullScore;
    private String winFlag;
    private String status;
    private String sellStatus;
    private String remark;

    /**
     * 联赛简称
     */
    private String leagueName;

    /**
     * 主队排名
     */
    private String homeRank;


    /**
     * 客队排名
     */
    private String awayRank;

    /**
     * 胜平负赔率 (HAD)
     */
    private BizOddsVo had;

    /**
     * 让球胜平负赔率 (HHAD)
     */
    private BizOddsVo hhad;

    /**
     * 主场队名
     */
    private String homeTeamName;

    /**
     * 客队名称
     */
    private String awayTeamName;

    /**
     * 主场logo
     */
    private String homeTeamLogo;

    /**
     * 客队logo
     */
    private String awayTeamLogo;

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
     * 比赛名称
     */
    private String matchName;
}
