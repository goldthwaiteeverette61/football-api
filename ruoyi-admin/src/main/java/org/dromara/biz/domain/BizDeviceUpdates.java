package org.dromara.biz.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 设备更新记录对象 biz_device_updates
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_device_updates")
public class BizDeviceUpdates extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 设备唯一标识符
     */
    private String deviceId;

    /**
     * 平台
     */
    private String platform;

    /**
     * 升级前的版本号
     */
    private String fromVersion;

    /**
     * 目标升级版本号
     */
    private String toVersion;

    /**
     * 更新类型
     */
    private String updateType;

    /**
     * 下载开始时间
     */
    private Date downloadStartedAt;

    /**
     * 下载完成时间
     */
    private Date downloadCompletedAt;

    /**
     * 安装开始时间
     */
    private Date installStartedAt;

    /**
     * 安装完成时间
     */
    private Date installCompletedAt;

    /**
     * 升级状态
     */
    private String status;

    /**
     * 失败时的错误信息
     */
    private String errorMessage;

    /**
     * 记录创建时间
     */
    private Date createdAt;


}
