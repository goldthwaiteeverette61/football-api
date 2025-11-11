package org.dromara.biz.parser;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizMatchResults;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.bo.BizOddsBo;
import org.dromara.biz.domain.vo.BizOddsVo;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.biz.utils.PoolCodeUtils;
import org.dromara.common.core.utils.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: 核心服务类，负责从 zgzcw.com 获取 HTML 页面，
 * 协调不同的解析处理器来提取各种玩法的比赛数据，并将其存入数据库。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ZgzcwDataProcessor {

    // --- 【新增】注入核心服務 ---
    private final IBizMatchesService bizMatchesService;
    private final IBizOddsService bizOddsService;

    // 【新增】定義比分分類，用於在儲存時進行分組
    private static final Set<String> CRS_WIN_SCORES = Set.of("1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0", "4:1", "4:2", "5:0", "5:1", "5:2", "胜其他");
    private static final Set<String> CRS_DRAW_SCORES = Set.of("0:0", "1:1", "2:2", "3:3", "平其他");
    private static final Set<String> CRS_LOSE_SCORES = Set.of("0:1", "0:2", "1:2", "0:3", "1:3", "2:3", "0:4", "1:4", "2:4", "0:5", "1:5", "2:5", "负其他");


    static {
        try {
            System.setProperty("https.protocols", "TLSv1.2");
        } catch (Exception e) {
            log.error("设置系统HTTPS协议失败", e);
        }
    }

    private static final Map<String, String> URL_MAP = Map.of(
        "spf", "https://cp.zgzcw.com/lottery/jchtplayvsForJsp.action?lotteryId=47&type=jcmini",
        "crs", "https://cp.zgzcw.com/lottery/jcplayvsForJsp.action?lotteryId=23",
        "ttg", "https://cp.zgzcw.com/lottery/jcplayvsForJsp.action?lotteryId=24",
        "hafu", "https://cp.zgzcw.com/lottery/jcplayvsForJsp.action?lotteryId=25"
    );

    // 將 handlerMap 設為靜態，因為它在所有實例中都是一樣的
    private static final Map<String, IMatchDataHandler> HANDLER_MAP = Map.of(
        "spf", new SpfDataHandler(),
        "crs", new CrsDataHandler(),
        "ttg", new TtgDataHandler(),
        "hafu", new HafuDataHandler()
    );

    /**
     * 【已重構】主流程方法：採集、解析所有玩法頁面，並統一儲存數據
     */
    public void processAllPoolsAndSave() {
        Map<String, MatchData> consolidatedMatches = new HashMap<>();
        try {
            String domain = "https://cp.zgzcw.com/";
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.put("Referer", "https://www.zgzcw.com/");

            // 獲取初始cookies
            Connection.Response initialResponse = Jsoup.connect(domain).headers(headers).method(Connection.Method.GET).execute();
            Map<String, String> cookies = initialResponse.cookies();

            // 遍歷所有玩法URL
            for (Map.Entry<String, String> entry : URL_MAP.entrySet()) {
                String poolCode = entry.getKey();
                String url = entry.getValue();
                IMatchDataHandler handler = HANDLER_MAP.get(poolCode);

                if (handler == null) continue;

                Document doc = Jsoup.connect(url).headers(headers).cookies(cookies).get();
                Elements rows = doc.select("#hide_box_1 tr.beginBet[id][mN][t], #hide_box_2 tr.beginBet[id][mN][t]");

                for (Element row : rows) {
                    try {
                        String matchId = row.attr("id").replace("tr_", "");
                        // 如果是第一次遇到該比賽，則解析基本資訊並創建物件；否則直接獲取
                        MatchData matchData = consolidatedMatches.computeIfAbsent(matchId, id -> parseBaseInfo(row));

                        if (matchData != null) {
                            // 使用對應的處理器解析賠率
                            handler.parse(row, matchData);
                        }
                    } catch (Exception e) {
                        log.error("处理单场比赛时出错, row HTML: {}", row.html(), e);
                    }
                }
            }

            // 統一儲存所有彙總後的比賽資料
            for (MatchData matchData : consolidatedMatches.values()) {
                saveMatchData(matchData);
            }

        } catch (IOException e) {
            log.error("采集或解析数据时发生严重错误", e);
        }
    }

    /**
     * 核心儲存方法，將單場比賽的所有資料寫入資料庫
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMatchData(MatchData data) {
        if (data.getNewplayId() == null || data.getNewplayId().isEmpty()) {
            log.warn("跳過一筆沒有 newplayId 的比賽記錄, sportteryMatchId: {}", data.getMatchId());
            return;
        }

        // 1. 更新比賽基本資訊 (關聯 sportteryMatchId)
        saveBaseMatchInfo(data);

        // 2. 根據不同玩法，儲存對應的賠率 (使用 newplayId 作為主鍵)
        if (data.getSpfOdds() != null) {
            saveSpfOdds(data.getNewplayId(), data.getMatchId(), data.getSpfOdds());
        }
        if (data.getTtgOdds() != null) {
            String s = PoolCodeUtils.formatTTG(JSONUtil.toJsonStr(data.getTtgOdds()));
            saveComplexOdds(data.getNewplayId(), data.getMatchId(), BizMatchResults.POOL_CODE_TTG,s);
        }

        if (data.getCrsOdds() != null && data.getCrsOdds().getScores() != null && !data.getCrsOdds().getScores().isEmpty()) {
            // 【核心修改】在此處臨時對比分數據進行分組
            Map<String, Map<String, BigDecimal>> groupedCrsOdds = new HashMap<>();
            Map<String, BigDecimal> hScores = new HashMap<>();
            Map<String, BigDecimal> dScores = new HashMap<>();
            Map<String, BigDecimal> aScores = new HashMap<>();

            // 遍歷原始比分數據並進行分組
            for (Map.Entry<String, BigDecimal> entry : data.getCrsOdds().getScores().entrySet()) {
                String scoreKey = entry.getKey();
                BigDecimal oddValue = entry.getValue();
                if (CRS_WIN_SCORES.contains(scoreKey)) {
                    hScores.put(scoreKey, oddValue);
                } else if (CRS_DRAW_SCORES.contains(scoreKey)) {
                    dScores.put(scoreKey, oddValue);
                } else if (CRS_LOSE_SCORES.contains(scoreKey)) {
                    aScores.put(scoreKey, oddValue);
                }
            }

            groupedCrsOdds.put("H", hScores);
            groupedCrsOdds.put("D", dScores);
            groupedCrsOdds.put("A", aScores);

            String s = PoolCodeUtils.formatCRS(JSONUtil.toJsonStr(groupedCrsOdds));
            saveComplexOdds(data.getNewplayId(), data.getMatchId(), BizMatchResults.POOL_CODE_CRS,s);
        }

        if (data.getHafuOdds() != null) {
            String s = PoolCodeUtils.formatHAFU(JSONUtil.toJsonStr(data.getHafuOdds()));
            saveComplexOdds(data.getNewplayId(), data.getMatchId(), BizMatchResults.POOL_CODE_HAFU,s);
        }
    }

    /**
     * 【已重構】更新比賽資訊，將 sportteryMatchId 關聯到已存在的比賽記錄
     */
    private void saveBaseMatchInfo(MatchData data) {
        BizMatchesBo matchBo = new BizMatchesBo();
        try {
            // 使用 newplayId 作為 biz_matches 表的主鍵 matchId
            matchBo.setMatchId(Long.parseLong(data.getNewplayId()));
            // 將採集到的 matchId 作為 sportteryMatchId 進行更新
            matchBo.setSportteryMatchId(Long.parseLong(data.getMatchId()));
            bizMatchesService.updateByBo(matchBo);
        } catch (Exception e) {
            log.error("更新比賽 sportteryMatchId 失敗, matchId: {}, sportteryMatchId: {}", data.getNewplayId(), data.getMatchId(), e);
        }
    }

    private void saveSpfOdds(String matchId, String sportteryMatchId, SpfOdds odds) {
        Long mId = Long.parseLong(matchId);
        Long sMId = Long.parseLong(sportteryMatchId);

        // 只保存有赔率的数据
        if (odds.getHomeWin() != null && odds.getDraw() != null && odds.getAwayWin() != null) {
            BizOddsBo hadBo = new BizOddsBo();
            hadBo.setMatchId(mId);
            hadBo.setSportteryMatchId(sMId);
            hadBo.setPoolCode("HAD");
            hadBo.setHomeOdds(odds.getHomeWin());
            hadBo.setDrawOdds(odds.getDraw());
            hadBo.setAwayOdds(odds.getAwayWin());
            saveOrUpdateOdds(hadBo);
        }

        if (odds.getLetHomeWin() != null && odds.getLetDraw() != null && odds.getLetAwayWin() != null) {
            BizOddsBo hhadBo = new BizOddsBo();
            hhadBo.setMatchId(mId);
            hhadBo.setSportteryMatchId(sMId);
            hhadBo.setPoolCode("HHAD");
            hhadBo.setGoalLine(odds.getGoalLine());
            hhadBo.setHomeOdds(odds.getLetHomeWin());
            hhadBo.setDrawOdds(odds.getLetDraw());
            hhadBo.setAwayOdds(odds.getLetAwayWin());
            saveOrUpdateOdds(hhadBo);
        }
    }

    private void saveComplexOdds(String matchId, String sportteryMatchId, String poolCode, String oddsData) {
        Long mId = Long.parseLong(matchId);
        Long sMId = Long.parseLong(sportteryMatchId);
        BizOddsBo oddsBo = new BizOddsBo();
        oddsBo.setMatchId(mId);
        oddsBo.setSportteryMatchId(sMId);
        oddsBo.setPoolCode(poolCode);
        oddsBo.setOddsData(oddsData);
        saveOrUpdateOdds(oddsBo);
    }

    private void saveOrUpdateOdds(BizOddsBo oddsBo) {
        try {
            BizOddsBo queryBo = new BizOddsBo();
            queryBo.setMatchId(oddsBo.getMatchId());
            queryBo.setPoolCode(oddsBo.getPoolCode());

            List<BizOddsVo> existingOddsList = bizOddsService.queryList(queryBo);
            BizOddsVo existingOdds = existingOddsList.isEmpty() ? null : existingOddsList.get(0);

            if (existingOdds != null) {
                oddsBo.setId(existingOdds.getId());
            }

            bizOddsService.saveOrUpdate(oddsBo);
        } catch (Exception e) {
            log.error("儲存賠率失敗, matchId: {}, poolCode: {}", oddsBo.getMatchId(), oddsBo.getPoolCode(), e);
        }
    }


    private MatchData parseBaseInfo(Element row) {
        try {
            MatchData matchData = new MatchData();
            // 這是 sportteryMatchId
            matchData.setMatchId(row.attr("id").replace("tr_", ""));

            // 【已修正】從 class 為 "wh-8" 的 <td> 元素中獲取 newplayid
            Element newPlayIdElement = row.selectFirst("td.wh-8");
            if (newPlayIdElement != null) {
                matchData.setNewplayId(newPlayIdElement.attr("newplayid"));
            }

            if(StringUtils.isBlank(matchData.getNewplayId())){
                newPlayIdElement = row.selectFirst("td.wh-10");
                if (newPlayIdElement != null) {
                    matchData.setNewplayId(newPlayIdElement.attr("newplayid"));
                }
                log.info("newplayerId 在wh-10扫描");
            }

            matchData.setMatchNum(row.attr("mN"));
            matchData.setLeagueName(row.attr("m"));
            matchData.setMatchTime(row.attr("t"));
            Element homeTeamElement = row.selectFirst("td.wh-4 a");
            Element awayTeamElement = row.selectFirst("td.wh-6 a");

            if (homeTeamElement != null) {
                matchData.setHomeTeam(homeTeamElement.text());
            }
            if (awayTeamElement != null) {
                matchData.setAwayTeam(awayTeamElement.text());
            }
            return matchData;
        } catch (Exception e) {
            log.warn("解析基础比赛信息失败, row HTML: {}", row.html(), e);
            return null;
        }
    }

    private static BigDecimal safeParseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    // --- 数据模型 ---
    @Data
    public static class MatchData {
        // 這是 sportteryMatchId
        private String matchId;
        private String matchNum;
        private String leagueName;
        private String matchTime;
        private String homeTeam;
        private String awayTeam;
        // 這是資料庫主鍵 matchId
        private String newplayId;
        private SpfOdds spfOdds;
        private TtgOdds ttgOdds;
        private CrsOdds crsOdds;
        private HafuOdds hafuOdds;
    }

    @Data
    public static class SpfOdds {
        private BigDecimal homeWin, draw, awayWin;
        private String goalLine;
        private BigDecimal letHomeWin, letDraw, letAwayWin;
    }

    @Data
    public static class TtgOdds {
        private BigDecimal goals0, goals1, goals2, goals3, goals4, goals5, goals6, goals7plus;
    }

    @Data
    public static class CrsOdds {
        private Map<String, BigDecimal> scores = new HashMap<>();

        private Map<String, BigDecimal> H = new HashMap<>();
        private Map<String, BigDecimal> D = new HashMap<>();
        private Map<String, BigDecimal> A = new HashMap<>();
    }

    @Data
    public static class HafuOdds {
        private BigDecimal ss, sp, sf, ps, pp, pf, fs, fp, ff;
    }

    // --- 【已修正】处理器接口与实现 (設為靜態內部類) ---
    public interface IMatchDataHandler {
        void parse(Element row, MatchData matchData);
    }

    public static class SpfDataHandler implements IMatchDataHandler {
        @Override
        public void parse(Element row, MatchData matchData) {
            SpfOdds odds = new SpfOdds();
            try {
                // 1. 处理非让球 (frq)
                Element frq = row.selectFirst("div.frq");
                if (frq != null) {
                    Elements frqOdds = frq.select("a");
                    if (frqOdds.size() >= 3) {
                        odds.setHomeWin(safeParseBigDecimal(frqOdds.get(0).text()));
                        odds.setDraw(safeParseBigDecimal(frqOdds.get(1).text()));
                        odds.setAwayWin(safeParseBigDecimal(frqOdds.get(2).text()));
                    } else {
                        log.warn("非让球赔率数量不足3, matchId: {}", matchData.getMatchId());
                    }
                }

                // 2. 处理让球 (rqq)
                Element rqq = row.selectFirst("div.rqq");
                if (rqq != null) {
                    Elements rqqOdds = rqq.select("a");
                    if (rqqOdds.size() >= 3) {
                        odds.setGoalLine(rqq.selectFirst("em.rq").text());
                        odds.setLetHomeWin(safeParseBigDecimal(rqqOdds.get(0).text()));
                        odds.setLetDraw(safeParseBigDecimal(rqqOdds.get(1).text()));
                        odds.setLetAwayWin(safeParseBigDecimal(rqqOdds.get(2).text()));
                    } else {
                        log.warn("让球赔率数量不足3, matchId: {}", matchData.getMatchId());
                    }
                }

                // 只要解析到了任何赔率数据，就将其设置到 matchData 中
                matchData.setSpfOdds(odds);

            } catch (Exception e) {
                log.warn("解析胜平负赔率时发生意外错误, matchId: {}", matchData.getMatchId(), e);
            }
        }
    }

    public static class CrsDataHandler implements IMatchDataHandler {
        @Override
        public void parse(Element row, MatchData matchData) {
            CrsOdds crsOdds = new CrsOdds();
            try {
                Element oddsInput = row.selectFirst("input[id^=ht_]");
                if (oddsInput != null) {
                    String oddsString = oddsInput.val();
                    String[] oddsArray = oddsString.split("\\s+");
                    if (oddsArray.length >= 31) {
                        String[] scoreKeys = {"1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0", "4:1", "4:2", "5:0", "5:1", "5:2", "胜其他", "0:0", "1:1", "2:2", "3:3", "平其他", "0:1", "0:2", "1:2", "0:3", "1:3", "2:3", "0:4", "1:4", "2:4", "0:5", "1:5", "2:5", "负其他"};
                        for (int i = 0; i < scoreKeys.length; i++) {
                            BigDecimal oddValue = safeParseBigDecimal(oddsArray[i]);
                            if (oddValue != null) {
                                crsOdds.getScores().put(scoreKeys[i], oddValue);
                            }
                        }
                    }
                } else {
                    Element crsArea = row.selectFirst("td.bf-box div.bf-hh-in table");
                    if (crsArea != null) {
                        for (Element cell : crsArea.select("td")) {
                            String[] parts = cell.text().split("\\s+");
                            if (parts.length == 2) {
                                BigDecimal oddValue = safeParseBigDecimal(parts[1]);
                                if (oddValue != null) {
                                    crsOdds.getScores().put(parts[0], oddValue);
                                }
                            }
                        }
                    }
                }
                if (!crsOdds.getScores().isEmpty()) {
                    matchData.setCrsOdds(crsOdds);
                }
            } catch (Exception e) {
                log.warn("解析比分赔率时发生错误, matchId: {}", matchData.getMatchId(), e);
            }
        }
    }

    public static class TtgDataHandler implements IMatchDataHandler {
        @Override
        public void parse(Element row, MatchData matchData) {
            Elements oddsElements = row.select("td.wh-7 div.zjq-area a");
            if (oddsElements.size() == 8) {
                try {
                    TtgOdds odds = new TtgOdds();
                    odds.setGoals0(safeParseBigDecimal(oddsElements.get(0).text()));
                    odds.setGoals1(safeParseBigDecimal(oddsElements.get(1).text()));
                    odds.setGoals2(safeParseBigDecimal(oddsElements.get(2).text()));
                    odds.setGoals3(safeParseBigDecimal(oddsElements.get(3).text()));
                    odds.setGoals4(safeParseBigDecimal(oddsElements.get(4).text()));
                    odds.setGoals5(safeParseBigDecimal(oddsElements.get(5).text()));
                    odds.setGoals6(safeParseBigDecimal(oddsElements.get(6).text()));
                    odds.setGoals7plus(safeParseBigDecimal(oddsElements.get(7).text()));
                    matchData.setTtgOdds(odds);
                } catch (Exception e) {
                    log.warn("解析总进球赔率时发生错误, matchId: {}", matchData.getMatchId());
                }
            }
        }
    }

    public static class HafuDataHandler implements IMatchDataHandler {
        @Override
        public void parse(Element row, MatchData matchData) {
            Elements oddsElements = row.select("td.wh-7 div.bqc-area a");
            if (oddsElements.size() == 9) {
                try {
                    HafuOdds odds = new HafuOdds();
                    odds.setSs(safeParseBigDecimal(oddsElements.get(0).text()));
                    odds.setSp(safeParseBigDecimal(oddsElements.get(1).text()));
                    odds.setSf(safeParseBigDecimal(oddsElements.get(2).text()));
                    odds.setPs(safeParseBigDecimal(oddsElements.get(3).text()));
                    odds.setPp(safeParseBigDecimal(oddsElements.get(4).text()));
                    odds.setPf(safeParseBigDecimal(oddsElements.get(5).text()));
                    odds.setFs(safeParseBigDecimal(oddsElements.get(6).text()));
                    odds.setFp(safeParseBigDecimal(oddsElements.get(7).text()));
                    odds.setFf(safeParseBigDecimal(oddsElements.get(8).text()));
                    matchData.setHafuOdds(odds);
                } catch (Exception e) {
                    log.warn("解析半全场赔率时发生错误, matchId: {}", matchData.getMatchId());
                }
            }
        }
    }
}

