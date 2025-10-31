// ===================================================================================
// 模塊: DTO (Data Transfer Objects)
// 描述: 用於接收前端的特定業務請求。
// ===================================================================================

// 文件路徑: org/dromara/biz/domain/dto/SchemeCreationDto.java
package org.dromara.biz.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SchemeCreationDto {

    @NotBlank(message = "方案標題不能爲空")
    private String title;

    @NotEmpty(message = "必須至少選擇一場比賽")
    private List<SchemeDetailDto> details;

    @Data
    public static class SchemeDetailDto {
        @NotNull(message = "比賽ID不能爲空")
        private Long matchId;
        @NotBlank(message = "玩法代碼不能爲空")
        private String poolCode;
        @NotBlank(message = "投注選項不能爲空")
        private String selection;
        @NotNull(message = "賠率不能爲空")
        private BigDecimal odds;
        @NotBlank(message = "讓球數")
        private String goalLine;
    }
}
