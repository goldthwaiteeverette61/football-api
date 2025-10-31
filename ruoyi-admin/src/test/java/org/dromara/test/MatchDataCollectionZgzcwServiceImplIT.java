package org.dromara.test;

import cn.hutool.json.JSONObject;
import org.dromara.biz.domain.vo.BizLeaguesVo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.domain.vo.BizTeamsVo;
import org.dromara.biz.service.IBizLeaguesService;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.biz.service.IBizTeamsService;
import org.dromara.biz.service.impl.MatchDataCollectionZgzcwServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 主採集服務整合測試
 *
 * @description: 該測試類旨在驗證 MatchDataCollectionZgzcwServiceImpl 的核心資料處理與儲存邏輯。
 */
@DisplayName("主採集服務整合測試 (MatchDataCollectionZgzcwServiceImpl)")
public class MatchDataCollectionZgzcwServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private MatchDataCollectionZgzcwServiceImpl matchDataCollectionService;

    // --- 【新增】注入 Service 用於斷言 ---
    @Autowired
    private IBizMatchesService bizMatchesService;
    @Autowired
    private IBizLeaguesService bizLeaguesService;
    @Autowired
    private IBizTeamsService bizTeamsService;
    @Autowired
    private IBizOddsService bizOddsService;


    /**
     * 在每個測試前清理相關資料表，確保測試環境的獨立性
     */
    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM biz_matches");
        jdbcTemplate.update("DELETE FROM biz_odds");
        jdbcTemplate.update("DELETE FROM biz_leagues");
        jdbcTemplate.update("DELETE FROM biz_teams");
    }

    @Test
    @DisplayName("【核心場景】處理單場比賽資料並正確存入資料庫")
    void testProcessSingleMatch_ShouldSaveDataCorrectly() {
        // --- 1. Arrange (安排) ---
        // 創建一個模擬從網頁解析出的 JSON 物件，包含比賽基本資訊
        JSONObject matchJson = new JSONObject();
        matchJson.put("MId", 2034001L);
        matchJson.put("MatchNum", "周二001");
        matchJson.put("LNameS", "日联杯");
        matchJson.put("LName", "日本联赛杯");
        matchJson.put("LId", "123");
        matchJson.put("MatchTime", System.currentTimeMillis() / 1000 + 3600);
        matchJson.put("Status", "未");
        matchJson.put("StatusID", "1");
        matchJson.put("HNameS", "川崎前锋");
        matchJson.put("HName", "川崎前锋");
        matchJson.put("HId", 456L);
        matchJson.put("ANameS", "柏太阳神");
        matchJson.put("AName", "柏太阳神");
        matchJson.put("AId", 789L);
        matchJson.put("FullScore", "0:0");
        matchJson.put("HalfScore", "0:0");
        // 注意：根據重構後的邏輯，此服務不再直接處理賠率，因此 sp 物件可以省略

        // --- 2. Act (執行) ---
        // 直接調用核心處理方法
        matchDataCollectionService.processSingleMatch(matchJson);

        // --- 3. Assert (斷言) ---
        // 【已修改】使用 Service 進行查詢，以確保在同一個交易內
        // 驗證比賽資訊是否正確存入並轉換為繁體
        BizMatchesVo match = bizMatchesService.queryById(2034001L);
        assertNotNull(match, "比賽記錄不應為空");
        assertEquals("川崎前鋒 vs 柏太陽神", match.getMatchName());
        assertEquals("週二001", match.getMatchNumStr());
        assertNotNull(match.getBusinessDate(), "business_date 不應為空");

        // 驗證隊伍和聯賽資訊是否正確存入並轉換為繁體
        BizLeaguesVo league = bizLeaguesService.queryById("123");
        assertNotNull(league, "聯賽記錄不應為空");
        assertEquals("日本聯賽盃", league.getName());

        BizTeamsVo homeTeam = bizTeamsService.queryById(456L);
        assertNotNull(homeTeam, "主隊記錄不應為空");
        assertEquals("川崎前鋒", homeTeam.getFullName());

        BizTeamsVo awayTeam = bizTeamsService.queryById(789L);
        assertNotNull(awayTeam, "客隊記錄不應為空");
        assertEquals("柏太陽神", awayTeam.getFullName());


        // 【已修改】驗證此服務不再寫入賠率資料
        Integer oddsCount = jdbcTemplate.queryForObject("SELECT count(*) FROM biz_odds WHERE match_id = 2034001", Integer.class);
        assertEquals(0, oddsCount, "MatchDataCollectionZgzcwServiceImpl 不應再寫入賠率資料");
    }
}

