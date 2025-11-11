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
import org.dromara.biz.domain.bo.BizOddsHistoryBo;
import org.dromara.biz.domain.vo.BizOddsHistoryVo;
import org.dromara.biz.domain.BizOddsHistory;
import org.dromara.biz.mapper.BizOddsHistoryMapper;
import org.dromara.biz.service.IBizOddsHistoryService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 比赛赔率历史Service业务层处理
 *
 * @author Lion Li
 * @date 2025-11-10
 */
@RequiredArgsConstructor
@Service
public class BizOddsHistoryServiceImpl extends BaseImpl<BizOddsHistory,BizOddsHistoryVo> implements IBizOddsHistoryService {

    private final BizOddsHistoryMapper baseMapper;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    @Override
    public LambdaQueryWrapper<BizOddsHistory> getLqw() {
        return super.lqw();
    }

    /**
     * 【新增】实现查询最后一条历史记录
     */
    @Override
    public BizOddsHistoryVo queryLastHistoryByMatchIdAndPoolCode(Long matchId, String poolCode) {
        LambdaQueryWrapper<BizOddsHistory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BizOddsHistory::getMatchId, matchId);
        lqw.eq(BizOddsHistory::getPoolCode, poolCode);
        lqw.orderByDesc(BizOddsHistory::getHistoryId); // 按主键ID倒序，确保拿到最后一条
        lqw.last("LIMIT 1"); // 只取一条

        BizOddsHistory history = baseMapper.selectOne(lqw);
        return MapstructUtils.convert(history, BizOddsHistoryVo.class);
    }

    @Override
    public List<BizOddsHistoryVo> queryHistoryByMatchId(Long matchId) {
        LambdaQueryWrapper<BizOddsHistory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BizOddsHistory::getMatchId, matchId);
        lqw.orderByDesc(BizOddsHistory::getCreateTime);
        return MapstructUtils.convert(baseMapper.selectList(lqw), BizOddsHistoryVo.class);
    }

    @Override
    public BizOddsHistoryVo queryOneByMatchIdAndPoolCode(Long matchId, String poolCode) {
        LambdaQueryWrapper<BizOddsHistory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BizOddsHistory::getMatchId, matchId);
        lqw.eq(BizOddsHistory::getPoolCode, poolCode);
        lqw.orderByDesc(BizOddsHistory::getCreateTime).last("limit 1"); // 按时间倒序
        return baseMapper.selectVoOne(lqw);
    }

    @Override
    public List<BizOddsHistoryVo> queryHistoryByMatchIdAndPoolCode(Long matchId, String poolCode) {
        LambdaQueryWrapper<BizOddsHistory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BizOddsHistory::getMatchId, matchId);
        lqw.eq(BizOddsHistory::getPoolCode, poolCode);
        lqw.orderByDesc(BizOddsHistory::getCreateTime); // 按时间倒序
        return MapstructUtils.convert(baseMapper.selectList(lqw), BizOddsHistoryVo.class);
    }

    /**
     * 查询比赛赔率历史
     *
     * @param historyId 主键
     * @return 比赛赔率历史
     */
    @Override
    public BizOddsHistoryVo queryById(Long historyId){
        return baseMapper.selectVoById(historyId);
    }

    /**
     * 分页查询比赛赔率历史列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛赔率历史分页列表
     */
    @Override
    public TableDataInfo<BizOddsHistoryVo> queryPageList(BizOddsHistoryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizOddsHistory> lqw = buildQueryWrapper(bo);
        Page<BizOddsHistoryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的比赛赔率历史列表
     *
     * @param bo 查询条件
     * @return 比赛赔率历史列表
     */
    @Override
    public List<BizOddsHistoryVo> queryList(BizOddsHistoryBo bo) {
        LambdaQueryWrapper<BizOddsHistory> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizOddsHistory> buildQueryWrapper(BizOddsHistoryBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizOddsHistory> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizOddsHistory::getHistoryId);
        lqw.eq(bo.getOddsId() != null, BizOddsHistory::getOddsId, bo.getOddsId());
        lqw.eq(bo.getMatchId() != null, BizOddsHistory::getMatchId, bo.getMatchId());
        lqw.eq(bo.getSportteryMatchId() != null, BizOddsHistory::getSportteryMatchId, bo.getSportteryMatchId());
        lqw.eq(StringUtils.isNotBlank(bo.getPoolCode()), BizOddsHistory::getPoolCode, bo.getPoolCode());
        lqw.eq(StringUtils.isNotBlank(bo.getGoalLine()), BizOddsHistory::getGoalLine, bo.getGoalLine());
        lqw.eq(bo.getHomeOdds() != null, BizOddsHistory::getHomeOdds, bo.getHomeOdds());
        lqw.eq(bo.getDrawOdds() != null, BizOddsHistory::getDrawOdds, bo.getDrawOdds());
        lqw.eq(bo.getAwayOdds() != null, BizOddsHistory::getAwayOdds, bo.getAwayOdds());
        lqw.eq(StringUtils.isNotBlank(bo.getOddsData()), BizOddsHistory::getOddsData, bo.getOddsData());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizOddsHistory::getStatus, bo.getStatus());
        lqw.eq(bo.getSingle() != null, BizOddsHistory::getSingle, bo.getSingle());
        return lqw;
    }

    /**
     * 新增比赛赔率历史
     *
     * @param bo 比赛赔率历史
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizOddsHistoryBo bo) {
        BizOddsHistory add = MapstructUtils.convert(bo, BizOddsHistory.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setHistoryId(add.getHistoryId());
        }
        return flag;
    }

    /**
     * 修改比赛赔率历史
     *
     * @param bo 比赛赔率历史
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizOddsHistoryBo bo) {
        BizOddsHistory update = MapstructUtils.convert(bo, BizOddsHistory.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizOddsHistory entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除比赛赔率历史信息
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
    public List<BizOddsHistoryVo> queryList(LambdaQueryWrapper<BizOddsHistory> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizOddsHistoryVo queryOne(LambdaQueryWrapper<BizOddsHistory> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizOddsHistoryBo bo) {
        BizOddsHistory update = MapstructUtils.convert(bo, BizOddsHistory.class);
        return baseMapper.saveOrUpdate(update);
    }
}
