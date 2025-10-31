package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizTeams;
import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.domain.vo.BizTeamsVo;
import org.dromara.biz.mapper.BizTeamsMapper;
import org.dromara.biz.service.IBizTeamsService;
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
 * 球队信息Service业务层处理
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@RequiredArgsConstructor
@Service
public class BizTeamsServiceImpl implements IBizTeamsService {

    private final BizTeamsMapper baseMapper;

    /**
     * 【核心新增】根据ID列表批量查询球队信息，并以Map形式返回
     *
     * @param ids 球队ID列表
     * @return Map<球队ID, 球队信息Vo>
     */
    @Override
    public Map<Long, BizTeamsVo> queryMapByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<BizTeamsVo> list = baseMapper.selectVoList(
            new LambdaQueryWrapper<BizTeams>().in(BizTeams::getTeamId, ids)
        );
        return list.stream().collect(Collectors.toMap(BizTeamsVo::getTeamId, Function.identity()));
    }

    @Override
    public boolean saveOrUpdate(BizTeamsBo bo) {
        BizTeams add = MapstructUtils.convert(bo, BizTeams.class);
        return baseMapper.saveOrUpdate(add);
    }

    @Override
    public List<BizTeamsVo> queryTeamsWithoutLogo() {
        return baseMapper.selectVoList(
            new LambdaQueryWrapper<BizTeams>()
                .isNotNull(BizTeams::getCode)
                .eq(BizTeams::getLogo,"https://static.sporttery.cn/res_1_0/jcw/upload/teamlogo/teamdef_zq.png")
        );
    }

    /**
     * 查询球队信息
     *
     * @param teamId 主键
     * @return 球队信息
     */
    @Override
    @Cacheable(cacheNames = "team", key = "#teamId")
    public BizTeamsVo queryById(Long teamId){
        return baseMapper.selectVoById(teamId);
    }

    /**
     * 分页查询球队信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 球队信息分页列表
     */
    @Override
    public TableDataInfo<BizTeamsVo> queryPageList(BizTeamsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizTeams> lqw = buildQueryWrapper(bo);
        Page<BizTeamsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的球队信息列表
     *
     * @param bo 查询条件
     * @return 球队信息列表
     */
    @Override
    public List<BizTeamsVo> queryList(BizTeamsBo bo) {
        LambdaQueryWrapper<BizTeams> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizTeams> buildQueryWrapper(BizTeamsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizTeams> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizTeams::getTeamId);
        lqw.like(StringUtils.isNotBlank(bo.getFullName()), BizTeams::getFullName, bo.getFullName());
        lqw.like(StringUtils.isNotBlank(bo.getAbbrName()), BizTeams::getAbbrName, bo.getAbbrName());
        return lqw;
    }

    /**
     * 新增球队信息
     *
     * @param bo 球队信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizTeamsBo bo) {
        BizTeams add = MapstructUtils.convert(bo, BizTeams.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTeamId(add.getTeamId());
        }
        return flag;
    }

    /**
     * 修改球队信息
     *
     * @param bo 球队信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizTeamsBo bo) {
        BizTeams update = MapstructUtils.convert(bo, BizTeams.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizTeams entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除球队信息信息
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
}
