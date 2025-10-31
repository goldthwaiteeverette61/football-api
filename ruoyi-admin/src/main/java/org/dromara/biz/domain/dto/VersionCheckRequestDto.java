package org.dromara.biz.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 版本檢查請求 DTO
 */
@Data
public class VersionCheckRequestDto {

    /**
     * 平臺 (android, ios)
     */
    @NotBlank(message = "平臺信息不能爲空")
    private String platform;

    /**
     * App 當前版本號
     */
    @NotBlank(message = "當前版本號不能爲空")
    private String currentVersion;

    /**
     * 設備唯一標識
     */
    @NotBlank(message = "設備ID不能爲空")
    private String deviceId;
}
