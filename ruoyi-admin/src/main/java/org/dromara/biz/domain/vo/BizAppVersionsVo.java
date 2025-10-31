package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizAppVersions;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 应用版本信息视图对象 biz_app_versions
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizAppVersions.class)
public class BizAppVersionsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    private String currentVersion;

    /**
     * 平台 (android 或 ios)
     */
    @ExcelProperty(value = "平台 (android 或 ios)")
    private String platform;

    /**
     * 构建号
     */
    @ExcelProperty(value = "构建号")
    private Integer buildNumber;

    /**
     * APK文件大小 (单位: 字节)
     */
    @ExcelProperty(value = "APK文件大小 (单位: 字节)")
    private Long fileSize;

    /**
     * APK下载地址
     */
    private String downloadUrl;

    private String version;

    /**
     * 文件校验和 (推荐使用SHA-256)
     */
    @ExcelProperty(value = "文件校验和 (推荐使用SHA-256)")
    private String checksum;

    /**
     * 版本更新日志
     */
    @ExcelProperty(value = "版本更新日志")
    private String releaseNotes;

    /**
     * 最低支持的客户端版本
     */
    @ExcelProperty(value = "最低支持的客户端版本")
    private String minSupportedVersion;

    /**
     * 是否强制更新 (0: 否, 1: 是)
     */
    @ExcelProperty(value = "是否强制更新 (0: 否, 1: 是)")
    private Integer forceUpdate;

    /**
     * 强制更新的最后期限
     */
    @ExcelProperty(value = "强制更新的最后期限")
    private Date updateDeadline;

    /**
     * 该版本是否为活跃版本，可供检查 (0: 否, 1: 是)
     */
    @ExcelProperty(value = "该版本是否为活跃版本，可供检查 (0: 否, 1: 是)")
    private Integer isActive;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createdAt;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updatedAt;

    private Long ossId;

    private String updateType;

    private Boolean hasUpdate;
}
