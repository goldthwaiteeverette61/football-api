package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizOddsHistory;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 比赛赔率历史业务对象 biz_odds_history
 *
 * @author Lion Li
 * @date 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizOddsHistory.class, reverseConvertGenerate = false)
public class BizOddsHistoryBo extends BaseEntity {

    /**
     * 历史ID, 主键
     */
    @NotNull(message = "历史ID, 主键不能为空", groups = { EditGroup.class })
    private Long historyId;

    /**
     * 关联的 biz_odds 主键ID
     */
    @NotNull(message = "关联的 biz_odds 主键ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long oddsId;

    /**
     * 关联的比赛ID
     */
    @NotNull(message = "关联的比赛ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer matchId;

    /**
     * sporttery的比赛id
     */
    private Integer sportteryMatchId;

    /**
     * 赔率类型/玩法代码
     */
    @NotBlank(message = "赔率类型/玩法代码不能为空", groups = { AddGroup.class, EditGroup.class })
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
