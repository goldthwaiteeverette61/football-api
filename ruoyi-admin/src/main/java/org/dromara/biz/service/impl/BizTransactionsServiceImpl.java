package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizTransactions;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.vo.BizTransactionsVo;
import org.dromara.biz.mapper.BizTransactionsMapper;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户资金流水Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-06
 */
@RequiredArgsConstructor
@Service
public class BizTransactionsServiceImpl extends BaseImpl<BizTransactions,BizTransactionsVo> implements IBizTransactionsService {

    private final BizTransactionsMapper baseMapper;

    @Override
    public void updateStatusAndTxIdBySourceId(String sourceId, String txId, String status) {
        baseMapper.update(null,
            Wrappers.<BizTransactions>lambdaUpdate()
                .set(BizTransactions::getStatus, status)
                .set(BizTransactions::getTransactionHash, txId)
                .eq(BizTransactions::getSourceId, sourceId)
        );
    }

    /**
     * 【核心新增】实现邀请收益的统计逻辑
     */
    @Override
    public Map<String, Object> getInvitationCommissionStats(Long userId) {
        if (userId == null) {
            return new HashMap<>();
        }

        // 1. 调用 Mapper 中的自定义 XML 查询方法
        BigDecimal todayCommission = baseMapper.sumCommissionByTimeRange(userId, "today");
        BigDecimal thisMonthCommission = baseMapper.sumCommissionByTimeRange(userId, "this_month");
        BigDecimal totalCommission = baseMapper.sumCommissionByTimeRange(userId, "total");

        // 2. 组装返回结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayEarnings", todayCommission != null ? todayCommission : BigDecimal.ZERO);
        stats.put("thisMonthEarnings", thisMonthCommission != null ? thisMonthCommission : BigDecimal.ZERO);
        stats.put("totalEarnings", totalCommission != null ? totalCommission : BigDecimal.ZERO);

        return stats;
    }
    /**
     * 根据源ID更新交易流水的状态
     *
     * @param sourceId 源ID (例如：提现申请ID)
     * @param status   新的状态
     */
    @Override
    public void updateStatusBySourceId(String sourceId, String status) {
        baseMapper.update(null,
            Wrappers.<BizTransactions>lambdaUpdate()
                .set(BizTransactions::getStatus, status)
                .eq(BizTransactions::getSourceId, sourceId)
        );
    }

    /**
     * 查询用户资金流水
     *
     * @param id 主键
     * @return 用户资金流水
     */
    @Override
    public BizTransactionsVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询用户资金流水列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户资金流水分页列表
     */
    @Override
    public TableDataInfo<BizTransactionsVo> queryPageList(BizTransactionsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizTransactions> lqw = buildQueryWrapper(bo);
        Page<BizTransactionsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户资金流水列表
     *
     * @param bo 查询条件
     * @return 用户资金流水列表
     */
    @Override
    public List<BizTransactionsVo> queryList(BizTransactionsBo bo) {
        LambdaQueryWrapper<BizTransactions> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizTransactions> buildQueryWrapper(BizTransactionsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizTransactions> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(BizTransactions::getId);
        lqw.eq(bo.getUserId() != null, BizTransactions::getUserId, bo.getUserId());
        lqw.eq(bo.getAmount() != null, BizTransactions::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getCurrency()), BizTransactions::getCurrency, bo.getCurrency());
        lqw.eq(StringUtils.isNotBlank(bo.getTransactionType()), BizTransactions::getTransactionType, bo.getTransactionType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizTransactions::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getReferenceId()), BizTransactions::getReferenceId, bo.getReferenceId());
        lqw.eq(StringUtils.isNotBlank(bo.getRemarks()), BizTransactions::getRemarks, bo.getRemarks());
        lqw.eq(StringUtils.isNotBlank(bo.getBlockchainNetwork()), BizTransactions::getBlockchainNetwork, bo.getBlockchainNetwork());
        lqw.eq(StringUtils.isNotBlank(bo.getTransactionHash()), BizTransactions::getTransactionHash, bo.getTransactionHash());
        lqw.eq(StringUtils.isNotBlank(bo.getFromAddress()), BizTransactions::getFromAddress, bo.getFromAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getToAddress()), BizTransactions::getToAddress, bo.getToAddress());
        return lqw;
    }

    /**
     * 新增用户资金流水
     *
     * @param bo 用户资金流水
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizTransactionsBo bo) {
        BizTransactions add = MapstructUtils.convert(bo, BizTransactions.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户资金流水
     *
     * @param bo 用户资金流水
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizTransactionsBo bo) {
        BizTransactions update = MapstructUtils.convert(bo, BizTransactions.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizTransactions entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户资金流水信息
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
    * 新增或更新
    *
    * @param bo 对象
    * @return 是否成功
    */
    @Override
    public Boolean insertOrUpdate(BizTransactionsBo bo) {
        if(bo.getId() != null && bo.getId() > 0){
            return this.updateByBo(bo);
        }

        LambdaQueryWrapper<BizTransactions> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizTransactions::getId, bo.getId());
        BizTransactionsVo vo = baseMapper.selectVoOne(lqw);

        if(vo != null){
            bo.setId(vo.getId());
            return this.updateByBo(bo);
        }else {
            return this.insertByBo(bo);
        }
    }

    /**
     * 执行列表查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public List<BizTransactionsVo> queryList(LambdaQueryWrapper<BizTransactions> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizTransactionsVo queryOne(LambdaQueryWrapper<BizTransactions> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    /**
     * 检查指定的交易哈希是否已经被处理过
     *
     * @param txHash 交易哈希 (txID)
     * @return 如果已处理则返回 true, 否则返回 false
     */
    @Override
    public boolean isTransactionProcessed(String txHash) {
        // 使用 MyBatis-Plus 的 exists 方法来高效地检查记录是否存在
        // 这会生成一条类似 "SELECT COUNT(1) FROM biz_wallet_transactions WHERE tx_hash = ?" 的SQL语句
        return baseMapper.exists(
            new LambdaQueryWrapper<BizTransactions>()
                .eq(BizTransactions::getTransactionHash, txHash)
        );
    }
}
