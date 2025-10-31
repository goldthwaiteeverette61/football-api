package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizOdds;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * 比賽賠率業務對象 biz_odds
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizOdds.class, reverseConvertGenerate = false)
public class BizOddsBo extends BaseEntity {

    /**
     * 自增ID, 主鍵
     */
    @NotNull(message = "自增ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long id;

    /**
     * 關聯的比賽ID
     */
    @NotNull(message = "關聯的比賽ID不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long matchId;

    private Long sportteryMatchId;

    /**
     * 賠率類型/玩法代碼 (例如 "HAD", "HHAD")
     */
    @NotBlank(message = "賠率類型/玩法代碼 (例如 HAD, HHAD)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String poolCode;

    /**
     * 讓球數 (來自JSON中的goalLine, 例如 "+1")
     */
    private String goalLine;

    /**
     * 主勝賠率 (來自JSON中的h)
     */
    private BigDecimal homeOdds;

    /**
     * 平局賠率 (來自JSON中的d)
     */
    private BigDecimal drawOdds;

    /**
     * 客勝賠率 (來自JSON中的a)
     */
    private BigDecimal awayOdds;

    /**
     * 盤口狀態 (來自JSON中的poolStatus)
     */
    private String status;

    /**
     * 賠率更新時間
     */
    @NotNull(message = "賠率更新時間不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Date updatedAt;

    /**
     * 用於IN查詢的比賽ID集合
     */
    private Collection<Long> matchIds;

    /**
     * 用於IN查詢的玩法代碼集合
     */
    private Collection<String> poolCodes;


    /**
     * 是否支持單關 (1:是, 0:否)
     */
    private Integer single;

    private String oddsData;
}
