package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizSchemePeriods;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.domain.vo.BizSchemePeriodDetailsVo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.mapper.BizSchemePeriodsMapper;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizSchemePeriodDetailsService;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 方案期数Service业务层处理 (已重构)
 *
 * @author Lion Li & Gemini
 * @date 2025-07-28
 */
@RequiredArgsConstructor
@Service
public class BizSchemePeriodsServiceImpl implements IBizSchemePeriodsService {

    private final BizSchemePeriodsMapper baseMapper;
    private final IBizSchemePeriodDetailsService iBizSchemePeriodDetailsService;
    private final IBizMatchesService iBizMatchesService;
    private final IBizSchemePeriodDetailsService schemePeriodDetailsService;

    /**
     * 【核心新增】实现查询当前或近期方案的逻辑
     */
    @Override
    public BizSchemePeriodsVo findActiveOrRecentPeriod() {
        // 1. 优先查找"pending"状态的方案
        BizSchemePeriodsVo pendingPeriod = baseMapper.selectVoOne(new LambdaQueryWrapper<BizSchemePeriods>()
            .eq(BizSchemePeriods::getStatus, "pending")
            .orderByDesc(BizSchemePeriods::getPeriodId)
            .last("LIMIT 1"));

        if (pendingPeriod != null) {
            fillDetailsForPeriods(Collections.singletonList(pendingPeriod));
            return pendingPeriod;
        }

        // 2. 如果没有"pending"状态的，则查找24小时内最近结算的方案
        Date twentyFourHoursAgo = DateUtil.offsetHour(new Date(), -24);

        BizSchemePeriodsVo recentPeriod = baseMapper.selectVoOne(new LambdaQueryWrapper<BizSchemePeriods>()
            .ge(BizSchemePeriods::getResultTime, twentyFourHoursAgo) // resultTime 在过去24小时内
            .ne(BizSchemePeriods::getStatus, "draft")
            .orderByDesc(BizSchemePeriods::getResultTime) // 按结算时间倒序取最新
            .last("LIMIT 1"));

        if (recentPeriod != null) {
            fillDetailsForPeriods(Collections.singletonList(recentPeriod));
        }

        return recentPeriod; // 如果都找不到，则返回null
    }

    /**
     * 【核心新增】实现看板统计数据的逻辑
     */
    @Override
    public Map<String, Object> getSchemeDashboardStats() {
        // 1. 统计 "won" 的总数
        long wonCount = baseMapper.selectCount(new LambdaQueryWrapper<BizSchemePeriods>()
            .eq(BizSchemePeriods::getStatus, "won"));

        // 2. 统计 "lost" 的总数
        long lostCount = baseMapper.selectCount(new LambdaQueryWrapper<BizSchemePeriods>()
            .eq(BizSchemePeriods::getStatus, "lost"));

        // 3. 获取最近50期的结果
        List<String> recentResults = this.queryRecentResultStats(30);

        // 4. 组装返回结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWon", wonCount);
        stats.put("totalLost", lostCount);
        stats.put("recentResults", recentResults);

        return stats;
    }

    @Override
    public Boolean publishPeriod(Long periodId) {
        BizSchemePeriodsVo period = this.queryById(periodId);
        if (period == null) {
            throw new ServiceException("方案不存在");
        }
        if (!BizSchemePeriods.STATUS_DRAFT.equals(period.getStatus())) {
            throw new ServiceException("只有草稿状态的方案才能发布");
        }

        // 检查方案是否已选择比赛
        if (schemePeriodDetailsService.countByPeriodId(periodId) == 0) {
            throw new ServiceException("发布失败，方案中尚未选择任何比赛");
        }

        BizSchemePeriodsBo updateBo = new BizSchemePeriodsBo();
        updateBo.setPeriodId(periodId);
        updateBo.setStatus(BizSchemePeriods.STATUS_PENDING);
        return this.updateByBo(updateBo);
    }

