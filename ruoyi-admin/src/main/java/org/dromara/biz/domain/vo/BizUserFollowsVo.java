package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizUserFollows;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 用户跟投记录视图对象 biz_user_follows
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizUserFollows.class)
public class BizUserFollowsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 跟投ID, 主键
     */
    @ExcelProperty(value = "跟投ID, 主键")
    private Long followId;

    /**
     * 跟投用户ID (关联 biz_users)
     */
    @ExcelProperty(value = "跟投用户ID (关联 biz_users)")
    private Long userId;

    /**
     * 跟投的期数ID (关联 biz_scheme_periods)
     */
    @ExcelProperty(value = "跟投的期数ID (关联 biz_scheme_periods)")
    private Long periodId;

    /**
     * 跟投的期数ID (关联 biz_scheme_periods)
     */
    @ExcelProperty(value = "跟投的期数名称")
    private String periodName;

    /**
     * 跟投金额 (根据期数固定)
     */
    @ExcelProperty(value = "跟投金额 (根据期数固定)")
    private BigDecimal betAmount;

    /**
     * 跟投状态: bought-已买, failed-买入失败, settled-已结算
     */
    @ExcelProperty(value = "跟投状态: bought-已买, failed-买入失败, settled-已结算")
    private String status;

    /**
     * 派奖金额 (中奖后更新)
     */
    @ExcelProperty(value = "派奖金额 (中奖后更新)")
    private Long payoutAmount;

    /**
     * 跟投时间
     */
    @ExcelProperty(value = "跟投时间")
    private Date createdAt;

    // 新增: 关联信息，用于“我的跟投记录”列表
    private String schemeTitle;


    /**
     * 关联的方案期数的状态: won, lost
     * 这个字段不存在于数据库表中，仅用于接收JOIN查询的结果。
     */
    private String periodStatus;

    /**
     * 结算时赔率
     */
    private BigDecimal payoutOdds;

    /**
     * 跟投时赔率详情 (例如: 1.38 * 1.45)
     */
    private String betOddsDesc;

    /**
     * 结算时赔率详情
     */
    private String payoutOddsDesc;

    private BizSchemePeriodsVo bizSchemePeriodsVo;

    /**
     * 新增：结算结果状态 (won, lost)
     */
    private String resultStatus;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String remark;

    /**
     * 投注类型:normal、double
     */
    private String betType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    private List<BizUserFollowDetailsVo> bizUserFollowDetailsVos;

}
