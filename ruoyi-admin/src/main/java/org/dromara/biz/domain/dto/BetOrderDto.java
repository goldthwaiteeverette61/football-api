package org.dromara.biz.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 投注订单数据传输对象
 * 用于接收前端提交的完整投注信息
 */
@Data
public class BetOrderDto {

    @NotNull(message = "总投注金额不能为空")
    @DecimalMin(value = "0.01", message = "投注金额必须大于0")
    private BigDecimal betAmount;

    @NotBlank(message = "过关类型不能为空")
    private String combinationType;

    @NotEmpty(message = "投注选项不能为空")
    @Valid // 关键注解：这会触发对列表中每个 BetDetailDto 对象的校验
    private List<BetDetailDto> details;

    /**
     * 内部类，定义每个投注选项的结构
     */
    @Data
    public static class BetDetailDto {
        @NotNull(message = "比赛ID不能为空")
        private Long matchId;

        @NotBlank(message = "玩法代码不能为空")
        private String poolCode;

        @NotBlank(message = "投注选择不能为空")
        private String selection;
    }
}
