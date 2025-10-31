package org.dromara.biz.domain.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 按日期分组的比赛信息视图对象
 *
 * @author Gemini
 */
@Data
public class BizMatchesGroupVo {

    /**
     * 业务日期
     */
    private Date businessDate;

    /**
     * 星期几
     */
    private String weekday;

    /**
     * 当天的比赛列表
     */
    private List<BizMatchesVo> bizMatchesVoList;
}
