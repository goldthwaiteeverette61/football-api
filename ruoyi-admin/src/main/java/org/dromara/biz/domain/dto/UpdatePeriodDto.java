package org.dromara.biz.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.math.BigDecimal;

@Data
public class UpdatePeriodDto {

    @NotNull(message = "期數ID不能爲空")
    private Long periodId;


    @NotNull(message = "必須包含比賽詳情")
    @Size(min = 1, message = "至少需要選擇一場比賽")
    private List<SchemePeriodDetailDto> details;

    /**
     * 嵌套的內部類，用於表示單場比賽的選擇詳情
     */
    @Data
    public static class SchemePeriodDetailDto {

        private String matchName;

        @NotNull(message = "比賽ID不能爲空")
        private Long matchId;

        @NotNull(message = "玩法代碼不能爲空")
        private String poolCode;

        @NotNull(message = "投注選項不能爲空")
        private String selection;

        @NotNull(message = "賠率不能爲空")
        private BigDecimal odds;

        @NotNull(message = "讓球數不能爲空")
        private String goalLine;
    }
}
