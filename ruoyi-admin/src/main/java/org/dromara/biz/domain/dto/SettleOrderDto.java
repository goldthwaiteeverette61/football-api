package org.dromara.biz.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 结算订单 DTO
 * 用于接收外部传入的比赛结果以结算特定订单
 */
@Data
public class SettleOrderDto {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotEmpty(message = "赛果列表不能为空")
    @Valid
    private List<MatchResultDto> results;

    /**
     * 内部类，定义单个赛果的结构
     */
    @Data
    public static class MatchResultDto {

        @NotNull(message = "比赛ID不能为空")
        private Long matchId;

        @NotBlank(message = "玩法代码不能为空")
        private String poolCode;

        @NotBlank(message = "中奖选项不能为空")
        private String winningSelection;
    }
}
