package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizRewardClaim;
import org.dromara.biz.domain.bo.BizRewardClaimBo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.vo.BizRewardClaimVo;
import org.dromara.biz.mapper.BizRewardClaimMapper;
import org.dromara.biz.service.IBizRewardClaimService;
import org.dromara.biz.service.IBizSystemReserveSummaryService;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 理赔申请Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-18
 */
@RequiredArgsConstructor
@Service
public class BizRewardClaimServiceImpl extends BaseImpl<BizRewardClaim,BizRewardClaimVo> implements IBizRewardClaimService {

    private final BizRewardClaimMapper baseMapper;

    private final IBizUserProgressService bizUserProgressService;
    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;
    private final IBizSystemReserveSummaryService bizSystemReserveSummaryService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveClaim(Long id) {
        // 1. 查找并校验申请记录
        BizRewardClaimVo claim = baseMapper.selectVoById(id);
        if (claim == null) {
            throw new ServiceException("理赔申请不存在");
        }
        if (!"PENDING".equalsIgnoreCase(claim.getStatus())) {
            throw new ServiceException("该申请已处理，请勿重复操作");
        }

        bizSystemReserveSummaryService.deductReserveAmount(claim.getAmount());

        // 2. 向用户余额打入申请金额
        sysUserService.addBalance(claim.getUserId(), claim.getAmount());

        // 3. 创建一笔交易流水记录
        BizTransactionsBo transactionBo = new BizTransactionsBo();
        transactionBo.setUserId(claim.getUserId());
        transactionBo.setAmount(claim.getAmount());
        transactionBo.setCurrency(claim.getCurrency());
        transactionBo.setTransactionType("REWARD_COMPENSATION"); // 交易类型：理赔补偿
        transactionBo.setStatus("CONFIRMED");
        transactionBo.setSourceId(claim.getId().toString());
        transactionBo.setRemarks("连输理赔金审核通过");
        bizTransactionsService.insertByBo(transactionBo);

        // 4. 更新申请记录状态为 "APPROVED"
        BizRewardClaimBo updateBo = new BizRewardClaimBo();
        updateBo.setId(id);
        updateBo.setStatus("APPROVED");
        baseMapper.updateById(MapstructUtils.convert(updateBo, BizRewardClaim.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectClaim(Long id) {
//        // 1. 查找并校验申请记录
//        BizRewardClaimVo claim = baseMapper.selectVoById(id);
//        if (claim == null) {
//            throw new ServiceException("理赔申请不存在");
//        }
//        if (!"PENDING".equalsIgnoreCase(claim.getStatus())) {
//            throw new ServiceException("该申请已处理，请勿重复操作");
//        }
//
//        // 2. 查找用户的连输进度记录
//        BizUserProgressVo progress = bizUserProgressService.findByUserId(claim.getUserId());
//        if (progress == null) {
//            // 如果找不到进度，创建一个新的并将数据累加回去
//            BizUserProgressBo newProgress = new BizUserProgressBo();
//            newProgress.setUserId(claim.getUserId());
//            newProgress.setConsecutiveLosses(claim.getLostCount());
//            newProgress.setConsecutiveLossesAmount(claim.getAmount());
//            newProgress.setCanClaimReward(1); // 重新打开领取资格
//            bizUserProgressService.insertByBo(newProgress);
//        } else {
//            // 如果找到了，将数据累加回去
//            BizUserProgressBo progressUpdateBo = new BizUserProgressBo();
//            progressUpdateBo.setProgressId(progress.getProgressId());
//            progressUpdateBo.setConsecutiveLosses(progress.getConsecutiveLosses() + claim.getLostCount());
//            progressUpdateBo.setConsecutiveLossesAmount(progress.getConsecutiveLossesAmount().add(claim.getAmount()));
//            progressUpdateBo.setCanClaimReward(1); // 重新打开领取资格
//            bizUserProgressService.updateByBo(progressUpdateBo);
//        }
//
//        // 3. 更新申请记录状态为 "REJECTED"
//        BizRewardClaimBo updateBo = new BizRewardClaimBo();
//        updateBo.setId(id);
//        updateBo.setStatus("REJECTED");
//        baseMapper.updateById(MapstructUtils.convert(updateBo, BizRewardClaim.class));
    }


    /**
     * 查询理赔申请
     *
     * @param id 主键
     * @return 理赔申请
     */
    @Override
    public BizRewardClaimVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询理赔申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 理赔申请分页列表
     */
    @Override
    public TableDataInfo<BizRewardClaimVo> queryPageList(BizRewardClaimBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizRewardClaim> lqw = buildQueryWrapper(bo);
        Page<BizRewardClaimVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的理赔申请列表
     *
     * @param bo 查询条件
     * @return 理赔申请列表
     */
    @Override
    public List<BizRewardClaimVo> queryList(BizRewardClaimBo bo) {
        LambdaQueryWrapper<BizRewardClaim> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizRewardClaim> buildQueryWrapper(BizRewardClaimBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizRewardClaim> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(BizRewardClaim::getId);
        lqw.eq(bo.getUserId() != null, BizRewardClaim::getUserId, bo.getUserId());
        lqw.eq(bo.getAmount() != null, BizRewardClaim::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getCurrency()), BizRewardClaim::getCurrency, bo.getCurrency());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizRewardClaim::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getRemarks()), BizRewardClaim::getRemarks, bo.getRemarks());
        return lqw;
    }

    /**
     * 新增理赔申请
     *
     * @param bo 理赔申请
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizRewardClaimBo bo) {
        BizRewardClaim add = MapstructUtils.convert(bo, BizRewardClaim.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改理赔申请
     *
     * @param bo 理赔申请
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizRewardClaimBo bo) {
        BizRewardClaim update = MapstructUtils.convert(bo, BizRewardClaim.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizRewardClaim entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除理赔申请信息
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
    public List<BizRewardClaimVo> queryList(LambdaQueryWrapper<BizRewardClaim> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizRewardClaimVo queryOne(LambdaQueryWrapper<BizRewardClaim> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }
}
