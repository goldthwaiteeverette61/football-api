package org.dromara.biz.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 批量更新赔率数据传输对象
 */
@Data
public class BatchUpdateOddsDto {

    @NotEmpty(message = "更新详情不能为空")
    @Valid
    private List<Detail> details;

    @Data
    public static class Detail {
        @NotNull(message = "详情ID不能为空")
        private Long detailId;

        @NotNull(message = "赔率不能为空")
        private BigDecimal odds;
    }
}
