package org.dromara.biz.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.biz.domain.BizChainSyncState;

import java.io.Serial;
import java.io.Serializable;



/**
 * 区块链同步状态视图对象 biz_chain_sync_state
 *
 * @author Lion Li
 * @date 2025-09-29
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizChainSyncState.class)
public class BizChainSyncStateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 链id
     */
    private int chainId;

    /**
     * 链名称 (例如: BSC)
     */
    @ExcelProperty(value = "链名称 (例如: BSC)")
    private String chainName;


    /**
     * 最后成功同步的区块号
     */
    @ExcelProperty(value = "最后成功同步的区块号")
    private Long lastSyncedBlock;

}
