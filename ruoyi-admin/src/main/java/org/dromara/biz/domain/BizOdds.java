package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 比赛赔率对象 biz_odds
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_odds")
public class BizOdds extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增ID, 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 关联的比赛ID
     */
    private Long matchId;

    private Long sportteryMatchId;

    /**
     * 赔率类型/玩法代码 (例如 "HAD", "HHAD")
     */
    private String poolCode;

    /**
     * 让球数 (来自JSON中的goalLine, 例如 "+1")
     */
    private String goalLine;

    /**
     * 主胜赔率 (来自JSON中的h)
     */
    private BigDecimal homeOdds;

    /**
     * 平局赔率 (来自JSON中的d)
     */
    private BigDecimal drawOdds;

    /**
     * 客胜赔率 (来自JSON中的a)
     */
    private BigDecimal awayOdds;

    /**
     * 盘口状态 (来自JSON中的poolStatus)
     */
    private String status;

    /**
     * 赔率更新时间
     */
    private Date updatedAt;

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