    /**
     * 查询方案期数 (已重构，现在会填充完整的详情)
     */
    @Override
    public BizSchemePeriodsVo queryById(Long periodId){
        BizSchemePeriodsVo periodVo = baseMapper.selectVoById(periodId);
        if (periodVo != null) {
            // 调用公共方法来填充详情
            fillDetailsForPeriods(Collections.singletonList(periodVo));
        }
        return periodVo;
    }

    /**
     * 分页查询方案期数列表
     */
    @Override
    public TableDataInfo<BizSchemePeriodsVo> queryPageList(BizSchemePeriodsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizSchemePeriods> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(BizSchemePeriods::getPeriodId);

        if(bo.getLimit() > 0){
            lqw.last(" limit "+bo.getLimit());
        }

        Page<BizSchemePeriodsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的方案期数列表
     */
    @Override
    public List<BizSchemePeriodsVo> queryList(BizSchemePeriodsBo bo) {
        LambdaQueryWrapper<BizSchemePeriods> lqw = buildQueryWrapper(bo);
        return this.queryList(lqw);
    }

    /**
     * 查询符合条件的方案期数列表
     */
    @Override
    public List<BizSchemePeriodsVo> queryList(LambdaQueryWrapper<BizSchemePeriods> lqw) {
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizSchemePeriods> buildQueryWrapper(BizSchemePeriodsBo bo) {
        LambdaQueryWrapper<BizSchemePeriods> lqw = Wrappers.lambdaQuery();

        // 保留您已有的查询条件
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizSchemePeriods::getStatus, bo.getStatus());
        lqw.like(StringUtils.isNotBlank(bo.getName()), BizSchemePeriods::getName, bo.getName());

        // 核心修改：增加对 excludeDraft 字段的处理
        if (bo.getExcludeDraft() != null && bo.getExcludeDraft()) {
            lqw.ne(BizSchemePeriods::getStatus, "draft");
        }

        // 核心修改：增加对 excludePending 字段的处理
        if (bo.getExcludePending() != null && bo.getExcludePending()) {
            lqw.ne(BizSchemePeriods::getStatus, "pending");
        }

        return lqw;
    }

    /**
     * 新增方案期数
     */
    @Override
    @Transactional
    public Boolean insertByBo(BizSchemePeriodsBo bo) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<BizSchemePeriods>().eq(BizSchemePeriods::getStatus,"pending"));
        if(count > 0) {
            throw new ServiceException("创建失败，当前已经存在进行中的方案。");
        }

