package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizUserProgress;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;



/**
 * 用户跟投进度视图对象 biz_user_progress
 *
 * @author Lion Li
 * @date 2025-08-12
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizUserProgress.class)
public class BizUserProgressVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 进度记录的唯一ID, 主键
     */
    @ExcelProperty(value = "进度记录的唯一ID, 主键")
    private Long progressId;

    /**
     * 关联的用户ID (sys_user.user_id)
     */
    @ExcelProperty(value = "关联的用户ID (sys_user.user_id)")
    private Long userId;

    /**
     * 当前的连续失败次数，默认为 0
     */
    @ExcelProperty(value = "当前的连续失败次数，默认为 0")
    private Integer consecutiveLosses;

    /**
     * 新增：是否可领取理赔金
     */
    private Integer canClaimReward;

    /**
     * 累计连输金额
     */
    private BigDecimal consecutiveLossesAmount;

    /**
     * 投注类型:normal、double
     */
    private String betType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    /**
     * 本轮投注
     */
    private BigDecimal betAmount;

    /**
     * 上一次投注金额
     */
    private BigDecimal lastBetAmount;

}
