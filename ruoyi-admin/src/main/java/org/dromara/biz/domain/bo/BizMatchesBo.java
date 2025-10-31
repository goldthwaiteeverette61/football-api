package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizMatches;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 比賽信息業務對象 biz_matches
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizMatches.class, reverseConvertGenerate = false)
public class BizMatchesBo extends BaseEntity {


    /**
     * 比賽ID (來自JSON中的matchId), 主鍵
     */
    @NotNull(message = "比賽ID (來自JSON中的matchId), 主鍵不能爲空", groups = { EditGroup.class })
    private Long matchId;

    private Long sportteryMatchId;

    /**
     * 比賽數字編號 (來自JSON中的matchNum)
     */
    private Integer matchNum;

    /**
     * 比賽編號字符串 (來自JSON中的matchNumStr, 例如 "週三001")
     */
    private String matchNumStr;

    /**
     * 比賽周次 (來自JSON中的matchWeek)
     */
    private String matchWeek;

    /**
     * 如果爲 true, 則查詢時排除 'Payout' 狀態
     */
    private Boolean geBusinessDate;

    /**
     * 業務日期 (來自JSON中的businessDate)
     */
    private Date businessDate;

    /**
     * 如果爲 true, 則查詢時排除 'Payout' 狀態
     */
    private Boolean geMatchDatetime;

    /**
     * 比賽開賽時間 (可由 matchDate 和 matchTime 組合)
     */
    private Date matchDatetime;

    /**
     * 聯賽ID (來自JSON中的leagueId)
     */
    private String leagueId;

    /**
     * 联赛简称
     */
    private String leagueName;

    /**
     * 主隊ID (來自JSON中的homeTeamId)
     */
    private Long homeTeamId;

    /**
     * 客隊ID (來自JSON中的awayTeamId)
     */
    private Long awayTeamId;

    /**
     * 上半場比分 (來自JSON中的sectionsNo1)
     */
    private String halfScore;

    /**
     * 全場比分 (來自JSON中的sectionsNo999)
     */
    private String fullScore;

    /**
     * 賽果標識 (來自JSON中的winFlag, H/D/A)
     */
    private String winFlag;

    /**
     * 比賽狀態 (來自JSON中的matchStatus, 例如 "Define")
     */
    private String status;

    /**
     * 銷售狀態 (來自JSON中的sellStatus)
     */
    private String sellStatus;

    /**
     * 備註 (來自JSON中的remark)
     */
    private String remark;

    /**
     * 數據入庫時間
     */
    @NotNull(message = "數據入庫時間不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Date createdAt;

    /**
     * 數據更新時間
     */
    @NotNull(message = "數據更新時間不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Date updatedAt;

    /**
     * 主場隊名
     */
    private String homeTeamName;

    /**
     * 客隊名稱
     */
    private String awayTeamName;

    /**
     * 主隊logo
     */
    private String homeTeamLogo;

    /**
     * 客隊logo
     */
    private String awayTeamLogo;

    /**
     * 如果爲 true, 則查詢時排除 'Payout' 狀態
     */
    private Boolean excludePayout;

    /**
     * 比賽時長
     */
    private String matchMinute;

    /**
     * 比賽狀態，5：進行中；6，結束。
     */
    private String matchStatus;

    /**
     * 比賽階段：10，中場休息
     */
    private String matchPhaseTc;

    /**
     * 比賽階段
     */
    private String matchPhaseTcName;

    /**
     * 比賽名稱
     */
    private String matchName;
}
