package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizDepositWallets;
import org.dromara.biz.domain.bo.BizDepositWalletsBo;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 平台充值钱包Service接口
 *
 * @author Lion Li
 * @date 2025-08-15
 */
public interface IBizDepositWalletsService {

    String applyDepositWallet();

    /**
     * 获取所有活跃且未同步的钱包地址
     * @return 钱包地址列表
     */
    List<String> selectWalletsAwaitingProcessing();

    /**
     * 【新增】根据钱包地址列表，批量更新同步状态为已同步 (synced = 1)
     * @param walletAddresses 钱包地址列表
     * @return 是否操作成功 (受影响行数 > 0)
     */
    Boolean updateHasBalancesForList(List<String> walletAddresses);

    /**
     * 查询平台充值钱包
     *
     * @param walletId 主键
     * @return 平台充值钱包
     */
    BizDepositWalletsVo queryById(Long walletId);

    /**
     * 分页查询平台充值钱包列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 平台充值钱包分页列表
     */
    TableDataInfo<BizDepositWalletsVo> queryPageList(BizDepositWalletsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的平台充值钱包列表
     *
     * @param bo 查询条件
     * @return 平台充值钱包列表
     */
    List<BizDepositWalletsVo> queryList(BizDepositWalletsBo bo);

    /**
     * 新增平台充值钱包
     *
     * @param bo 平台充值钱包
     * @return 是否新增成功
     */
    Boolean insertByBo(BizDepositWalletsBo bo);

    /**
     * 修改平台充值钱包
     *
     * @param bo 平台充值钱包
     * @return 是否修改成功
     */
    Boolean updateByBo(BizDepositWalletsBo bo);

    /**
     * 校验并批量删除平台充值钱包信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizDepositWalletsVo> queryList(LambdaQueryWrapper<BizDepositWallets> lqw);

    BizDepositWalletsVo queryOne(LambdaQueryWrapper<BizDepositWallets> lqw);
}
