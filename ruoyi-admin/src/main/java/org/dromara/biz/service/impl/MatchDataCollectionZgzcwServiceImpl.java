package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizLeaguesBo;
import org.dromara.biz.domain.bo.BizMatchResultsBo;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.service.*;
import org.dromara.biz.utils.MatchResultCalculator;
import org.dromara.common.core.exception.ServiceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpCookie;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 【核心重構】比賽數據採集與處理服務 (zgzcw.com 數據源)
 * @description: 該服務已重構為使用 Jsoup 直接解析 live.zgzcw.com/jz/ HTML 頁面。
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MatchDataCollectionZgzcwServiceImpl {

    private final IBizMatchesService bizMatchesService;
    private final IBizOddsService bizOddsService;
    private final IBizLeaguesService bizLeaguesService;
    private final IBizTeamsService bizTeamsService;
    private final IBizMatchResultsService bizMatchResultsService;

//    public static void main(String[] args) {
//        LocalDate today = LocalDate.now();
//        System.out.println(today.minusDays(-1));
//        System.out.println(today.minusDays(2));
//    }

    /**
     * 【核心修改】主流程：從 zgzcw.com 採集多個日期的比賽信息
     * @description: 通過模擬 POST 請求來獲取不同日期的數據。
     */
    public void collectAndProcessMatches() {
        int start= -2;
        int end = 2;
        LocalDate today = LocalDate.now();

        // 【新增】在循環外先訪問一次主頁以獲取必要的 cookie
        List<HttpCookie> cookies = null;
        try {
            HttpResponse response = HttpRequest.get("https://live.zgzcw.com/jz/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .execute();
            cookies = response.getCookies();
            if (CollUtil.isNotEmpty(cookies)) {
            } else {
                log.warn(" -> 未能從主頁獲取到任何 cookies，後續請求可能失敗。");
            }
        } catch (Exception e) {
            log.error(" -> 訪問主頁時出錯，後續請求可能失敗。", e);
        }

        // 循環採集后天及過去2天的數據 (-2，-1，0, 1, 2)
        for (int i = start; i <= end; i++) {
            LocalDate targetDate = today.minusDays(i);
            String dateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // API 地址
            String url = "https://live.zgzcw.com/ls/AllData.action";

            try {
//                log.info(" -> 正在採集日期 [{}] 的比賽數據, URL: {}", dateStr, url);

                // 【核心修正】增加 Sec-Fetch-* 頭部參數，更完美地模擬瀏覽器行為
                HttpRequest postRequest = HttpRequest.post(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html, */*; q=0.01")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Origin", "https://live.zgzcw.com")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Referer", "https://live.zgzcw.com/jz/")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .form("code", "201")
                    .form("date", dateStr)
                    .form("ajax", "true")
                    .timeout(30000);

                // 【新增】附帶獲取到的 cookie
                if (CollUtil.isNotEmpty(cookies)) {
                    postRequest.cookie(cookies);
                }

                String htmlResponse = postRequest.execute().body();

                if (StrUtil.isBlank(htmlResponse) || htmlResponse.contains("沒有比賽記錄")) {
                    log.warn(" -> 日期 [{}] 未獲取到任何比賽數據，跳過。", dateStr);
                    continue;
                }

                // 【核心修正】為 HTML 片段包裹 table 標籤，確保 Jsoup 能正確解析
                String wrappedHtml = "<table><tbody>" + htmlResponse + "</tbody></table>";
                Document doc = Jsoup.parse(wrappedHtml);
                Elements rows = doc.select("tr.matchTr");
//                log.info(" -> 日期 [{}] 成功獲取 {} 條比賽數據行，開始處理...", dateStr, rows.size());

                for (Element row : rows) {
                    try {
                        JSONObject matchJson = parseRowToJson(row);
                        if (matchJson != null) {
                            if(matchJson.containsKey("StatusID") && matchJson.getInt("StatusID") > 3){
                                log.info("==================== 比赛状态为："+matchJson.getInt("StatusID")+"，"+matchJson.getStr("HNameS")+" vs "+matchJson.getStr("ANameS")+",不允许更新比分 ====================");
                                continue;
                            }
                            processSingleMatch(matchJson);
                        }
                    } catch (Exception e) {
                        log.error("處理單場比賽數據時出錯, HTML row: {}", row.html(), e);
                    }
                }
            } catch (Exception e) {
                log.error("採集日期 [{}] 的數據時發生嚴重異常", dateStr, e);
            }
        }
//        log.info("===== zgzcw.com 多日期比賽數據採集與更新任務執行完畢 =====");
    }

    /**
     * 處理從HTML行解析出的單場比賽JSON數據
     */
    @Transactional(rollbackFor = Exception.class)
    public void processSingleMatch(JSONObject matchJson) {
        Long matchId = matchJson.getLong("MId");
        if (matchId == null) {
            log.warn("跳過一條沒有 matchId 的記錄。");
            return;
        }

        saveOrUpdateLeague(matchJson);
        saveOrUpdateTeam(matchJson, true); // 主隊
        saveOrUpdateTeam(matchJson, false); // 客隊

        BizMatchesBo matchBo = parseMatchInfo(matchJson);
        bizMatchesService.insertOrUpdate(matchBo);

        // 檢查比賽是否完賽並需要結算
        if ("完".equals(matchJson.getStr("Status"))) {
            BizMatchesVo currentMatchInDb = bizMatchesService.queryById(matchId);
            if (currentMatchInDb != null && !"Payout".equals(currentMatchInDb.getStatus())) {
//                log.info("檢測到比賽ID: {} 已完賽，開始進行結算...", matchId);
                currentMatchInDb.setFullScore(matchBo.getFullScore());
                finalizeMatch(currentMatchInDb);
            }
        }
    }


    /**
     * 【核心修正】解析 HTML 的 <tr> 行為一個 JSONObject，修正了多個選擇器並增強了健壯性
     */
    private JSONObject parseRowToJson(Element row) {
        Elements tds = row.select("td");
        if (tds.size() < 13) {
            log.warn("跳過一個不完整的行，列數: {}, HTML: {}", tds.size(), row.html());
            return null;
        }

        JSONObject matchJson = new JSONObject();

        try {
            // 比賽唯一ID
            matchJson.set("MId", Long.parseLong(row.attr("matchid")));
            matchJson.set("MatchNum", tds.get(0).text().trim());

            // 聯賽信息
            Element leagueCell = tds.get(1);
            matchJson.set("LNameS", leagueCell.text().trim());
            matchJson.set("LName", leagueCell.text().trim());
            Element leagueA = leagueCell.selectFirst("a");
            if (leagueA != null) {
                matchJson.set("LId", extractIdFromHref(leagueA.attr("href")));
            }

            // 時間和狀態
            Element dateCell = tds.get(3);
            Element statusCell = tds.get(4);
            matchJson.set("MatchTime", DateUtil.parse(dateCell.attr("date")).getTime() / 1000);
            matchJson.set("Status", statusCell.text().trim());
            matchJson.set("StatusID", dateCell.attr("status"));

            // 主隊信息
            Element homeCell = tds.get(5);
            Element homeA = homeCell.selectFirst("a");
            if (homeA != null) {
                String homeName = homeA.text().trim();
                Element homeRankEl = homeCell.selectFirst("em.paim");
                String homeRank = (homeRankEl != null) ? homeRankEl.text().replace("[", "").replace("]", "").trim() : "";
                matchJson.set("HNameS", homeName);
                matchJson.set("HName", homeName);
                matchJson.set("HOrder", homeRank);
                matchJson.set("HId", extractIdFromHref(homeA.attr("href")));
            }

            // 比分
            String fullScoreText = tds.get(6).text().trim();
            if ("-".equals(fullScoreText) || StrUtil.isBlank(fullScoreText)) {
                matchJson.set("FullScore", "0:0");
            } else {
                matchJson.set("FullScore", fullScoreText.replace(" ", "").replace("-", ":"));
            }

            // 客隊信息
            Element awayCell = tds.get(7);
            Element awayA = awayCell.selectFirst("a");
            if (awayA != null) {
                String awayName = awayA.text().trim();
                Element awayRankEl = awayCell.selectFirst("em.paim");
                String awayRank = (awayRankEl != null) ? awayRankEl.text().replace("[", "").replace("]", "").trim() : "";
                matchJson.set("ANameS", awayName);
                matchJson.set("AName", awayName);
                matchJson.set("AOrder", awayRank);
                matchJson.set("AId", extractIdFromHref(awayA.attr("href")));
            }

            // 半場比分
            String halfScoreText = tds.get(8).text().trim();
            if ("-".equals(halfScoreText) || StrUtil.isBlank(halfScoreText)) {
                matchJson.set("HalfScore", "0:0");
            } else {
                matchJson.set("HalfScore", halfScoreText.replace(" ", "").replace("-", ":"));
            }
            return matchJson;
        } catch (Exception e) {
            log.error("解析比賽行時發生錯誤, HTML: {}", row.html(), e);
            return null; // 返回null以防止處理不完整或錯誤的數據
        }
    }


    private String extractIdFromHref(String href) {
        if (StrUtil.isBlank(href)) return null;
        String[] parts = href.split("/");
        // 從後往前找第一個純數字的路徑段
        for (int i = parts.length - 1; i >= 0; i--) {
            if (StrUtil.isNumeric(parts[i])) {
                return parts[i];
            }
        }
        return null;
    }

    private BizMatchesBo parseMatchInfo(JSONObject matchJson) {
        BizMatchesBo matchBo = new BizMatchesBo();
        matchBo.setMatchId(matchJson.getLong("MId"));

        // === 繁体转换开始 ===
        String homeTeamName = ZhConverterUtil.toTraditional(matchJson.getStr("HNameS"));
        String awayTeamName = ZhConverterUtil.toTraditional(matchJson.getStr("ANameS"));
        matchBo.setMatchName(homeTeamName + " vs " + awayTeamName);
        matchBo.setHomeTeamName(homeTeamName);
        matchBo.setAwayTeamName(awayTeamName);
        // === 繁体转换结束 ===
        matchBo.setMatchNumStr(ZhConverterUtil.toTraditional(matchJson.getStr("MatchNum")));
        if (matchJson.containsKey("MatchTime")) {
            Date matchDateTime = DateUtil.date(matchJson.getLong("MatchTime") * 1000);
            matchBo.setMatchDatetime(matchDateTime);
            matchBo.setBusinessDate(matchDateTime);
        }
        matchBo.setLeagueId(matchJson.getStr("LId"));
        matchBo.setLeagueName(ZhConverterUtil.toTraditional(matchJson.getStr("LNameS")));
        matchBo.setHomeTeamId(matchJson.getLong("HId"));
        matchBo.setAwayTeamId(matchJson.getLong("AId"));
        matchBo.setFullScore(matchJson.getStr("FullScore", "0:0"));
        matchBo.setHalfScore(matchJson.getStr("HalfScore", "0:0"));
        matchBo.setStatus("NotStarted");
        matchBo.setMatchStatus(matchJson.getStr("StatusID"));
        matchBo.setMatchMinute(matchJson.getStr("Status"));
        matchBo.setMatchPhaseTcName(matchJson.getStr("Status"));

        return matchBo;
    }


    private void saveOrUpdateLeague(JSONObject matchJson) {
        if (StrUtil.isBlank(matchJson.getStr("LId"))) return;
        BizLeaguesBo leagueBo = new BizLeaguesBo();
        leagueBo.setLeagueId(matchJson.getStr("LId"));
        // === 繁体转换 ===
        leagueBo.setName(ZhConverterUtil.toTraditional(matchJson.getStr("LName")));
        leagueBo.setAbbrName(ZhConverterUtil.toTraditional(matchJson.getStr("LNameS")));
        // === 繁体转换 ===
        bizLeaguesService.saveOrUpdate(leagueBo);
    }

    private void saveOrUpdateTeam(JSONObject matchJson, boolean isHome) {
        String idKey = isHome ? "HId" : "AId";
        String nameKey = isHome ? "HName" : "AName";
        String shortNameKey = isHome ? "HNameS" : "ANameS";
        String rankKey = isHome ? "HOrder" : "AOrder";

        Long teamId = matchJson.getLong(idKey);
        if (teamId == null) return;

        BizTeamsBo teamBo = new BizTeamsBo();
        teamBo.setTeamId(teamId);
        // === 繁体转换 ===
        teamBo.setFullName(ZhConverterUtil.toTraditional(matchJson.getStr(nameKey)));
        teamBo.setAbbrName(ZhConverterUtil.toTraditional(matchJson.getStr(shortNameKey)));
        // === 繁体转换 ===
        teamBo.setRanks(matchJson.getStr(rankKey));
        bizTeamsService.saveOrUpdate(teamBo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void finalizeMatch(BizMatchesVo matchToFinalize) {
        Long matchId = matchToFinalize.getMatchId();

        if (StrUtil.isBlank(matchToFinalize.getFullScore()) || !matchToFinalize.getFullScore().contains(":")) {
            log.warn("比賽ID: {} 的最終比分格式不正確 ('{}')，無法結算。", matchId, matchToFinalize.getFullScore());
            throw new ServiceException("比分格式錯誤，無法結算");
        }

        Map<Long, String> goalLineMap = bizOddsService.queryGoalLinesByMatchIds(List.of(matchId), "HHAD");
        List<BizMatchResultsBo> results = MatchResultCalculator.calculateAllResults(matchToFinalize, goalLineMap.get(matchId));

        if (CollUtil.isNotEmpty(results)) {
            bizMatchResultsService.deleteByMatchId(matchId);
            bizMatchResultsService.insertBatchByBo(results);

            BizMatchesBo statusUpdateBo = new BizMatchesBo();
            statusUpdateBo.setMatchId(matchId);
            statusUpdateBo.setStatus("Payout");
            statusUpdateBo.setMatchStatus("-1");
            statusUpdateBo.setFullScore(matchToFinalize.getFullScore());
            bizMatchesService.updateByBo(statusUpdateBo);

//            log.info("比賽ID: {} 內部賽果生成並更新狀態為Payout成功！", matchId);
        } else {
            log.warn("未能為比賽ID: {} 生成任何賽果，結算中止。", matchId);
        }
    }
}
