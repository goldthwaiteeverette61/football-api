package org.dromara.biz.service.impl;


import jakarta.annotation.PostConstruct;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.biz.domain.bo.BizUserWalletsBo;
import org.dromara.biz.domain.vo.BizUserWalletsVo;
import org.dromara.biz.domain.BizUserWallets;
import org.dromara.biz.mapper.BizUserWalletsMapper;
import org.dromara.biz.service.IBizUserWalletsService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 用户钱包地址Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-06
 */
@RequiredArgsConstructor
@Service
public class BizUserWalletsServiceImpl extends BaseImpl<BizUserWallets,BizUserWalletsVo> implements IBizUserWalletsService {

    private final BizUserWalletsMapper baseMapper;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 查询用户钱包地址
     *
     * @param walletId 主键
     * @return 用户钱包地址
     */
    @Override
    public BizUserWalletsVo queryById(Long walletId){
        return baseMapper.selectVoById(walletId);
    }

    /**
     * 分页查询用户钱包地址列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户钱包地址分页列表
     */
    @Override
    public TableDataInfo<BizUserWalletsVo> queryPageList(BizUserWalletsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizUserWallets> lqw = buildQueryWrapper(bo);
        Page<BizUserWalletsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户钱包地址列表
     *
     * @param bo 查询条件
     * @return 用户钱包地址列表
     */
    @Override
    public List<BizUserWalletsVo> queryList(BizUserWalletsBo bo) {
        LambdaQueryWrapper<BizUserWallets> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizUserWallets> buildQueryWrapper(BizUserWalletsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizUserWallets> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizUserWallets::getWalletId);
        lqw.eq(bo.getUserId() != null, BizUserWallets::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), BizUserWallets::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getPrivateKeyEncrypted()), BizUserWallets::getPrivateKeyEncrypted, bo.getPrivateKeyEncrypted());
        lqw.eq(bo.getCreatedAt() != null, BizUserWallets::getCreatedAt, bo.getCreatedAt());
        return lqw;
    }

    /**
     * 新增用户钱包地址
     *
     * @param bo 用户钱包地址
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizUserWalletsBo bo) {
        BizUserWallets add = MapstructUtils.convert(bo, BizUserWallets.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setWalletId(add.getWalletId());
        }
        return flag;
    }

    /**
     * 修改用户钱包地址
     *
     * @param bo 用户钱包地址
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizUserWalletsBo bo) {
        BizUserWallets update = MapstructUtils.convert(bo, BizUserWallets.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizUserWallets entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户钱包地址信息
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
    public Boolean insertOrUpdate(BizUserWalletsBo bo) {
        if(bo.getWalletId() != null && bo.getWalletId() > 0){
            return this.updateByBo(bo);
        }

        LambdaQueryWrapper<BizUserWallets> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizUserWallets::getWalletId, bo.getWalletId());
        BizUserWalletsVo vo = baseMapper.selectVoOne(lqw);

        if(vo != null){
            bo.setWalletId(vo.getWalletId());
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
    public List<BizUserWalletsVo> queryList(LambdaQueryWrapper<BizUserWallets> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizUserWalletsVo queryOne(LambdaQueryWrapper<BizUserWallets> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }
}
