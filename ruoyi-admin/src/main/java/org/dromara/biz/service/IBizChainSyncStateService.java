package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizChainSyncState;
import org.dromara.biz.domain.vo.BizChainSyncStateVo;
import org.dromara.biz.domain.bo.BizChainSyncStateBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 区块链同步状态Service接口
 *
 * @author Lion Li
 * @date 2025-09-29
 */
public interface IBizChainSyncStateService {

    /**
     * 获取指定链最后成功同步的区块号
     * @param chainName 链名称 (例如: "BSC")
     * @return 区块号
     */
    long getLastSyncedBlock(String chainName,int chainId);

    /**
     * 更新指定链的最后同步区块号
     * @param chainName 链名称
     * @param blockNumber 新的区块号
     */
    void updateLastSyncedBlock(String chainName,int chainId, long blockNumber);

    /**
     * 查询区块链同步状态
     *
     * @param id 主键
     * @return 区块链同步状态
     */
    BizChainSyncStateVo queryById(Long id);

    /**
     * 分页查询区块链同步状态列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 区块链同步状态分页列表
     */
    TableDataInfo<BizChainSyncStateVo> queryPageList(BizChainSyncStateBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的区块链同步状态列表
     *
     * @param bo 查询条件
     * @return 区块链同步状态列表
     */
    List<BizChainSyncStateVo> queryList(BizChainSyncStateBo bo);

    /**
     * 新增区块链同步状态
     *
     * @param bo 区块链同步状态
     * @return 是否新增成功
     */
    Boolean insertByBo(BizChainSyncStateBo bo);

    /**
     * 修改区块链同步状态
     *
     * @param bo 区块链同步状态
     * @return 是否修改成功
     */
    Boolean updateByBo(BizChainSyncStateBo bo);

    /**
     * 校验并批量删除区块链同步状态信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizChainSyncStateVo> queryList(LambdaQueryWrapper<BizChainSyncState> lqw);

    BizChainSyncStateVo queryOne(LambdaQueryWrapper<BizChainSyncState> lqw);

    /**
     * 新增或修改
     *
     * @param bo 区块链同步状态
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizChainSyncStateBo bo);
}
