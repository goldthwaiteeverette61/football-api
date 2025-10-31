package org.dromara.biz.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 应用版本信息对象 biz_app_versions
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_app_versions")
public class BizAppVersions extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 平台 (android 或 ios)
     */
    private String platform;

    /**
     * 版本号 (例如: 1.0.1)
     */
    @Version
    private String version;

    /**
     * 构建号
     */
    private Integer buildNumber;

    /**
     * APK文件大小 (单位: 字节)
     */
    private Long fileSize;

    /**
     * APK下载地址
     */
    private String downloadUrl;

    /**
     * 文件校验和 (推荐使用SHA-256)
     */
    private String checksum;

    /**
     * 版本更新日志
     */
    private String releaseNotes;

    /**
     * 最低支持的客户端版本
     */
    private String minSupportedVersion;

    /**
     * 是否强制更新 (0: 否, 1: 是)
     */
    private Integer forceUpdate;

    /**
     * 强制更新的最后期限
     */
    private Date updateDeadline;

    /**
     * 该版本是否为活跃版本，可供检查 (0: 否, 1: 是)
     */
    private Integer isActive;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    private Long ossId;

}
