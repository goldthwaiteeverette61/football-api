package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizUserProgress;
import org.dromara.biz.domain.bo.BizUserProgressBo;
import org.dromara.biz.domain.vo.BizUserFollowsVo;
import org.dromara.biz.domain.vo.BizUserProgressVo;
import org.dromara.biz.mapper.BizUserProgressMapper;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户跟投进度Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-12
 */
@RequiredArgsConstructor
@Service
public class BizUserProgressServiceImpl extends BaseImpl<BizUserProgress,BizUserProgressVo> implements IBizUserProgressService {

    private final BizUserProgressMapper baseMapper;
    private final ISysConfigService configService;
    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;


    @Override
    public void createInitialProgress(Long userId,String userName) {
        // 检查记录是否已存在，防止重复插入
        boolean exists = baseMapper.exists(new LambdaQueryWrapper<BizUserProgress>().eq(BizUserProgress::getUserId, userId));
        if (!exists) {
            BizUserProgressBo bo = new BizUserProgressBo();
            bo.setUserId(userId);
            bo.setConsecutiveLosses(0);
            bo.setConsecutiveLossesAmount(BigDecimal.ZERO);
            bo.setBetAmount(BigDecimal.ZERO);
            bo.setCanClaimReward(0);
            bo.setUserName(userName);
            bo.setBetType(BizUserProgress.BET_TYPE_NORMAL);
            bo.setCommissionRate(configService.rewardPersentNormal());
            this.insertByBo(bo);
        }
    }

    /**
     * 【核心新增】切换用户投注模式
     *
     * @param userId  用户ID
     * @param betType 新的投注模式 (normal 或 double)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBetType(Long userId, String betType) {
        // 1. 参数校验
        if (!("normal".equalsIgnoreCase(betType) || "double".equalsIgnoreCase(betType))) {
            throw new ServiceException("无效的投注模式，只能是 'normal' 或 'double'");
        }

        BizUserProgressVo progress = this.findByUserId(userId);
        // 2. 检查是否存在未重置的连败记录 (轮次进行中)
        if (progress.getConsecutiveLosses() != null && progress.getConsecutiveLosses() > 0) {
            throw new ServiceException("请先重置倍投后再进行此项操作");
        }

        // 3. 根据投注类型获取对应的佣金率
        //    此处假设 ISysConfigService 中存在一个获取“倍投”模式佣金率的方法，例如 rewardPersentDouble()
        BigDecimal newCommissionRate;
        if ("double".equalsIgnoreCase(betType)) {
            newCommissionRate = configService.rewardPersent();
        } else {
            newCommissionRate = configService.rewardPersentNormal();
        }

        baseMapper.update(null,
            new LambdaUpdateWrapper<BizUserProgress>()
                .set(BizUserProgress::getBetType, betType)
                .set(BizUserProgress::getCommissionRate, newCommissionRate)
                .eq(BizUserProgress::getProgressId, progress.getProgressId()));
    }

    @Override
    public void incrementUserLosses(Long userId, BizUserFollowsVo followsVo) {
        BizUserProgressVo progress = this.findByUserId(userId);
        BizUserProgressBo progressUpdateBo = new BizUserProgressBo();
        int lossesThreshold = Integer.parseInt(configService.selectConfigByKey("sys.biz.lossesThresholdForReward"));
        int newLosses = progress.getConsecutiveLosses() + 1;
        progressUpdateBo.setProgressId(progress.getProgressId());
        BigDecimal newTotalLossAmount = (progress.getConsecutiveLossesAmount() != null ? progress.getConsecutiveLossesAmount() : BigDecimal.ZERO).add(followsVo.getBetAmount());
        progressUpdateBo.setConsecutiveLosses(newLosses);
        progressUpdateBo.setLastBetAmount(followsVo.getBetAmount());
        progressUpdateBo.setBetAmount(BigDecimal.ZERO);
        progressUpdateBo.setConsecutiveLossesAmount(newTotalLossAmount);
        if (newLosses >= lossesThreshold) {
            progressUpdateBo.setCanClaimReward(1);
        }
        this.updateByBo(progressUpdateBo);
    }

    @Override
    public void resetUserLosses(Long userId) {
        BizUserProgressVo progress = this.findByUserId(userId);
        BizUserProgressBo progressUpdateBo = new BizUserProgressBo();
        progressUpdateBo.setProgressId(progress.getProgressId());
        progressUpdateBo.setConsecutiveLosses(0);
        progressUpdateBo.setConsecutiveLossesAmount(BigDecimal.ZERO);
        progressUpdateBo.setCanClaimReward(0);
        progressUpdateBo.setLastBetAmount(BigDecimal.ZERO);
        progressUpdateBo.setBetAmount(BigDecimal.ZERO);
        progressUpdateBo.setCommissionRate(BigDecimal.ZERO);
        this.updateByBo(progressUpdateBo);
    }

    /**
     * 查询用户跟投进度
     *
     * @param progressId 主键
     * @return 用户跟投进度
     */
    @Override
    public BizUserProgressVo queryById(Long progressId){
        return baseMapper.selectVoById(progressId);
    }

