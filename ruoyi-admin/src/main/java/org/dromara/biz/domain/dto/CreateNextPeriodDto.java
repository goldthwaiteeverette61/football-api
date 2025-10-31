package org.dromara.biz.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateNextPeriodDto {
    @NotNull(message = "方案ID不能爲空")
    private Long schemeId;

    @NotEmpty(message = "必須至少選擇一場比賽")
    private List<SchemeCreationDto.SchemeDetailDto> details;
}
