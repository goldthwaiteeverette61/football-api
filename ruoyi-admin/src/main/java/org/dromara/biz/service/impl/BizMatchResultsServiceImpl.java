package org.dromara.biz.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizMatchResults;
import org.dromara.biz.domain.bo.BizMatchResultsBo;
import org.dromara.biz.domain.vo.BizMatchResultsVo;
import org.dromara.biz.mapper.BizMatchResultsMapper;
import org.dromara.biz.service.IBizMatchResultsService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 比赛赛果Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-08
 */
@RequiredArgsConstructor
@Service
public class BizMatchResultsServiceImpl extends BaseImpl<BizMatchResults,BizMatchResultsVo> implements IBizMatchResultsService {

    private final BizMatchResultsMapper baseMapper;


    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 【已实现】根据比赛ID列表查询赛果
     */
    @Override
    public List<BizMatchResultsVo> queryListByMatchIds(List<Long> matchIds) {
        if (CollUtil.isEmpty(matchIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<BizMatchResults> lqw = Wrappers.lambdaQuery();
        lqw.in(BizMatchResults::getMatchId, matchIds);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public LambdaQueryWrapper<BizMatchResults> getLqw() {
        return super.lqw();
    }

    /**
     * 【核心新增】根据比赛ID删除赛果
     * <p>
     * 这是为了保证赛果生成时的幂等性，在插入新赛果前先清除旧数据。
     *
     * @param matchId 比赛ID
     */
    @Override
    public void deleteByMatchId(Long matchId) {
        if (matchId == null) {
            return;
        }
        baseMapper.delete(new LambdaQueryWrapper<BizMatchResults>()
            .eq(BizMatchResults::getMatchId, matchId));
    }

    /**
     * 【核心新增】批量插入赛果
     *
     * @param boList 赛果业务对象列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatchByBo(List<BizMatchResultsBo> boList) {
        if (CollUtil.isEmpty(boList)) {
            return;
        }
        List<BizMatchResults> resultsToInsert = MapstructUtils.convert(boList, BizMatchResults.class);
        // IService 中自带了 saveBatch 方法，可以直接使用
        baseMapper.insertBatch(resultsToInsert);
    }

    /**
     * 查询比赛赛果
     *
     * @param id 主键
     * @return 比赛赛果
     */
    @Override
    public BizMatchResultsVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询比赛赛果列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛赛果分页列表
     */
    @Override
    public TableDataInfo<BizMatchResultsVo> queryPageList(BizMatchResultsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizMatchResults> lqw = buildQueryWrapper(bo);
        Page<BizMatchResultsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的比赛赛果列表
     *
     * @param bo 查询条件
     * @return 比赛赛果列表
     */
    @Override
    public List<BizMatchResultsVo> queryList(BizMatchResultsBo bo) {
        LambdaQueryWrapper<BizMatchResults> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizMatchResults> buildQueryWrapper(BizMatchResultsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizMatchResults> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizMatchResults::getId);
        lqw.eq(bo.getMatchId() != null, BizMatchResults::getMatchId, bo.getMatchId());
        lqw.eq(bo.getPoolId() != null, BizMatchResults::getPoolId, bo.getPoolId());
        lqw.eq(StringUtils.isNotBlank(bo.getPoolCode()), BizMatchResults::getPoolCode, bo.getPoolCode());
        lqw.eq(StringUtils.isNotBlank(bo.getCombination()), BizMatchResults::getCombination, bo.getCombination());
        lqw.eq(StringUtils.isNotBlank(bo.getCombinationDesc()), BizMatchResults::getCombinationDesc, bo.getCombinationDesc());
        lqw.eq(StringUtils.isNotBlank(bo.getGoalLine()), BizMatchResults::getGoalLine, bo.getGoalLine());
        lqw.eq(bo.getOdds() != null, BizMatchResults::getOdds, bo.getOdds());
        lqw.eq(bo.getCreatedAt() != null, BizMatchResults::getCreatedAt, bo.getCreatedAt());
        return lqw;
    }

    /**
     * 新增比赛赛果
     *
     * @param bo 比赛赛果
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizMatchResultsBo bo) {
        BizMatchResults add = MapstructUtils.convert(bo, BizMatchResults.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改比赛赛果
     *
     * @param bo 比赛赛果
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizMatchResultsBo bo) {
        BizMatchResults update = MapstructUtils.convert(bo, BizMatchResults.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizMatchResults entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除比赛赛果信息
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
    public Boolean insertOrUpdate(BizMatchResultsBo bo) {
        if(bo.getId() != null && bo.getId() > 0){
            return this.updateByBo(bo);
        }

        LambdaQueryWrapper<BizMatchResults> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizMatchResults::getId, bo.getId());
        BizMatchResultsVo vo = baseMapper.selectVoOne(lqw);

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
    public List<BizMatchResultsVo> queryList(LambdaQueryWrapper<BizMatchResults> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizMatchResultsVo queryOne(LambdaQueryWrapper<BizMatchResults> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public TableDataInfo<BizMatchResultsVo> queryPageListWithDetails(BizMatchResultsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizMatchResults> lqw = buildQueryWrapper(bo);
        Page<BizMatchResultsVo> result = baseMapper.selectVoPageWithDetails(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public Boolean saveOrUpdate(BizMatchResultsBo bo) {
        BizMatchResults update = MapstructUtils.convert(bo, BizMatchResults.class);
        return baseMapper.saveOrUpdate(update);
    }
}
