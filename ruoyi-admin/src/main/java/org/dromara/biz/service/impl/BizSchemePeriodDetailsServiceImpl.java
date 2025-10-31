package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.biz.domain.bo.BizSchemePeriodDetailsBo;
import org.dromara.biz.domain.vo.BizSchemePeriodDetailsVo;
import org.dromara.biz.domain.BizSchemePeriodDetails;
import org.dromara.biz.mapper.BizSchemePeriodDetailsMapper;
import org.dromara.biz.service.IBizSchemePeriodDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 方案期数详情Service业务层处理
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@RequiredArgsConstructor
@Service
public class BizSchemePeriodDetailsServiceImpl implements IBizSchemePeriodDetailsService {

    private final BizSchemePeriodDetailsMapper baseMapper;

    /**
     * 【核心补充】根据期数ID查询详情列表
     */
    @Override
    public List<BizSchemePeriodDetailsVo> queryByPeriodId(Long periodId) {
        if (periodId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<BizSchemePeriodDetails> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizSchemePeriodDetails::getPeriodId, periodId);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 对应 a.png 的保存功能
     */
    @Override
    @Transactional
    public void updateBatch(List<BizSchemePeriodDetailsBo> boList) {
        if (CollUtil.isEmpty(boList)) {
            return;
        }
        List<BizSchemePeriodDetails> detailsToUpdate = MapstructUtils.convert(boList, BizSchemePeriodDetails.class);
        // MyBatis-Plus 的 serviceImpl 自带 saveOrUpdateBatch
        detailsToUpdate.forEach(baseMapper::updateById);
    }

    @Override
    public List<BizSchemePeriodDetailsVo> queryListByPeriodIds(List<Long> periodIds) {
        if (CollUtil.isEmpty(periodIds)) {
            return new ArrayList<>();
        }
        return baseMapper.selectVoList(
            Wrappers.<BizSchemePeriodDetails>lambdaQuery().in(BizSchemePeriodDetails::getPeriodId, periodIds)
        );
    }

    @Override
    public long countByPeriodId(Long periodId) {
        if (periodId == null) {
            return 0L;
        }
        return baseMapper.selectCount(new LambdaQueryWrapper<BizSchemePeriodDetails>()
            .eq(BizSchemePeriodDetails::getPeriodId, periodId));
    }

    /**
     * 查询方案期数详情
     *
     * @param detailId 主键
     * @return 方案期数详情
     */
    @Override
    public BizSchemePeriodDetailsVo queryById(Long detailId){
        return baseMapper.selectVoById(detailId);
    }

    /**
     * 分页查询方案期数详情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 方案期数详情分页列表
     */
    @Override
    public TableDataInfo<BizSchemePeriodDetailsVo> queryPageList(BizSchemePeriodDetailsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizSchemePeriodDetails> lqw = buildQueryWrapper(bo);
        Page<BizSchemePeriodDetailsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的方案期数详情列表
     *
     * @param bo 查询条件
     * @return 方案期数详情列表
     */
    @Override
    public List<BizSchemePeriodDetailsVo> queryList(BizSchemePeriodDetailsBo bo) {
        LambdaQueryWrapper<BizSchemePeriodDetails> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizSchemePeriodDetails> buildQueryWrapper(BizSchemePeriodDetailsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizSchemePeriodDetails> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizSchemePeriodDetails::getDetailId);
        lqw.eq(bo.getPeriodId() != null, BizSchemePeriodDetails::getPeriodId, bo.getPeriodId());
        lqw.eq(bo.getMatchId() != null, BizSchemePeriodDetails::getMatchId, bo.getMatchId());
        lqw.eq(StringUtils.isNotBlank(bo.getPoolCode()), BizSchemePeriodDetails::getPoolCode, bo.getPoolCode());
        lqw.eq(StringUtils.isNotBlank(bo.getSelection()), BizSchemePeriodDetails::getSelection, bo.getSelection());
        lqw.eq(bo.getOdds() != null, BizSchemePeriodDetails::getOdds, bo.getOdds());
        return lqw;
    }

    /**
     * 新增方案期数详情
     *
     * @param bo 方案期数详情
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizSchemePeriodDetailsBo bo) {
        BizSchemePeriodDetails add = MapstructUtils.convert(bo, BizSchemePeriodDetails.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setDetailId(add.getDetailId());
        }
        return flag;
    }

    /**
     * 修改方案期数详情
     *
     * @param bo 方案期数详情
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizSchemePeriodDetailsBo bo) {
        BizSchemePeriodDetails update = MapstructUtils.convert(bo, BizSchemePeriodDetails.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizSchemePeriodDetails entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除方案期数详情信息
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
    public Boolean insertOrUpdate(BizSchemePeriodDetailsBo bo) {
        if(bo.getDetailId() != null && bo.getDetailId() > 0){
            return this.updateByBo(bo);
        }

        LambdaQueryWrapper<BizSchemePeriodDetails> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizSchemePeriodDetails::getDetailId, bo.getDetailId());
        BizSchemePeriodDetailsVo vo = baseMapper.selectVoOne(lqw);

        if(vo != null){
            bo.setDetailId(vo.getDetailId());
            return this.updateByBo(bo);
        }else {
            return this.insertByBo(bo);
        }
    }

    @Override
    public Boolean insertBatchByBo(List<BizSchemePeriodDetailsBo> boList) {
        List<BizSchemePeriodDetails> addList = MapstructUtils.convert(boList, BizSchemePeriodDetails.class);
        return baseMapper.insertBatch(addList);
    }

    @Override
    public Boolean deleteByPeriodId(Long periodId) {
        LambdaQueryWrapper<BizSchemePeriodDetails> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizSchemePeriodDetails::getPeriodId, periodId);
        return baseMapper.delete(lqw) > 0;
    }

    @Override
    public List<BizSchemePeriodDetailsVo> queryByPeriodIds(List<Long> periodIds) {
        if (periodIds == null || periodIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BizSchemePeriodDetails> lqw = new LambdaQueryWrapper<>();
        lqw.in(BizSchemePeriodDetails::getPeriodId, periodIds);
        return baseMapper.selectVoList(lqw);
    }
}
