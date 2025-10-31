package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizLeaguesBo;
import org.dromara.biz.domain.bo.BizMatchResultsBo;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.bo.BizOddsBo;
import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.domain.vo.BizOddsVo;
import org.dromara.biz.service.IBizLeaguesService;
import org.dromara.biz.service.IBizMatchResultsService;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.biz.service.IBizTeamsService;
import org.dromara.biz.utils.MatchResultCalculator;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 【新增服务】比赛数据采集与处理服务 (500.com 数据源)
 * @description: 该服务专门用于从 live.500.com 采集和处理比赛数据。
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MatchDataCollection500ServiceImpl {

    private final IBizMatchesService bizMatchesService;
    private final IBizOddsService bizOddsService;
    private final IBizLeaguesService bizLeaguesService;
    private final IBizTeamsService bizTeamsService;
    private final IBizMatchResultsService bizMatchResultsService;

    /**
     * 主流程：从 500.com 采集比赛信息并生成赛果
     */
    public void collectAndProcessMatches() {
        log.info("===== 开始执行 500.com 比赛数据采集与更新任务 =====");
        // 500.com 的数据源是一个 JS 文件，其中包含一个名为 live_data 的全局变量
        String url = "https://idata.500.com/zc/jczq/jczq_live.js";
        try {
            // 1. 获取并解析数据源
            log.info(" -> 正在从URL采集数据: {}", url);
            String responseBody = HttpUtil.get(url);

            // 清理JS变量定义，得到纯JSON字符串
            String jsonString = responseBody.replace("var live_data =", "").replace(";", "").trim();
            if (!JSONUtil.isJson(jsonString)) {
                log.error("从 500.com 获取的数据不是有效的JSON格式。");
                return;
            }
            JSONObject liveData = JSONUtil.parseObj(jsonString);
            JSONArray matches = liveData.getJSONArray("matches");
            log.info(" -> 成功获取 {} 场比赛数据，开始处理...", matches.size());

            // 2. 遍历处理每一场比赛
            for (JSONObject matchJson : matches.jsonIter()) {
                try {
                    processSingleMatch(matchJson);
                } catch (Exception e) {
                    log.error("处理单场比赛数据时出错, match JSON: {}", matchJson.toString(), e);
                }
            }

            log.info("===== 500.com 比赛数据采集与更新任务执行完毕 =====");
        } catch (Exception e) {
            log.error("从 500.com 采集数据时发生严重异常", e);
            throw new RuntimeException("数据采集与更新任务失败", e);
        }
    }

    /**
     * 处理从 500.com 获取的单场比赛JSON数据
     * (包含联赛、球队、比赛、赔率的保存和赛果结算)
     */
    @Transactional(rollbackFor = Exception.class)
    public void processSingleMatch(JSONObject matchJson) {
        Long matchId = matchJson.getLong("mid");
        if (matchId == null) {
            log.warn("跳过一条没有 matchId 的记录。");
            return;
        }

        // 1. 保存或更新联赛和球队信息
        saveOrUpdateLeague(matchJson);
        saveOrUpdateTeam(matchJson, true); // 主队
        saveOrUpdateTeam(matchJson, false); // 客队

        // 2. 保存或更新比赛信息
        BizMatchesBo matchBo = parseMatchInfo(matchJson);
        bizMatchesService.insertOrUpdate(matchBo);

        // 3. 保存或更新赔率信息
        processAndSaveOdds(matchJson);

        // 4. 检查比赛是否完赛并需要结算 (500.com status: 4 = 完场)
        if ("4".equals(matchJson.getStr("status"))) {
            BizMatchesVo currentMatchInDb = bizMatchesService.queryById(matchId);
            if (currentMatchInDb != null && !"Payout".equals(currentMatchInDb.getStatus())) {
                log.info("检测到比赛ID: {} 已完赛 (来自 500.com)，开始进行结算...", matchId);
                // 确保Vo中有最新的比分信息
                currentMatchInDb.setFullScore(matchBo.getFullScore());
                currentMatchInDb.setHalfScore(matchBo.getHalfScore());
                finalizeMatch(currentMatchInDb);
            }
        }
    }

    private void saveOrUpdateLeague(JSONObject matchJson) {
        BizLeaguesBo leagueBo = new BizLeaguesBo();
        leagueBo.setLeagueId(matchJson.getStr("lid"));
        leagueBo.setName(matchJson.getStr("gname")); // 全称
        leagueBo.setAbbrName(matchJson.getStr("lname_s")); // 简称
        leagueBo.setBackColor(matchJson.getStr("color"));
        bizLeaguesService.saveOrUpdate(leagueBo);
    }

    private BizTeamsBo saveOrUpdateTeam(JSONObject matchJson, boolean isHome) {
        String idKey = isHome ? "htid" : "atid";
        String nameKey = isHome ? "hname" : "aname";
        String rankKey = isHome ? "hrank" : "arank";

        BizTeamsBo teamBo = new BizTeamsBo();
        teamBo.setTeamId(matchJson.getLong(idKey));
        teamBo.setFullName(matchJson.getStr(nameKey));
        teamBo.setAbbrName(matchJson.getStr(nameKey)); // 500.com 未区分简称，统一使用全称
        teamBo.setRanks(matchJson.getStr(rankKey));
        bizTeamsService.saveOrUpdate(teamBo);
        return teamBo;
    }

    private BizMatchesBo parseMatchInfo(JSONObject matchJson) {
        BizMatchesBo matchBo = new BizMatchesBo();
        matchBo.setMatchId(matchJson.getLong("mid"));
        matchBo.setMatchName(matchJson.getStr("hname") + " vs " + matchJson.getStr("aname"));
        // 500.com 的 match_num 在 jcodds 对象中
        JSONObject jcodds = matchJson.getJSONObject("jcodds");
        if (jcodds != null) {
            matchBo.setMatchNumStr(jcodds.getStr("match_num"));
        }
        matchBo.setMatchDatetime(DateUtil.date(matchJson.getLong("starttime") * 1000));
        matchBo.setLeagueId(matchJson.getStr("lid"));
        matchBo.setHomeTeamId(matchJson.getLong("htid"));
        matchBo.setHomeTeamName(matchJson.getStr("hname"));
        matchBo.setAwayTeamId(matchJson.getLong("atid"));
        matchBo.setAwayTeamName(matchJson.getStr("aname"));
        matchBo.setFullScore(matchJson.getStr("hscore", "0") + ":" + matchJson.getStr("ascore", "0"));
        matchBo.setHalfScore(matchJson.getStr("hhalfscore", "0") + ":" + matchJson.getStr("ahalfscore", "0"));

        // 模拟 matchMinute 以便复用 Redis 逻辑
        String matchMinute = calculateMatchMinute(matchJson.getInt("status", 0), matchJson.getStr("time"));
        matchBo.setMatchMinute(matchMinute);

        // Redis防抖，防止比赛时间倒退
        String redisKey = "match:minute:" + matchBo.getMatchId();
        RedisUtils.setCacheObject(redisKey, matchMinute, Duration.ofHours(24));
        return matchBo;
    }

    private String calculateMatchMinute(int status, String time) {
        switch (status) {
            case 1: return time; // 上半场
            case 2: return "HT"; // 中场
            case 3: // 下半场
                try {
                    return String.valueOf(45 + Integer.parseInt(time));
                } catch (NumberFormatException e) {
                    return "45+";
                }
            case 4: return "FT"; // 完场
            default: return "NS"; // 未开始或其他
        }
    }

    private void processAndSaveOdds(JSONObject matchJson) {
        Long matchId = matchJson.getLong("mid");
        JSONObject jcodds = matchJson.getJSONObject("jcodds");
        if (jcodds == null) return;

        // 处理胜平负 (HAD)
        if (jcodds.containsKey("spf")) {
            JSONObject spf = jcodds.getJSONObject("spf");
            BizOddsBo hadOdds = new BizOddsBo();
            hadOdds.setMatchId(matchId);
            hadOdds.setPoolCode("HAD");
            hadOdds.setHomeOdds(spf.getBigDecimal("win"));
            hadOdds.setDrawOdds(spf.getBigDecimal("draw"));
            hadOdds.setAwayOdds(spf.getBigDecimal("lost"));
            hadOdds.setStatus("Selling"); // 假设在售
            hadOdds.setSingle(1); // 假设可单关
            saveOrUpdateOdds(hadOdds);
        }

        // 处理让球胜平负 (HHAD)
        if (jcodds.containsKey("rqspf")) {
            JSONObject rqspf = jcodds.getJSONObject("rqspf");
            BizOddsBo hhadOdds = new BizOddsBo();
            hhadOdds.setMatchId(matchId);
            hhadOdds.setPoolCode("HHAD");
            hhadOdds.setGoalLine(rqspf.getStr("letgoal"));
            hhadOdds.setHomeOdds(rqspf.getBigDecimal("win"));
            hhadOdds.setDrawOdds(rqspf.getBigDecimal("draw"));
            hhadOdds.setAwayOdds(rqspf.getBigDecimal("lost"));
            hhadOdds.setStatus("Selling");
            hhadOdds.setSingle(1);
            saveOrUpdateOdds(hhadOdds);
        }
    }

    /**
     * 【新增】辅助方法：保存或更新赔率记录
     */
    private void saveOrUpdateOdds(BizOddsBo oddsBo) {
        BizOddsBo queryBo = new BizOddsBo();
        queryBo.setMatchId(oddsBo.getMatchId());
        queryBo.setPoolCode(oddsBo.getPoolCode());
        List<BizOddsVo> existingOddsList = bizOddsService.queryList(queryBo);

        if (CollUtil.isNotEmpty(existingOddsList)) {
            oddsBo.setId(existingOddsList.get(0).getId());
            bizOddsService.updateByBo(oddsBo);
        } else {
            bizOddsService.insertByBo(oddsBo);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void finalizeMatch(BizMatchesVo matchToFinalize) {
        Long matchId = matchToFinalize.getMatchId();

        if (StrUtil.isBlank(matchToFinalize.getFullScore()) || !matchToFinalize.getFullScore().contains(":")) {
            log.warn("比赛ID: {} 的最终比分格式不正确 ('{}')，无法结算。", matchId, matchToFinalize.getFullScore());
            throw new ServiceException("比分格式错误，无法结算");
        }

        Map<Long, String> goalLineMap = bizOddsService.queryGoalLinesByMatchIds(List.of(matchId), "HHAD");
        List<BizMatchResultsBo> results = MatchResultCalculator.calculateAllResults(matchToFinalize, goalLineMap.get(matchId));

        if (CollUtil.isNotEmpty(results)) {
            bizMatchResultsService.deleteByMatchId(matchId);
            bizMatchResultsService.insertBatchByBo(results);

            BizMatchesBo statusUpdateBo = new BizMatchesBo();
            statusUpdateBo.setMatchId(matchId);
            statusUpdateBo.setStatus("Payout"); // 使用系统内部状态
            statusUpdateBo.setMatchStatus("4"); // 对应 500.com 的完场状态
            statusUpdateBo.setFullScore(matchToFinalize.getFullScore());
            bizMatchesService.updateByBo(statusUpdateBo);

            log.info("比赛ID: {} 内部赛果生成并更新状态为Payout成功！", matchId);
        } else {
            log.warn("未能为比赛ID: {} 生成任何赛果，结算中止。", matchId);
        }
    }
}