        BizSchemePeriods add = MapstructUtils.convert(bo, BizSchemePeriods.class);
        add.setStatus(BizSchemePeriods.STATUS_DRAFT);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPeriodId(add.getPeriodId());
        }
        return flag;
    }

    /**
     * 修改方案期数
     */
    @Override
    public Boolean updateByBo(BizSchemePeriodsBo bo) {
        BizSchemePeriods update = MapstructUtils.convert(bo, BizSchemePeriods.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 校验并批量删除方案期数信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO: 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 新增或更新 (Upsert)
     * 建议：此方法逻辑可以简化，或在调用处直接判断使用 insert 还是 update。
     * 但为保持原样，仅做保留。
     */
    @Override
    public Boolean insertOrUpdate(BizSchemePeriodsBo bo) {
        if(bo.getPeriodId() != null && bo.getPeriodId() > 0){
            return this.updateByBo(bo);
        } else {
            return this.insertByBo(bo);
        }
    }

    /**
     * 核心修改：现在这个方法只负责调用新的公共方法
     */
    @Override
    public TableDataInfo<BizSchemePeriodsVo> queryPageListFull(BizSchemePeriodsBo bo, PageQuery pageQuery) {
        // 1. 先获取分页后的基本数据
        TableDataInfo<BizSchemePeriodsVo> page = this.queryPageList(bo, pageQuery);

        // 2. 调用新的公共方法来填充详情
        fillDetailsForPeriods(page.getRows());

        return page;
    }

    /**
     * 核心修改：这是您需要的、提炼出来的可复用方法
     */
    @Override
    public void fillDetailsForPeriods(List<BizSchemePeriodsVo> periods) {
        if (periods == null || periods.isEmpty()) {
            return;
        }

        // 1. 获取所有方案期数的ID
        List<Long> periodIds = periods.stream()
            .map(BizSchemePeriodsVo::getPeriodId)
            .collect(Collectors.toList());

        // 2. 一次性查询出所有相关的详情记录
        List<BizSchemePeriodDetailsVo> allDetails = iBizSchemePeriodDetailsService.queryByPeriodIds(periodIds);

        if (allDetails.isEmpty()) {
            return;
        }

        // 3. 一次性查询出所有相关的比赛信息
        List<Long> matchIds = allDetails.stream()
            .map(BizSchemePeriodDetailsVo::getMatchId)
            .distinct() // 去重，减少查询压力
            .collect(Collectors.toList());
        Map<Long, BizMatchesVo> matchMap = iBizMatchesService.queryMapByIds(matchIds);

        // 4. 在内存中进行数据组装
        // 将比赛信息填充到详情中
        allDetails.forEach(detail -> detail.setBizMatchesVo(matchMap.get(detail.getMatchId())));

        // 将详情列表按 periodId 分组
        Map<Long, List<BizSchemePeriodDetailsVo>> detailsMap = allDetails.stream()
            .collect(Collectors.groupingBy(BizSchemePeriodDetailsVo::getPeriodId));

        // 将分组后的详情设置回每个方案期数
        periods.forEach(period -> period.setDetails(detailsMap.get(period.getPeriodId())));
    }

    @Override
    public Map<Long, BizSchemePeriodsVo> queryMapByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<BizSchemePeriodsVo> list = baseMapper.selectVoList(
            new LambdaQueryWrapper<BizSchemePeriods>().in(BizSchemePeriods::getPeriodId, ids)
        );
        // 将列表转换为Map，方便快速查找
        return list.stream().collect(Collectors.toMap(BizSchemePeriodsVo::getPeriodId, Function.identity(), (key1, key2) -> key2));
    }

    @Override
    public BizSchemePeriodsVo findLastWonPeriodByUserId(Long userId) {
        return baseMapper.selectLastWonPeriodByUserId(userId);
    }

    @Override
    public List<BizSchemePeriodsVo> findLostPeriodsAfter(Long userId, Long periodId) {
        return baseMapper.findLostPeriodsAfter(userId, periodId);
    }

    /**
     * 【核心新增】实现查询最近开奖结果的逻辑
     */
    @Override
    public List<String> queryRecentResultStats(int limit) {
        // 1. 构建查询条件
        LambdaQueryWrapper<BizSchemePeriods> lqw = new LambdaQueryWrapper<>();
        lqw.in(BizSchemePeriods::getStatus, "won", "lost");
        lqw.orderByDesc(BizSchemePeriods::getPeriodId) // 按期数倒序
            .last("LIMIT " + limit); //

        // 2. 查询数据库
        List<BizSchemePeriodsVo> periods = baseMapper.selectVoList(lqw);

        // 3. 将结果转换为 "red" 或 "black"
        return periods.stream()
            .map(period -> {
                return period.getStatus();
            })
            .collect(Collectors.toList());
    }

    /**
     * 最近开奖
     */
    @Override
    public BizSchemePeriodsVo lastWonOrLost() {
        LambdaQueryWrapper<BizSchemePeriods> lqw = new LambdaQueryWrapper<>();
        lqw.in(BizSchemePeriods::getStatus, "won", "lost")
            .orderByDesc(BizSchemePeriods::getPeriodId)
            .last("LIMIT 1");

        List<BizSchemePeriodsVo> periods = this.queryList(lqw);

        // 使用 CollUtil 判断列表是否为空，代码更简洁安全
        return CollUtil.isNotEmpty(periods) ? periods.get(0) : null;
    }
}
