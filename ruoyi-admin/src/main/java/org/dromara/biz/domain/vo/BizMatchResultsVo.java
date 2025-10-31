package org.dromara.biz.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.biz.domain.BizMatchResults;
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
 * 比赛赛果视图对象 biz_match_results
 *
 * @author Lion Li
 * @date 2025-08-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizMatchResults.class)
public class BizMatchResultsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 是否已用於結算: 0-否, 1-是
     */
    private int isSettled;

    /**
     * 自增ID, 主键
     */
    @ExcelProperty(value = "自增ID, 主键")
    private Long id;

    /**
     * 关联的比赛ID
     */
    @ExcelProperty(value = "关联的比赛ID ")
    private Integer matchId;

    /**
     * 玩法ID
     */
    @ExcelProperty(value = "玩法ID ")
    private Long poolId;

    /**
     * 玩法代码
     */
    @ExcelProperty(value = "玩法代码 ")
    private String poolCode;

    /**
     * 赛果组合
     */
    @ExcelProperty(value = "赛果组合 ")
    private String combination;

    /**
     * 赛果组合描述)
     */
    @ExcelProperty(value = "赛果组合描述)")
    private String combinationDesc;

    /**
     * 让球数
     */
    @ExcelProperty(value = "让球数 ")
    private String goalLine;

    /**
     * 最终赔率
     */
    @ExcelProperty(value = "最终赔率")
    private BigDecimal odds;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createdAt;

    @ExcelProperty(value = "比赛编号")
    private String matchNumStr;

    @ExcelProperty(value = "主队名称")
    private String homeTeamName;

    @ExcelProperty(value = "客队名称")
    private String awayTeamName;

    @ExcelProperty(value = "全场比分")
    private String fullScore;



}
