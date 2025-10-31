package org.dromara.biz.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.dromara.biz.domain.BizDepositWallets;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 平台充值钱包Mapper接口
 *
 * @author Lion Li
 * @date 2025-08-15
 */
public interface BizDepositWalletsMapper extends BaseMapperPlus<BizDepositWallets, BizDepositWalletsVo> {
    /**
     * 【新增】查询所有状态为 'active' 且尚未同步的钱包地址
     * @return 钱包地址列表
     */
    @Select("select wallet_address from biz_deposit_wallets where has_balance =1 ")
    List<String> selectWalletsAwaitingProcessing();

    /**
     * 【新增】根据钱包地址列表，批量更新同步状态为已同步 (synced = 1)
     * @param walletAddresses 钱包地址列表
     * @return 受影响的行数
     */
    @Update({
        "<script>",
        "UPDATE biz_deposit_wallets SET has_balance = 0 WHERE wallet_address IN",
        "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>",
        "#{item}",
        "</foreach>",
        "</script>"
    })
    int updateHasBalancesForList(List<String> walletAddresses);
}
