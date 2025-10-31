package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizLeagues;
import org.dromara.biz.domain.bo.BizLeaguesBo;
import org.dromara.biz.domain.vo.BizLeaguesVo;
import org.dromara.biz.mapper.BizLeaguesMapper;
import org.dromara.biz.service.IBizLeaguesService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 联赛信息Service业务层处理
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@RequiredArgsConstructor
@Service
public class BizLeaguesServiceImpl implements IBizLeaguesService {

    private final BizLeaguesMapper baseMapper;

    @Override
    public Map<String, BizLeaguesVo> queryMapByIds(List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<BizLeaguesVo> list = baseMapper.selectVoList(
            new LambdaQueryWrapper<BizLeagues>().in(BizLeagues::getLeagueId, ids)
        );
        return list.stream().collect(Collectors.toMap(BizLeaguesVo::getLeagueId, Function.identity()));
    }

    /**
     * 查询联赛信息
     *
     * @param leagueId 主键
     * @return 联赛信息
     */
    @Override
    @Cacheable(cacheNames = "leagues", key = "#leagueId")
    public BizLeaguesVo queryById(String leagueId){
        return baseMapper.selectVoById(leagueId);
    }

    /**
     * 分页查询联赛信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 联赛信息分页列表
     */
    @Override
    public TableDataInfo<BizLeaguesVo> queryPageList(BizLeaguesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizLeagues> lqw = buildQueryWrapper(bo);
        Page<BizLeaguesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的联赛信息列表
     *
     * @param bo 查询条件
     * @return 联赛信息列表
     */
    @Override
    public List<BizLeaguesVo> queryList(BizLeaguesBo bo) {
        LambdaQueryWrapper<BizLeagues> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizLeagues> buildQueryWrapper(BizLeaguesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizLeagues> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizLeagues::getLeagueId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), BizLeagues::getName, bo.getName());
        lqw.like(StringUtils.isNotBlank(bo.getAbbrName()), BizLeagues::getAbbrName, bo.getAbbrName());
        lqw.eq(StringUtils.isNotBlank(bo.getBackColor()), BizLeagues::getBackColor, bo.getBackColor());
        return lqw;
    }

    /**
     * 新增联赛信息
     *
     * @param bo 联赛信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizLeaguesBo bo) {
        BizLeagues add = MapstructUtils.convert(bo, BizLeagues.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setLeagueId(add.getLeagueId());
        }
        return flag;
    }

    /**
     * 修改联赛信息
     *
     * @param bo 联赛信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizLeaguesBo bo) {
        BizLeagues update = MapstructUtils.convert(bo, BizLeagues.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizLeagues entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除联赛信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public Boolean saveOrUpdate(BizLeaguesBo bo) {
        BizLeagues update = MapstructUtils.convert(bo, BizLeagues.class);
        return baseMapper.saveOrUpdate(update);
    }
}
