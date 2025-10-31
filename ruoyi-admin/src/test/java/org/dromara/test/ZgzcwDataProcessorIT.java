package org.dromara.test;

import org.dromara.biz.parser.ZgzcwDataProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 補充賠率採集服務整合測試
 *
 * @description: 該測試類旨在驗證 ZgzcwDataProcessor 的核心資料處理與儲存邏輯。
 */
@DisplayName("補充賠率採集服務整合測試 (ZgzcwDataProcessor)")
public class ZgzcwDataProcessorIT extends BaseIntegrationTest {

    @Autowired
    private ZgzcwDataProcessor zgzcwDataProcessor;

    /**
     * 在每個測試前清理相關資料表
     */
    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM biz_matches");
        jdbcTemplate.update("DELETE FROM biz_odds");
    }

    @Test
    @DisplayName("【核心場景】儲存彙總後的比賽資料並正確存入資料庫")
    void testSaveMatchData_ShouldSaveAllPoolsCorrectly() {
        // --- 1. Arrange (安排) ---
        // 創建一個模擬的 MatchData 物件，包含所有玩法的賠率
        ZgzcwDataProcessor.MatchData matchData = new ZgzcwDataProcessor.MatchData();
        matchData.setMatchId("2034002");
        matchData.setMatchNum("周二002");
        matchData.setLeagueName("日联杯");
        matchData.setMatchTime("2025-10-08 18:00");
        matchData.setHomeTeam("横滨FC");
        matchData.setAwayTeam("广岛三箭");

        // 準備勝平負(SPF)賠率
        ZgzcwDataProcessor.SpfOdds spf = new ZgzcwDataProcessor.SpfOdds();
        spf.setHomeWin(new BigDecimal("5.00"));
        spf.setDraw(new BigDecimal("3.42"));
        spf.setAwayWin(new BigDecimal("1.57"));
        spf.setGoalLine("+1");
        spf.setLetHomeWin(new BigDecimal("2.10"));
        spf.setLetDraw(new BigDecimal("3.20"));
        spf.setLetAwayWin(new BigDecimal("2.95"));
        matchData.setSpfOdds(spf);

        // 準備總進球數(TTG)賠率
        ZgzcwDataProcessor.TtgOdds ttg = new ZgzcwDataProcessor.TtgOdds();
        // 【已修改】使用與序列化後一致的整數值
        ttg.setGoals0(new BigDecimal("9"));
        ttg.setGoals1(new BigDecimal("4.05"));
        ttg.setGoals7plus(new BigDecimal("35"));
        matchData.setTtgOdds(ttg);

        // 準備比分(CRS)賠率
        ZgzcwDataProcessor.CrsOdds crs = new ZgzcwDataProcessor.CrsOdds();
        crs.getScores().put("1:0", new BigDecimal("12"));
        crs.getScores().put("负其他", new BigDecimal("55"));
        matchData.setCrsOdds(crs);

        // 準備半全場(HAFU)賠率
        ZgzcwDataProcessor.HafuOdds hafu = new ZgzcwDataProcessor.HafuOdds();
        hafu.setSs(new BigDecimal("9.40"));
        hafu.setFf(new BigDecimal("2.30"));
        matchData.setHafuOdds(hafu);

        // --- 2. Act (執行) ---
        zgzcwDataProcessor.saveMatchData(matchData);

        // --- 3. Assert (斷言) ---
        // 驗證比賽資訊是否正確存入並轉換為繁體
        Map<String, Object> match = jdbcTemplate.queryForMap("SELECT * FROM biz_matches WHERE match_id = 2034002");
        assertEquals("橫濱FC vs 廣島三箭", match.get("match_name"));
        assertEquals("週二002", match.get("match_num_str"));
        assertNotNull(match.get("business_date"));

        // 驗證簡單玩法 (HAD, HHAD) 是否正確存入獨立欄位
        Map<String, Object> hadOdds = jdbcTemplate.queryForMap("SELECT * FROM biz_odds WHERE match_id = 2034002 AND pool_code = 'HAD'");
        assertEquals(0, new BigDecimal("5.00").compareTo((BigDecimal) hadOdds.get("home_odds")));

        Map<String, Object> hhadOdds = jdbcTemplate.queryForMap("SELECT * FROM biz_odds WHERE match_id = 2034002 AND pool_code = 'HHAD'");
        assertEquals("+1", hhadOdds.get("goal_line"));

        // 驗證複雜玩法 (TTG, CRS, HAFU) 是否正確存入 odds_data 欄位
        Map<String, Object> ttgOdds = jdbcTemplate.queryForMap("SELECT * FROM biz_odds WHERE match_id = 2034002 AND pool_code = 'TTG'");
        assertNotNull(ttgOdds.get("odds_data"));
        // 【已修改】斷言檢查序列化後的整數值
        assertTrue(((String) ttgOdds.get("odds_data")).contains("\"goals0\":9"));
        assertTrue(((String) ttgOdds.get("odds_data")).contains("\"goals7plus\":35"));
        assertTrue(((String) ttgOdds.get("odds_data")).contains("\"goals1\":4.05")); // 帶小數的保持不變

        Map<String, Object> crsOdds = jdbcTemplate.queryForMap("SELECT * FROM biz_odds WHERE match_id = 2034002 AND pool_code = 'CRS'");
        assertNotNull(crsOdds.get("odds_data"));
        assertTrue(((String) crsOdds.get("odds_data")).contains("\"1:0\":12"));

        Map<String, Object> hafuOdds = jdbcTemplate.queryForMap("SELECT * FROM biz_odds WHERE match_id = 2034002 AND pool_code = 'HAFU'");
        assertNotNull(hafuOdds.get("odds_data"));
        assertTrue(((String) hafuOdds.get("odds_data")).contains("\"ss\":9.4")); // 序列化後會去掉末尾的0
    }
}
