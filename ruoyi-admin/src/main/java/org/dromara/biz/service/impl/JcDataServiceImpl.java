package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizMatches;
import org.dromara.biz.domain.BizOdds;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.domain.vo.BizOddsVo;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.biz.service.IJcDataService;
import org.dromara.common.core.constant.CacheNames;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 竞彩资料服务实现类
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class JcDataServiceImpl implements IJcDataService {

    private final IBizMatchesService bizMatchesService;
    private final IBizOddsService bizOddsService;

    @Override
    @Cacheable(cacheNames = CacheNames.ODDS_DATA)
    public JSONObject getFormattedOddsByPools() {
        // 【已修改】增加了 orderByAsc(BizMatches::getMatchDatetime) 来按比赛时间升序排序
        List<BizMatchesVo> matches = bizMatchesService.queryList(
            bizMatchesService.getLqw()
                .eq(BizMatches::getMatchStatus, 0)
                .isNotNull(BizMatches::getSportteryMatchId)
                .orderByAsc(BizMatches::getMatchDatetime)
        );

        if (CollUtil.isEmpty(matches)) {
            return new JSONObject().set("matchList", new JSONArray());
        }

        // 2. 根据比赛ID列表，一次性查询出所有相关的赔率
        List<Long> matchIds = matches.stream().map(BizMatchesVo::getMatchId).collect(Collectors.toList());
        List<BizOddsVo> allOdds = bizOddsService.queryList(new LambdaQueryWrapper<BizOdds>().in(BizOdds::getMatchId, matchIds));
        Map<Long, List<BizOddsVo>> oddsByMatchId = allOdds.stream().collect(Collectors.groupingBy(BizOddsVo::getMatchId));

        // 3. 组装前端所需的 JSON 结构
        JSONArray formattedMatches = new JSONArray();
        for (BizMatchesVo match : matches) {
            JSONObject matchJson = formatMatchBaseInfo(match);
            JSONObject oddsJson = formatOddsInfo(oddsByMatchId.get(match.getMatchId()));
            matchJson.set("odds", oddsJson);
            formattedMatches.add(matchJson);
        }

        return new JSONObject().set("matchList", formattedMatches);
    }

    /**
     * 辅助方法：格式化比赛基本信息
     */
    private JSONObject formatMatchBaseInfo(BizMatchesVo match) {
        JSONObject matchJson = new JSONObject();
        matchJson.set("matchId", match.getMatchId());
        matchJson.set("matchName", match.getMatchName());
        matchJson.set("matchNumStr", match.getMatchNumStr());
        matchJson.set("leagueName", match.getLeagueName());
        matchJson.set("matchDatetime", match.getMatchDatetime());
        matchJson.set("homeTeamName", match.getHomeTeamName());
        matchJson.set("awayTeamName", match.getAwayTeamName());
        return matchJson;
    }

    /**
     * 辅助方法：格式化赔率信息
     */
    private JSONObject formatOddsInfo(List<BizOddsVo> oddsList) {
        JSONObject oddsJson = new JSONObject();
        if (CollUtil.isEmpty(oddsList)) {
            return oddsJson;
        }

        for (BizOddsVo odds : oddsList) {
            String poolCode = odds.getPoolCode();
//            String simplifiedCode = poolCode.toLowerCase().replace("had");

            if ("HAD".equals(poolCode) || "HHAD".equals(poolCode)) {
                JSONObject spfDetail = new JSONObject();
                spfDetail.set("H", odds.getHomeOdds());
                spfDetail.set("D", odds.getDrawOdds());
                spfDetail.set("A", odds.getAwayOdds());
                if ("HHAD".equals(poolCode)) {
                    spfDetail.set("goalLine", odds.getGoalLine());
                }
                // 根据您的前端结构，决定是放入 spf 还是 hhad
                oddsJson.set(poolCode.toUpperCase(), spfDetail);
            } else {
                // 对于 CRS, TTG, HAFU，直接解析 odds_data JSON
                if (odds.getOddsData() != null) {
                    oddsJson.set(poolCode.toUpperCase(), JSONUtil.parse(odds.getOddsData()));
                }
            }
        }
        return oddsJson;
    }
}

