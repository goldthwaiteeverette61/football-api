package org.dromara.biz.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizOdds;
import org.dromara.biz.domain.bo.BizOddsBo;
import org.dromara.biz.domain.bo.BizOddsHistoryBo;
import org.dromara.biz.domain.vo.BizOddsHistoryVo;
import org.dromara.biz.domain.vo.BizOddsVo;
import org.dromara.biz.mapper.BizOddsMapper;
import org.dromara.biz.service.IBizOddsHistoryService;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 比赛赔率Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-06
 */
@RequiredArgsConstructor
@Service
public class BizOddsServiceImpl extends BaseImpl<BizOdds,BizOddsVo> implements IBizOddsService {

    private final BizOddsMapper baseMapper;

    private final IBizOddsHistoryService bizOddsHistoryService;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }


    /**
     * 【核心新增】根据比赛ID列表和玩法代码，批量查询让球盘口
     *
     * @param matchIds 比赛ID列表
     * @param poolCode 玩法代码 (例如 "HHAD")
     * @return Map<比赛ID, 让球盘口字符串>
     */
    @Override
    public Map<Long, String> queryGoalLinesByMatchIds(List<Long> matchIds, String poolCode) {

        if (CollUtil.isEmpty(matchIds) || StringUtils.isBlank(poolCode)) {
            return Collections.emptyMap();
        }

        List<BizOddsVo> oddsList = baseMapper.selectVoList(
            new LambdaQueryWrapper<BizOdds>()
                .in(BizOdds::getMatchId, matchIds)
                .eq(BizOdds::getPoolCode, poolCode)
                .isNotNull(BizOdds::getGoalLine) // 确保 goal_line 字段不为空
        );

        if (CollUtil.isEmpty(oddsList)) {
            return Collections.emptyMap();
        }

        // 将查询结果转换为 Map，方便快速查找
        return oddsList.stream()
            .collect(Collectors.toMap(BizOddsVo::getMatchId, BizOddsVo::getGoalLine, (existing, replacement) -> existing));
    }

    /**
     * 查询比赛赔率
     *
     * @param id 主键
     * @return 比赛赔率
     */
    @Override
    public BizOddsVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询比赛赔率列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛赔率分页列表
     */
    @Override
    public TableDataInfo<BizOddsVo> queryPageList(BizOddsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizOdds> lqw = buildQueryWrapper(bo);
        Page<BizOddsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的比赛赔率列表
     *
     * @param bo 查询条件
     * @return 比赛赔率列表
     */
    @Override
    public List<BizOddsVo> queryList(BizOddsBo bo) {
        LambdaQueryWrapper<BizOdds> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizOdds> buildQueryWrapper(BizOddsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizOdds> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizOdds::getId);
        lqw.eq(bo.getMatchId() != null, BizOdds::getMatchId, bo.getMatchId());
        lqw.eq(StringUtils.isNotBlank(bo.getPoolCode()), BizOdds::getPoolCode, bo.getPoolCode());
        lqw.eq(StringUtils.isNotBlank(bo.getGoalLine()), BizOdds::getGoalLine, bo.getGoalLine());
        lqw.eq(bo.getHomeOdds() != null, BizOdds::getHomeOdds, bo.getHomeOdds());
        lqw.eq(bo.getDrawOdds() != null, BizOdds::getDrawOdds, bo.getDrawOdds());
        lqw.eq(bo.getAwayOdds() != null, BizOdds::getAwayOdds, bo.getAwayOdds());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizOdds::getStatus, bo.getStatus());
        lqw.eq(bo.getSingle() != null, BizOdds::getSingle, bo.getSingle());
        lqw.eq(bo.getUpdatedAt() != null, BizOdds::getUpdatedAt, bo.getUpdatedAt());
        lqw.eq(StringUtils.isNotBlank(bo.getOddsData()), BizOdds::getOddsData, bo.getOddsData());
        return lqw;
    }

    /**
     * 新增比赛赔率
     *
     * @param bo 比赛赔率
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizOddsBo bo) {
        BizOdds add = MapstructUtils.convert(bo, BizOdds.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改比赛赔率
     *
     * @param bo 比赛赔率
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizOddsBo bo) {
        BizOdds update = MapstructUtils.convert(bo, BizOdds.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizOdds entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除比赛赔率信息
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
    public Boolean saveOrUpdate(BizOddsBo bo) {

        if(bo.getPoolCode() != null && (bo.getPoolCode().toUpperCase().equals("HAD") || bo.getPoolCode().toUpperCase().equals("HHAD"))){
            BizOddsHistoryVo bizOddsHistoryVo = bizOddsHistoryService.queryOneByMatchIdAndPoolCode(bo.getMatchId(), bo.getPoolCode());
            boolean diff = false;
            if(bizOddsHistoryVo == null){
                diff = true;
            }else {
                if(!bizOddsHistoryVo.getHomeOdds().equals(bo.getHomeOdds())){
                    diff = true;
                }
                if(!bizOddsHistoryVo.getHomeOdds().equals(bo.getHomeOdds())){
                    diff = true;
                }
                if(!bizOddsHistoryVo.getAwayOdds().equals(bo.getAwayOdds())){
                    diff = true;
                }
            }

            if(diff){
                BizOddsHistoryBo bizOddsHistoryBo = new BizOddsHistoryBo();
                BeanUtil.copyProperties(bo,bizOddsHistoryBo);
                bizOddsHistoryService.insertByBo(bizOddsHistoryBo);
            }
        }

        if(bo.getId() != null && bo.getId() > 0){
            return this.updateByBo(bo);
        }

        BizOddsVo vo = this.queryById(bo.getId());
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
    public List<BizOddsVo> queryList(LambdaQueryWrapper<BizOdds> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizOddsVo queryOne(LambdaQueryWrapper<BizOdds> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }
}
