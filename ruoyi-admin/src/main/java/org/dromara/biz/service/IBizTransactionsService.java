package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizTransactions;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.vo.BizTransactionsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户资金流水Service接口
 *
 * @author Lion Li
 * @date 2025-08-06
 */
public interface IBizTransactionsService {

    /**
     * 【核心新增】获取指定用户的邀请收益统计
     * @param userId 用户ID
     * @return 包含今日、本月、总收益的Map
     */
    Map<String, Object> getInvitationCommissionStats(Long userId);

    /**
     * 根据源ID更新交易流水的状态
     *
     * @param sourceId 源ID (例如：提现申请ID)
     * @param status   新的状态
     */
    void updateStatusAndTxIdBySourceId(String sourceId,String txId, String status);


    /**
     * 根据源ID更新交易流水的状态
     *
     * @param sourceId 源ID (例如：提现申请ID)
     * @param status   新的状态
     */
    void updateStatusBySourceId(String sourceId, String status);

    /**
     * 查询用户资金流水
     *
     * @param id 主键
     * @return 用户资金流水
     */
    BizTransactionsVo queryById(Long id);

    /**
     * 分页查询用户资金流水列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户资金流水分页列表
     */
    TableDataInfo<BizTransactionsVo> queryPageList(BizTransactionsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户资金流水列表
     *
     * @param bo 查询条件
     * @return 用户资金流水列表
     */
    List<BizTransactionsVo> queryList(BizTransactionsBo bo);

    /**
     * 新增用户资金流水
     *
     * @param bo 用户资金流水
     * @return 是否新增成功
     */
    Boolean insertByBo(BizTransactionsBo bo);

    /**
     * 修改用户资金流水
     *
     * @param bo 用户资金流水
     * @return 是否修改成功
     */
    Boolean updateByBo(BizTransactionsBo bo);

    /**
     * 校验并批量删除用户资金流水信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    Boolean insertOrUpdate(BizTransactionsBo bo);

    List<BizTransactionsVo> queryList(LambdaQueryWrapper<BizTransactions> lqw);

    BizTransactionsVo queryOne(LambdaQueryWrapper<BizTransactions> lqw);

    boolean isTransactionProcessed(String txHash);
}
