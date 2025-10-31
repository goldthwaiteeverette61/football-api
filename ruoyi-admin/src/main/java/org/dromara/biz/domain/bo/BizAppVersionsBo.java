package org.dromara.biz.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.biz.domain.BizAppVersions;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 應用版本信息業務對象 biz_app_versions
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizAppVersions.class, reverseConvertGenerate = false)
public class BizAppVersionsBo extends BaseEntity {

    /**
     * 主鍵ID
     */
    private Long id;

    /**
     * 平臺 (android 或 ios)
     */
    private String platform;

    /**
     * 構建號
     */
    private Integer buildNumber;

    /**
     * APK文件大小 (單位: 字節)
     */
    private Long fileSize;

    /**
     * APK下載地址
     */
    private String downloadUrl;

    /**
     * 文件校驗和 (推薦使用SHA-256)
     */
    private String checksum;

    /**
     * 版本更新日誌
     */
    private String releaseNotes;

    /**
     * 最低支持的客戶端版本
     */
    private String minSupportedVersion;

    /**
     * 是否強制更新 (0: 否, 1: 是)
     */
    private Integer forceUpdate;

    /**
     * 強制更新的最後期限
     */
    private Date updateDeadline;

    /**
     * 該版本是否爲活躍版本，可供檢查 (0: 否, 1: 是)
     */
    private Integer isActive;

    /**
     * 創建時間
     */
    private Date createdAt;

    /**
     * 更新時間
     */
    private Date updatedAt;

    private String version;

    private Long ossId;
}
