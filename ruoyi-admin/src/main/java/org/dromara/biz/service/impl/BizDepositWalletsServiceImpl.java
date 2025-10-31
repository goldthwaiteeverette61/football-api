package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizDepositWallets;
import org.dromara.biz.domain.bo.BizDepositWalletsBo;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.biz.mapper.BizDepositWalletsMapper;
import org.dromara.biz.service.IBizDepositWalletsService;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 平台充值钱包Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-15
 */
@RequiredArgsConstructor
@Service
public class BizDepositWalletsServiceImpl extends BaseImpl<BizDepositWallets,BizDepositWalletsVo> implements IBizDepositWalletsService {

    private final BizDepositWalletsMapper baseMapper;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    public String applyDepositWallet(){
        LoginUser loginUser = LoginHelper.getLoginUser();
        BizDepositWalletsVo bizDepositWalletsVo = this.queryOne(this.lqw().eq(BizDepositWallets::getUserId, loginUser.getUserId()));
        if(bizDepositWalletsVo != null) {
            return bizDepositWalletsVo.getWalletAddress();
        }

        // 3. 分配充值钱包
        bizDepositWalletsVo = this.queryOne(this.lqw().isNull(BizDepositWallets::getUserId));
        if(bizDepositWalletsVo == null){
            throw new ServiceException("系统充值钱包库存不足，申请失败");
        }

        BizDepositWalletsBo bizDepositWalletsBo = new BizDepositWalletsBo();
        bizDepositWalletsBo.setUserId(loginUser.getUserId());
        bizDepositWalletsBo.setUserName(loginUser.getUsername());
        bizDepositWalletsBo.setStatus("active");
        bizDepositWalletsBo.setWalletId(bizDepositWalletsVo.getWalletId());
        this.updateByBo(bizDepositWalletsBo);

        return bizDepositWalletsVo.getWalletAddress();
    }

    @Override
    public List<String> selectWalletsAwaitingProcessing() {
        return baseMapper.selectWalletsAwaitingProcessing();
    }

    @Override
    @Transactional
    public Boolean updateHasBalancesForList(List<String> walletAddresses) {
        if (walletAddresses == null || walletAddresses.isEmpty()) {
            return true; // 如果列表为空，直接返回成功
        }
        // 调用 Mapper 方法，并判断受影响行数是否大于0
        return baseMapper.updateHasBalancesForList(walletAddresses) > 0;
    }


    /**
     * 查询平台充值钱包
     *
     * @param walletId 主键
     * @return 平台充值钱包
     */
    @Override
    public BizDepositWalletsVo queryById(Long walletId){
        return baseMapper.selectVoById(walletId);
    }

    /**
     * 分页查询平台充值钱包列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 平台充值钱包分页列表
     */
    @Override
    public TableDataInfo<BizDepositWalletsVo> queryPageList(BizDepositWalletsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizDepositWallets> lqw = buildQueryWrapper(bo);
        Page<BizDepositWalletsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的平台充值钱包列表
     *
     * @param bo 查询条件
     * @return 平台充值钱包列表
     */
    @Override
    public List<BizDepositWalletsVo> queryList(BizDepositWalletsBo bo) {
        LambdaQueryWrapper<BizDepositWallets> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizDepositWallets> buildQueryWrapper(BizDepositWalletsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizDepositWallets> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizDepositWallets::getWalletId);
        lqw.like(StringUtils.isNotBlank(bo.getWalletName()), BizDepositWallets::getWalletName, bo.getWalletName());
        lqw.eq(StringUtils.isNotBlank(bo.getWalletAddress()), BizDepositWallets::getWalletAddress, bo.getWalletAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizDepositWallets::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getQrCodeUrl()), BizDepositWallets::getQrCodeUrl, bo.getQrCodeUrl());
        return lqw;
    }

    /**
     * 新增平台充值钱包
     *
     * @param bo 平台充值钱包
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizDepositWalletsBo bo) {
        BizDepositWallets add = MapstructUtils.convert(bo, BizDepositWallets.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setWalletId(add.getWalletId());
        }
        return flag;
    }

    /**
     * 修改平台充值钱包
     *
     * @param bo 平台充值钱包
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizDepositWalletsBo bo) {
        BizDepositWallets update = MapstructUtils.convert(bo, BizDepositWallets.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizDepositWallets entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除平台充值钱包信息
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
    public List<BizDepositWalletsVo> queryList(LambdaQueryWrapper<BizDepositWallets> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizDepositWalletsVo queryOne(LambdaQueryWrapper<BizDepositWallets> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }
}
