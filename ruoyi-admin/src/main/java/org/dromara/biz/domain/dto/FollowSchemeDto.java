
// 文件路徑: org/dromara/biz/domain/dto/FollowSchemeDto.java
package org.dromara.biz.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FollowSchemeDto {
    @NotNull(message = "方案期數ID不能爲空")
    private Long periodId;

    private Long userId;

    /**
     * 投注金額
     */
    private BigDecimal betAmount;

}
