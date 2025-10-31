package org.dromara.biz.domain.vo;

import org.dromara.biz.domain.BizLeagues;
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
 * 联赛信息视图对象 biz_leagues
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizLeagues.class)
public class BizLeaguesVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 联赛ID (来自JSON中的leagueId), 主键
     */
    @ExcelProperty(value = "联赛ID (来自JSON中的leagueId), 主键")
    private String leagueId;

    /**
     * 联赛全称 (来自JSON中的leagueAllName)
     */
    @ExcelProperty(value = "联赛全称 (来自JSON中的leagueAllName)")
    private String name;

    /**
     * 球队简称
     */
    @ExcelProperty(value = "球队简称")
    private String abbrName;

    /**
     * 联赛背景颜色 (来自JSON中的leagueBackColor)
     */
    @ExcelProperty(value = "联赛背景颜色 (来自JSON中的leagueBackColor)")
    private String backColor;


}
