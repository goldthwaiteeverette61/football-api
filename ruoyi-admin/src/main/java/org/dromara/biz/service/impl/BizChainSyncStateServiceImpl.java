package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizChainSyncState;
import org.dromara.biz.domain.bo.BizChainSyncStateBo;
import org.dromara.biz.domain.vo.BizChainSyncStateVo;
import org.dromara.biz.mapper.BizChainSyncStateMapper;
import org.dromara.biz.service.IBizChainSyncStateService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 区块链同步状态Service业务层处理
 *
 * @author Lion Li
 * @date 2025-09-29
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BizChainSyncStateServiceImpl extends BaseImpl<BizChainSyncState,BizChainSyncStateVo> implements IBizChainSyncStateService {

    private final BizChainSyncStateMapper baseMapper;

    @Override
    public long getLastSyncedBlock(String chainName,int chainId) {
        LambdaQueryWrapper<BizChainSyncState> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizChainSyncState::getChainName, chainName).eq(BizChainSyncState::getChainId, chainId);
        BizChainSyncState state = baseMapper.selectOne(lqw);

        if (state == null) {
            log.warn("在 biz_chain_sync_state 表中未找到链 '{}' 的记录，将返回 -1。", chainName);
            return -1L;
        }

        return state.getLastSyncedBlock();
    }

    @Override
    public void updateLastSyncedBlock(String chainName,int chainId, long blockNumber) {
        LambdaUpdateWrapper<BizChainSyncState> luw = Wrappers.lambdaUpdate();

        luw.set(BizChainSyncState::getLastSyncedBlock, blockNumber)
            .eq(BizChainSyncState::getChainName, chainName)
            .eq(BizChainSyncState::getChainId, chainId);

        baseMapper.update(null, luw);
    }

    /**
     * 查询区块链同步状态
     *
     * @param id 主键
     * @return 区块链同步状态
     */
    @Override
    public BizChainSyncStateVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询区块链同步状态列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 区块链同步状态分页列表
     */
    @Override
    public TableDataInfo<BizChainSyncStateVo> queryPageList(BizChainSyncStateBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizChainSyncState> lqw = buildQueryWrapper(bo);
        Page<BizChainSyncStateVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的区块链同步状态列表
     *
     * @param bo 查询条件
     * @return 区块链同步状态列表
     */
    @Override
    public List<BizChainSyncStateVo> queryList(BizChainSyncStateBo bo) {
        LambdaQueryWrapper<BizChainSyncState> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizChainSyncState> buildQueryWrapper(BizChainSyncStateBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizChainSyncState> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizChainSyncState::getId);
        lqw.like(StringUtils.isNotBlank(bo.getChainName()), BizChainSyncState::getChainName, bo.getChainName());
        lqw.eq(bo.getLastSyncedBlock() != null, BizChainSyncState::getLastSyncedBlock, bo.getLastSyncedBlock());
        return lqw;
    }

    /**
     * 新增区块链同步状态
     *
     * @param bo 区块链同步状态
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizChainSyncStateBo bo) {
        BizChainSyncState add = MapstructUtils.convert(bo, BizChainSyncState.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改区块链同步状态
     *
     * @param bo 区块链同步状态
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizChainSyncStateBo bo) {
        BizChainSyncState update = MapstructUtils.convert(bo, BizChainSyncState.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizChainSyncState entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除区块链同步状态信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 执行列表查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public List<BizChainSyncStateVo> queryList(LambdaQueryWrapper<BizChainSyncState> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizChainSyncStateVo queryOne(LambdaQueryWrapper<BizChainSyncState> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizChainSyncStateBo bo) {
        BizChainSyncState update = MapstructUtils.convert(bo, BizChainSyncState.class);
        return baseMapper.saveOrUpdate(update);
    }
}
