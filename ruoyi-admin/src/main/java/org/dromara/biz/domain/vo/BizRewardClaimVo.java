package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizRewardClaim;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 理赔申请视图对象 biz_reward_claim
 *
 * @author Lion Li
 * @date 2025-08-18
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizRewardClaim.class)
public class BizRewardClaimVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 申请用户ID
     */
    @ExcelProperty(value = "申请用户ID")
    private Long userId;

    /**
     * 申请金额
     */
    @ExcelProperty(value = "申请金额")
    private BigDecimal amount;

    /**
     * 货币类型
     */
    @ExcelProperty(value = "货币类型")
    private String currency;

    /**
     * 状态 (PENDING, APPROVED, REJECTED)
     */
    @ExcelProperty(value = "状态 (PENDING, APPROVED, REJECTED)")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remarks;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 失败次数
     */
    private Integer lostCount;

    /**
     * 业务编号
     */
    private String bizCode;
}
