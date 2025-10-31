package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizUserWallets;
import org.dromara.biz.domain.vo.BizUserWalletsVo;
import org.dromara.biz.domain.bo.BizUserWalletsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 用户钱包地址Service接口
 *
 * @author Lion Li
 * @date 2025-08-06
 */
public interface IBizUserWalletsService {

    /**
     * 查询用户钱包地址
     *
     * @param walletId 主键
     * @return 用户钱包地址
     */
    BizUserWalletsVo queryById(Long walletId);

    /**
     * 分页查询用户钱包地址列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户钱包地址分页列表
     */
    TableDataInfo<BizUserWalletsVo> queryPageList(BizUserWalletsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户钱包地址列表
     *
     * @param bo 查询条件
     * @return 用户钱包地址列表
     */
    List<BizUserWalletsVo> queryList(BizUserWalletsBo bo);

    /**
     * 新增用户钱包地址
     *
     * @param bo 用户钱包地址
     * @return 是否新增成功
     */
    Boolean insertByBo(BizUserWalletsBo bo);

    /**
     * 修改用户钱包地址
     *
     * @param bo 用户钱包地址
     * @return 是否修改成功
     */
    Boolean updateByBo(BizUserWalletsBo bo);

    /**
     * 校验并批量删除用户钱包地址信息
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
    Boolean insertOrUpdate(BizUserWalletsBo bo);

    List<BizUserWalletsVo> queryList(LambdaQueryWrapper<BizUserWallets> lqw);

    BizUserWalletsVo queryOne(LambdaQueryWrapper<BizUserWallets> lqw);
}
