package org.dromara.biz.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.biz.domain.BizDeviceUpdates;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备更新记录视图对象 biz_device_updates
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizDeviceUpdates.class)
public class BizDeviceUpdatesVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Integer id;

    /**
     * 设备唯一标识符
     */
    @ExcelProperty(value = "设备唯一标识符")
    private String deviceId;

    /**
     * 平台
     */
    @ExcelProperty(value = "平台")
    private String platform;

    /**
     * 升级前的版本号
     */
    @ExcelProperty(value = "升级前的版本号")
    private String fromVersion;

    /**
     * 目标升级版本号
     */
    @ExcelProperty(value = "目标升级版本号")
    private String toVersion;

    /**
     * 更新类型
     */
    @ExcelProperty(value = "更新类型")
    private String updateType;

    /**
     * 下载开始时间
     */
    @ExcelProperty(value = "下载开始时间")
    private Date downloadStartedAt;

    /**
     * 下载完成时间
     */
    @ExcelProperty(value = "下载完成时间")
    private Date downloadCompletedAt;

    /**
     * 安装开始时间
     */
    @ExcelProperty(value = "安装开始时间")
    private Date installStartedAt;

    /**
     * 安装完成时间
     */
    @ExcelProperty(value = "安装完成时间")
    private Date installCompletedAt;

    /**
     * 升级状态
     */
    @ExcelProperty(value = "升级状态")
    private String status;

    /**
     * 失败时的错误信息
     */
    @ExcelProperty(value = "失败时的错误信息")
    private String errorMessage;

    /**
     * 记录创建时间
     */
    @ExcelProperty(value = "记录创建时间")
    private Date createdAt;


}
