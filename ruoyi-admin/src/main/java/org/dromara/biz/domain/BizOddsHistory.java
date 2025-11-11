package org.dromara.biz.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;

/**
 * 比赛赔率历史对象 biz_odds_history
 *
 * @author Lion Li
 * @date 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_odds_history")
public class BizOddsHistory extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 历史ID, 主键
     */
    @TableId(value = "history_id")
    private Long historyId;

    /**
     * 关联的 biz_odds 主键ID
     */
    private Long oddsId;

    /**
     * 关联的比赛ID
     */
    private Integer matchId;

    /**
     * sporttery的比赛id
     */
    private Integer sportteryMatchId;

    /**
     * 赔率类型/玩法代码
     */
    private String poolCode;

    /**
     * 让球数
     */
    private String goalLine;

    /**
     * 主胜赔率
     */
    private BigDecimal homeOdds;

    /**
     * 平局赔率
     */
    private BigDecimal drawOdds;

    /**
     * 客胜赔率
     */
    private BigDecimal awayOdds;

    /**
     * 复杂赔率JSON数据 (CRS, TTG, HAFU)
     */
    private String oddsData;

    /**
     * 盘口状态
     */
    private String status;

    /**
     * 是否支持单关
     */
    private Integer single;


}
