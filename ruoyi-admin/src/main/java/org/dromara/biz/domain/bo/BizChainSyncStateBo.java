package org.dromara.biz.domain.bo;

import org.dromara.biz.domain.BizChainSyncState;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 區塊鏈同步狀態業務對象 biz_chain_sync_state
 *
 * @author Lion Li
 * @date 2025-09-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizChainSyncState.class, reverseConvertGenerate = false)
public class BizChainSyncStateBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能爲空", groups = { EditGroup.class })
    private Long id;

    /**
     * 鏈名稱 (例如: BSC)
     */
    @NotBlank(message = "鏈名稱 (例如: BSC)不能爲空", groups = { AddGroup.class, EditGroup.class })
    private String chainName;

    /**
     * 最後成功同步的區塊號
     */
    @NotNull(message = "最後成功同步的區塊號不能爲空", groups = { AddGroup.class, EditGroup.class })
    private Long lastSyncedBlock;

}
