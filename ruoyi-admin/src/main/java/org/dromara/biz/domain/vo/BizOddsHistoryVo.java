package org.dromara.biz.domain.vo;

import java.math.BigDecimal;
import org.dromara.biz.domain.BizOddsHistory;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 比赛赔率历史视图对象 biz_odds_history
 *
 * @author Lion Li
 * @date 2025-11-10
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizOddsHistory.class)
public class BizOddsHistoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 历史ID, 主键
     */
    @ExcelProperty(value = "历史ID, 主键")
    private Long historyId;

    /**
     * 关联的 biz_odds 主键ID
     */
    @ExcelProperty(value = "关联的 biz_odds 主键ID")
    private Long oddsId;

    /**
     * 关联的比赛ID
     */
    @ExcelProperty(value = "关联的比赛ID")
    private Integer matchId;

    /**
     * sporttery的比赛id
     */
    @ExcelProperty(value = "sporttery的比赛id")
    private Integer sportteryMatchId;

    /**
     * 赔率类型/玩法代码
     */
    @ExcelProperty(value = "赔率类型/玩法代码")
    private String poolCode;

    /**
     * 让球数
     */
    @ExcelProperty(value = "让球数")
    private String goalLine;

    /**
     * 主胜赔率
     */
    @ExcelProperty(value = "主胜赔率")
    private BigDecimal homeOdds;

    /**
     * 平局赔率
     */
    @ExcelProperty(value = "平局赔率")
    private BigDecimal drawOdds;

    /**
     * 客胜赔率
     */
    @ExcelProperty(value = "客胜赔率")
    private BigDecimal awayOdds;

    /**
     * 复杂赔率JSON数据 (CRS, TTG, HAFU)
     */
    @ExcelProperty(value = "复杂赔率JSON数据 (CRS, TTG, HAFU)")
    private String oddsData;

    /**
     * 盘口状态
     */
    @ExcelProperty(value = "盘口状态")
    private String status;

    /**
     * 是否支持单关
     */
    @ExcelProperty(value = "是否支持单关")
    private Integer single;


}
