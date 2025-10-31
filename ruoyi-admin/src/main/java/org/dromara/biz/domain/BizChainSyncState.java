package org.dromara.biz.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 区块链同步状态对象 biz_chain_sync_state
 *
 * @author Lion Li
 * @date 2025-09-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_chain_sync_state")
public class BizChainSyncState extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 链名称 (例如: BSC)
     */
    private String chainName;

    /**
     * 最后成功同步的区块号
     */
    private Long lastSyncedBlock;

}
