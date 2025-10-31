package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizOdds;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 比赛赔率视图对象 biz_odds
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizOdds.class)
public class BizOddsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增ID, 主键
     */
    @ExcelProperty(value = "自增ID, 主键")
    private Long id;

    /**
     * 关联的比赛ID
     */
    @ExcelProperty(value = "关联的比赛ID")
    private Long matchId;

    private Long sportteryMatchId;

    /**
     * 赔率类型/玩法代码 (例如 "HAD", "HHAD")
     */
    @ExcelProperty(value = "赔率类型/玩法代码 (例如 HAD, HHAD)")
    private String poolCode;

    /**
     * 让球数 (来自JSON中的goalLine, 例如 "+1")
     */
    @ExcelProperty(value = "让球数 (来自JSON中的goalLine, 例如 +1)")
    private String goalLine;

    /**
     * 主胜赔率 (来自JSON中的h)
     */
    @ExcelProperty(value = "主胜赔率 (来自JSON中的h)")
    private BigDecimal homeOdds;

    /**
     * 平局赔率 (来自JSON中的d)
     */
    @ExcelProperty(value = "平局赔率 (来自JSON中的d)")
    private BigDecimal drawOdds;

    /**
     * 客胜赔率 (来自JSON中的a)
     */
    @ExcelProperty(value = "客胜赔率 (来自JSON中的a)")
    private BigDecimal awayOdds;

    /**
     * 盘口状态 (来自JSON中的poolStatus)
     */
    @ExcelProperty(value = "盘口状态 (来自JSON中的poolStatus)")
    private String status;

    /**
     * 赔率更新时间
     */
    @ExcelProperty(value = "赔率更新时间")
    private Date updatedAt;

    // ↓↓↓↓↓↓ 新增以下字段以满足计算器页面的需求 ↓↓↓↓↓↓

    /**
     * 联赛简称
     */
    @ExcelProperty(value = "联赛简称")
    private String leagueName;

    /**
     * 主队简称
     */
    @ExcelProperty(value = "主队简称")
    private String homeTeamName;

    /**
     * 主队排名
     */
    @ExcelProperty(value = "主队排名")
    private String homeRank;

    /**
     * 客队简称
     */
    @ExcelProperty(value = "客队简称")
    private String awayTeamName;

    /**
     * 客队排名
     */
    @ExcelProperty(value = "客队排名")
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
     * 是否支持单关 (1:是, 0:否)
     */
    private Integer single;

    /**
     * 【核心新增】复杂赔率JSON数据 (CRS, TTG, HAFU)
     */
    @TableField(value = "odds_data")
    private String oddsData;

}
