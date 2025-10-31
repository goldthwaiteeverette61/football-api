package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户跟投记录对象 biz_user_follows
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_follows")
public class BizUserFollows extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 跟投ID, 主键
     */
    @TableId(value = "follow_id")
    private Long followId;

    /**
     * 跟投用户ID (关联 biz_users)
     */
    private Long userId;

    /**
     * 跟投的期数ID (关联 biz_scheme_periods)
     */
    private Long periodId;

    private String periodName;

    /**
     * 跟投金额 (根据期数固定)
     */
    private BigDecimal betAmount;

    /**
     * 跟投状态: bought-已买, failed-买入失败, settled-已结算
     */
    private String status;

    private String remark;


    /**
     * 派奖金额 (中奖后更新)
     */
    private BigDecimal payoutAmount;

    /**
     * 跟投时间
     */
    private Date createdAt;

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

    /**
     * 新增：结算结果状态 (won, lost)
     */
    private String resultStatus;

    /**
     * 投注类型:normal、double
     */
    private String betType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    /**
     * 用户名称
     */
    private String userName;

}
