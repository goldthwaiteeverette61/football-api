package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizMatches;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.bo.BizOddsBo;
import org.dromara.biz.domain.vo.*;
import org.dromara.biz.mapper.BizMatchesMapper;
import org.dromara.biz.service.IBizLeaguesService;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.biz.service.IBizTeamsService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 比赛信息Service业务层处理
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BizMatchesServiceImpl extends BaseImpl<BizMatches,BizMatchesVo> implements IBizMatchesService {

    private final BizMatchesMapper baseMapper;
    private final IBizOddsService bizOddsService;
    private final IBizLeaguesService iBizLeaguesService;
    private final IBizTeamsService bizTeamsService;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 【新增】根据ID列表批量查询比赛信息的实现
     */
    @Override
    public List<BizMatchesVo> queryListByIds(List<Long> matchIds) {
        // 如果傳入的ID列表為空，直接返回空集合，避免無效查詢
        if (CollUtil.isEmpty(matchIds)) {
            return Collections.emptyList();
        }
        // 使用 MyBatis-Plus 的 in 查詢
        return baseMapper.selectVoList(this.getLqw().in(BizMatches::getMatchId, matchIds));
    }


    @Override
    public LambdaQueryWrapper<BizMatches> getLqw() {
        return super.lqw();
    }

    @Override
    public Boolean insertOrUpdate(BizMatchesBo bo) {
        BizMatches bizMatches = MapstructUtils.convert(bo, BizMatches.class);
        return baseMapper.insertOrUpdate(bizMatches);
    }

    @Override
    public List<BizMatchesVo> queryFinishedMatches() {
        return baseMapper.selectVoList(
            new LambdaQueryWrapper<BizMatches>()
                .eq(BizMatches::getStatus, "Finished")
        );
    }

    /**
     * 核心修改：重构为单一的高性能数据库查询
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 需要更新实时比分的比赛列表
     */
    @Override
    public List<BizMatchesVo> queryMatchesForLiveScoreUpdate(Date startTime, Date endTime) {
        // 通过调用 Mapper 中的自定义 XML 查询，将多次数据库交互合并为一次
        return baseMapper.selectMatchesForLiveScoreUpdate(startTime, endTime);
    }

    /**
     * 查询比赛信息
     *
     * @param matchId 主键
     * @return 比赛信息
     */
    @Override
    public BizMatchesVo queryById(Long matchId){
        return baseMapper.selectVoById(matchId);
    }

    /**
     * 分页查询比赛信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛信息分页列表
     */
    @Override
    public TableDataInfo<BizMatchesVo> queryPageList(BizMatchesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizMatches> lqw = buildQueryWrapper(bo);
        Page<BizMatchesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的比赛信息列表
     *
     * @param bo 查询条件
     * @return 比赛信息列表
     */
    @Override
    public List<BizMatchesVo> queryList(BizMatchesBo bo) {
        LambdaQueryWrapper<BizMatches> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizMatches> buildQueryWrapper(BizMatchesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizMatches> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizMatches::getMatchDatetime);

        // 【核心修改】根据 geBusinessDate 标志动态选择查询方式
        if (bo.getBusinessDate() != null) {
            if (Boolean.TRUE.equals(bo.getGeBusinessDate())) {
                // 大于等于查询
                lqw.ge(BizMatches::getBusinessDate, bo.getBusinessDate());
            } else {
                // 精确等于查询
                lqw.eq(BizMatches::getBusinessDate, bo.getBusinessDate());
            }
        }

        if (bo.getGeMatchDatetime() != null && bo.getGeMatchDatetime()) {
            lqw.ge(bo.getMatchDatetime() != null, BizMatches::getMatchDatetime, bo.getMatchDatetime());
        }

        if (bo.getExcludePayout() != null && bo.getExcludePayout()) {
            lqw.ne(BizMatches::getStatus, "Payout");
        }

        return lqw;
    }

    /**
     * 新增比赛信息
     *
     * @param bo 比赛信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizMatchesBo bo) {
        BizMatches add = MapstructUtils.convert(bo, BizMatches.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMatchId(add.getMatchId());
        }
        return flag;
    }

    /**
     * 修改比赛信息
     *
     * @param bo 比赛信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizMatchesBo bo) {
        BizMatches update = MapstructUtils.convert(bo, BizMatches.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizMatches entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除比赛信息信息
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

    @Override
    public List<BizMatchesVo> nePayoutList() {
        LambdaQueryWrapper<BizMatches> lqw = Wrappers.lambdaQuery();
        lqw.ne(BizMatches::getStatus,"Payout");
        lqw.lt(BizMatches::getMatchDatetime,new Date());
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 【核心重构】查询计算器列表（不含赔率），并进行高性能数据组装
     */
    @Override
    public List<BizMatchesGroupVo> queryCalculatorListWithoutOdds(BizMatchesBo bo) {
        List<BizMatchesVo> matches = this.queryList(bo);
        if (CollUtil.isEmpty(matches)) {
            return new ArrayList<>();
        }

        // 【核心优化】调用公共方法，批量获取并组装联赛和球队Logo信息
        enrichBasicMatchDetails(matches);

        // 按 businessDate 分组
        Map<Date, List<BizMatchesVo>> groupedByDate = matches.stream()
            .collect(Collectors.groupingBy(
                BizMatchesVo::getBusinessDate,
                TreeMap::new,
                Collectors.toList()
            ));

        // 构造成最终返回给前端的结构
        return groupedByDate.entrySet().stream()
            .map(entry -> {
                BizMatchesGroupVo groupVo = new BizMatchesGroupVo();
                groupVo.setBusinessDate(entry.getKey());
                groupVo.setWeekday(DateUtil.dayOfWeekEnum(entry.getKey()).toChinese("星期"));
                groupVo.setBizMatchesVoList(entry.getValue());
                return groupVo;
            })
            .collect(Collectors.toList());
    }

    /**
     * 【核心重构】查询计算器列表（含赔率），并进行高性能数据组装
     */
    @Override
    public List<BizMatchesGroupVo> queryCalculatorList(BizMatchesBo bo) {
        List<BizMatchesVo> matches = this.queryList(bo);
        if (CollUtil.isEmpty(matches)) {
            return new ArrayList<>();
        }

        // 【核心优化】调用公共方法，批量获取并组装联赛和球队Logo信息
        enrichBasicMatchDetails(matches);

        // 提取所有比赛ID，用于查询赔率
        List<Long> matchIds = matches.stream().map(BizMatchesVo::getMatchId).distinct().collect(Collectors.toList());

        // 一次性查询所有相关赔率
        BizOddsBo oddsQueryBo = new BizOddsBo();
        oddsQueryBo.setMatchIds(matchIds);
        oddsQueryBo.setPoolCodes(Arrays.asList("HAD", "HHAD"));
        List<BizOddsVo> oddsList = bizOddsService.queryList(oddsQueryBo);

        Map<Long, Map<String, BizOddsVo>> oddsMap = oddsList.stream()
            .collect(Collectors.groupingBy(
                BizOddsVo::getMatchId,
                Collectors.toMap(BizOddsVo::getPoolCode, Function.identity())
            ));

        // 将赔率数据组装到比赛对象中
        for (BizMatchesVo match : matches) {
            Map<String, BizOddsVo> matchOdds = oddsMap.get(match.getMatchId());
            if (matchOdds != null) {
                match.setHad(matchOdds.get("HAD"));
                match.setHhad(matchOdds.get("HHAD"));
            }
        }

        // 按 businessDate 分组
        Map<Date, List<BizMatchesVo>> groupedByDate = matches.stream()
            .collect(Collectors.groupingBy(
                BizMatchesVo::getBusinessDate,
                TreeMap::new,
                Collectors.toList()
            ));

        // 构造成最终返回给前端的结构
        return groupedByDate.entrySet().stream()
            .map(entry -> {
                BizMatchesGroupVo groupVo = new BizMatchesGroupVo();
                groupVo.setBusinessDate(entry.getKey());
                groupVo.setWeekday(DateUtil.dayOfWeekEnum(entry.getKey()).toChinese("星期"));
                groupVo.setBizMatchesVoList(entry.getValue());
                return groupVo;
            })
            .collect(Collectors.toList());
    }

    /**
     * 【核心新增】私有辅助方法，用于批量为比赛列表填充基础信息
     *
     * @param matches 待处理的比赛列表
     */
    private void enrichBasicMatchDetails(List<BizMatchesVo> matches) {
        if (CollUtil.isEmpty(matches)) {
            return;
        }

        // 1. 收集所有不重复的联赛ID和球队ID
        Set<String> leagueIds = new HashSet<>();
        Set<Long> teamIds = new HashSet<>();
        for (BizMatchesVo match : matches) {
            leagueIds.add(match.getLeagueId());
            teamIds.add(match.getHomeTeamId());
            teamIds.add(match.getAwayTeamId());
        }

        // 2. 一次性查询出所有相关的联赛和球队信息
        Map<String, BizLeaguesVo> leagueMap = iBizLeaguesService.queryMapByIds(new ArrayList<>(leagueIds));
        Map<Long, BizTeamsVo> teamMap = bizTeamsService.queryMapByIds(new ArrayList<>(teamIds));

        // 3. 遍历比赛列表，从Map中获取信息并填充
        for (BizMatchesVo match : matches) {
//            BizLeaguesVo league = leagueMap.get(match.getLeagueId());
//            if (league != null) {
//                match.setLeagueName(league.getAbbrName());
//            }

            BizTeamsVo homeTeam = teamMap.get(match.getHomeTeamId());
            if (homeTeam != null) {
                match.setHomeTeamLogo(homeTeam.getLogo());
            }

            BizTeamsVo awayTeam = teamMap.get(match.getAwayTeamId());
            if (awayTeam != null) {
                match.setAwayTeamLogo(awayTeam.getLogo());
            }
        }
    }

    @Override
    public Map<Long, BizMatchesVo> queryMapByIds(List<Long> matchIds) {
        if (matchIds == null || matchIds.isEmpty()) {
            return Collections.emptyMap();
        }
        // 1. 查询比赛基础信息
        LambdaQueryWrapper<BizMatches> lqw = new LambdaQueryWrapper<>();
        lqw.in(BizMatches::getMatchId, matchIds);
        List<BizMatchesVo> matches = baseMapper.selectVoList(lqw);

        if (matches.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. 一次性查询出所有相关的赔率
        BizOddsBo oddsQueryBo = new BizOddsBo();
        oddsQueryBo.setMatchIds(matchIds);
        oddsQueryBo.setPoolCodes(Arrays.asList("HAD", "HHAD"));
        List<BizOddsVo> oddsList = bizOddsService.queryList(oddsQueryBo);

        // 3. 将赔率按 matchId 分组
        Map<Long, Map<String, BizOddsVo>> oddsMap = oddsList.stream()
            .collect(Collectors.groupingBy(
                BizOddsVo::getMatchId,
                Collectors.toMap(BizOddsVo::getPoolCode, Function.identity())
            ));

        // 4. 将赔率数据附加到比赛对象中
        for (BizMatchesVo match : matches) {
            Map<String, BizOddsVo> matchOdds = oddsMap.get(match.getMatchId());
            if (matchOdds != null) {
                match.setHad(matchOdds.get("HAD"));
                match.setHhad(matchOdds.get("HHAD"));
            }

//            BizLeaguesVo bizLeaguesVo = iBizLeaguesService.queryById(match.getLeagueId());
//            if(bizLeaguesVo != null){
//                match.setLeagueName(bizLeaguesVo.getName());
//            }
        }

        // 5. 将包含完整赔率的比赛列表转换为Map返回
        return matches.stream().collect(Collectors.toMap(BizMatchesVo::getMatchId, Function.identity(), (key1, key2) -> key2));
    }

    /**
     * 执行列表查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public List<BizMatchesVo> queryList(LambdaQueryWrapper<BizMatches> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizMatchesVo queryOne(LambdaQueryWrapper<BizMatches> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizMatchesBo bo) {
        BizMatches update = MapstructUtils.convert(bo, BizMatches.class);
        return baseMapper.saveOrUpdate(update);
    }
}