    /**
     * 分页查询用户跟投进度列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户跟投进度分页列表
     */
    @Override
    public TableDataInfo<BizUserProgressVo> queryPageList(BizUserProgressBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizUserProgress> lqw = buildQueryWrapper(bo);
        Page<BizUserProgressVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户跟投进度列表
     *
     * @param bo 查询条件
     * @return 用户跟投进度列表
     */
    @Override
    public List<BizUserProgressVo> queryList(BizUserProgressBo bo) {
        LambdaQueryWrapper<BizUserProgress> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizUserProgress> buildQueryWrapper(BizUserProgressBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizUserProgress> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizUserProgress::getProgressId);
        lqw.eq(bo.getUserId() != null, BizUserProgress::getUserId, bo.getUserId());
        lqw.eq(bo.getConsecutiveLosses() != null, BizUserProgress::getConsecutiveLosses, bo.getConsecutiveLosses());
        return lqw;
    }

    /**
     * 新增用户跟投进度
     *
     * @param bo 用户跟投进度
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizUserProgressBo bo) {
        BizUserProgress add = MapstructUtils.convert(bo, BizUserProgress.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProgressId(add.getProgressId());
        }
        return flag;
    }

    /**
     * 修改用户跟投进度
     *
     * @param bo 用户跟投进度
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizUserProgressBo bo) {
        BizUserProgress update = MapstructUtils.convert(bo, BizUserProgress.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizUserProgress entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户跟投进度信息
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
    public List<BizUserProgressVo> queryList(LambdaQueryWrapper<BizUserProgress> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizUserProgressVo queryOne(LambdaQueryWrapper<BizUserProgress> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    /**
     * 根据用户ID查询跟投进度
     */
    @Override
    public BizUserProgressVo findByUserId(Long userId) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<BizUserProgress>()
            .eq(BizUserProgress::getUserId, userId));
    }

    /**
     * 核心修复：实现新增的方法
     */
    @Override
    public void deleteByUserId(Long userId) {
        if (userId == null) return;
        baseMapper.delete(new LambdaQueryWrapper<BizUserProgress>().eq(BizUserProgress::getUserId, userId));
    }

    /**
     * 处理用户跟投胜利
     * 当用户跟投的方案获胜时，重置其连输记录。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleFollowWin(Long userId) {
        BizUserProgress progress = baseMapper.selectOne(new LambdaQueryWrapper<BizUserProgress>()
            .eq(BizUserProgress::getUserId, userId));

        if (progress != null) {
            // 胜利则重置连输记录
            progress.setConsecutiveLosses(0);
            progress.setConsecutiveLossesAmount(BigDecimal.ZERO);
            progress.setBetAmount(BigDecimal.ZERO);
            progress.setCanClaimReward(0); // 同时重置领取资格
            baseMapper.updateById(progress);
        }
    }

    /**
     * 处理用户跟投失败
     * 当用户跟投的方案失败时，累加其连输次数和金额。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleFollowLoss(Long userId, BigDecimal amount) {
        // 查找或创建用户进度记录
        BizUserProgress progress = baseMapper.selectOne(new LambdaQueryWrapper<BizUserProgress>()
            .eq(BizUserProgress::getUserId, userId));

        if (progress == null) {
            progress = new BizUserProgress();
            progress.setUserId(userId);
            progress.setConsecutiveLosses(1);
            progress.setConsecutiveLossesAmount(amount);
            progress.setCanClaimReward(0);
            baseMapper.insert(progress);
        } else {
            // 累加连输次数和金额
            progress.setConsecutiveLosses(progress.getConsecutiveLosses() + 1);
            // 如果之前是null，则初始化为0
            BigDecimal currentLossAmount = progress.getConsecutiveLossesAmount() != null ? progress.getConsecutiveLossesAmount() : BigDecimal.ZERO;
            progress.setConsecutiveLossesAmount(currentLossAmount.add(amount));

            if (progress.getConsecutiveLosses() >= 8) {
                progress.setCanClaimReward(1);
            }
            baseMapper.updateById(progress);
        }
    }
}
