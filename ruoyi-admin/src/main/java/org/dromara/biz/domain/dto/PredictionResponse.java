package org.dromara.biz.domain.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Gemini 预测响应的 DTO
 * 用于强制 Gemini 返回我们需要的 JSON 结构
 */
@Data
public class PredictionResponse {

    @SerializedName("match_id")
    private Long matchId;

    @SerializedName("had_prediction")
    private String hadPrediction; // "H", "D", "A"

    @SerializedName("had_confidence")
    private int hadConfidence; // 0-100

    @SerializedName("hhad_prediction")
    private String hhadPrediction; // "H", "D", "A"

    @SerializedName("hhad_confidence")
    private int hhadConfidence; // 0-100

    @SerializedName("analysis_summary")
    private String analysisSummary; // AI 的分析摘要
}
