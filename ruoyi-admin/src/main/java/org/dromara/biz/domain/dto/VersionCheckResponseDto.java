package org.dromara.biz.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 版本檢查響應 DTO
 */
@Data
@NoArgsConstructor
public class VersionCheckResponseDto {

    private boolean hasUpdate = false;
    private String latestVersion;
    private String currentVersion;
    private String updateType; // optional, required, force
    private String updateSize;
    private String releaseNotes;
    private String downloadUrl;
    private String minSupportedVersion;
    private Boolean forceUpdate;
    private Date updateDeadline;
    private String checksum;

    public VersionCheckResponseDto(boolean hasUpdate, String currentVersion) {
        this.hasUpdate = hasUpdate;
        this.currentVersion = currentVersion;
    }
}
