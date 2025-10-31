package org.dromara.biz.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.biz.domain.BizReferrals;
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
 * 好友推荐关系视图对象 biz_referrals
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizReferrals.class)
public class BizReferralsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 推荐人ID
     */
    @ExcelProperty(value = "推荐人ID")
    private Long referrerUserId;

    /**
     * 被推荐人ID
     */
    @ExcelProperty(value = "被推荐人ID")
    private Long referredUserId;

    /**
     * 状态(例如被推荐人完成首次存款或投注后变为completed)
     */
    @ExcelProperty(value = "状态(例如被推荐人完成首次存款或投注后变为completed)")
    private String status;

    /**
     * 是否已发放奖励
     */
    @ExcelProperty(value = "是否已发放奖励")
    private Long bonusAwarded;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private Date createdAt;


}
