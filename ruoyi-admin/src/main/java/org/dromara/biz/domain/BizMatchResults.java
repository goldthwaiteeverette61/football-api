package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 比赛赛果对象 biz_match_results
 *
 * @author Lion Li
 * @date 2025-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_match_results")
public class BizMatchResults extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    public final static String POOL_CODE_HAD = "HAD";
    public final static String POOL_CODE_HHAD = "HHAD";
    public final static String POOL_CODE_CRS = "CRS";
    public final static String POOL_CODE_TTG = "TTG";
    public final static String POOL_CODE_HAFU = "HAFU";

    /**
     * 是否已用於結算: 0-否, 1-是
     */
    private int isSettled;

    /**
     * 自增ID, 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 关联的比赛ID
     */
    private Integer matchId;

    /**
     * 玩法ID
     */
    private Long poolId;

    /**
     * 玩法代码
     */
    private String poolCode;

    /**
     * 赛果组合
     */
    private String combination;

    /**
     * 赛果组合描述)
     */
    private String combinationDesc;

    /**
     * 让球数
     */
    private String goalLine;

    /**
     * 最终赔率
     */
    private BigDecimal odds;

    /**
     * 创建时间
     */
    private Date createdAt;


}
