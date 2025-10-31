package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizMatchResults;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 比賽賽果業務對象 biz_match_results
 *
 * @author Lion Li
 * @date 2025-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizMatchResults.class, reverseConvertGenerate = false)
public class BizMatchResultsBo extends BaseEntity {

    /**
     * 是否已用於結算: 0-否, 1-是
     */
    private int isSettled;

    /**
     * 自增ID, 主鍵
     */
    @NotNull(message = "自增ID, 主鍵不能爲空", groups = { EditGroup.class })
    private Long id;

    /**
     * 關聯的比賽ID
     */
    @NotNull(message = "關聯的比賽ID 不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long matchId;

    /**
     * 玩法ID
     */
    private Long poolId;

    /**
     * 玩法代碼
     */
    private String poolCode;

    /**
     * 賽果組合
     */
    private String combination;

    /**
     * 賽果組合描述)
     */
    private String combinationDesc;

    /**
     * 讓球數
     */
    private String goalLine;

    /**
     * 最終賠率
     */
    private BigDecimal odds;

    /**
     * 創建時間
     */
    @NotNull(message = "創建時間不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Date createdAt;


}
