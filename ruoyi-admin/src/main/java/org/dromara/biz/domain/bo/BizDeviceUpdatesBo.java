package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizDeviceUpdates;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 設備更新記錄業務對象 biz_device_updates
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizDeviceUpdates.class, reverseConvertGenerate = false)
public class BizDeviceUpdatesBo extends BaseEntity {

    /**
     * 主鍵ID
     */
    @NotNull(message = "主鍵ID不能爲空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 設備唯一標識符
     */
    @NotBlank(message = "設備唯一標識符不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String deviceId;

    /**
     * 平臺
     */
    @NotBlank(message = "平臺不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String platform;

    /**
     * 升級前的版本號
     */
    private String fromVersion;

    /**
     * 目標升級版本號
     */
    @NotBlank(message = "目標升級版本號不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String toVersion;

    /**
     * 更新類型
     */
    @NotBlank(message = "更新類型不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String updateType;

    /**
     * 下載開始時間
     */
    private Date downloadStartedAt;

    /**
     * 下載完成時間
     */
    private Date downloadCompletedAt;

    /**
     * 安裝開始時間
     */
    private Date installStartedAt;

    /**
     * 安裝完成時間
     */
    private Date installCompletedAt;

    /**
     * 升級狀態
     */
    private String status;

    /**
     * 失敗時的錯誤信息
     */
    private String errorMessage;

    /**
     * 記錄創建時間
     */
    @NotNull(message = "記錄創建時間不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Date createdAt;


}
