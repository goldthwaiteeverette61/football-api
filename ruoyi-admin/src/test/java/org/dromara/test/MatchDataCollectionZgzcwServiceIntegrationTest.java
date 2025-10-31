package org.dromara.test;

import jakarta.annotation.Resource;
import org.dromara.biz.service.impl.MatchDataCollectionZgzcwServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.biz.domain.vo.BizMatchesVo;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * zgzcw.com 數據採集服務整合測試
 *
 * @description: 該測試類旨在驗證 MatchDataCollectionZgzcwServiceImpl 的核心功能，
 * 包括從真實API中採集數據並儲存，以及在比賽結束後觸發結算。
 */
@Transactional
class MatchDataCollectionZgzcwServiceIntegrationTest extends BaseIntegrationTest {

    @Resource
    private MatchDataCollectionZgzcwServiceImpl matchDataCollectionZgzcwService;

    @Test
    @DisplayName("【場景一】應能成功從遠端API採集比賽數據並存入資料庫")
    @Sql(scripts = "/sql/zgzcw-collection-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_CollectUpcomingMatches_FromLiveApi() {
        // --- 1. Arrange (安排) ---
        // @Sql 註解已確保執行前資料庫是乾淨的。

        // --- 2. Act (執行) ---
        // 執行實際的採集方法
        assertDoesNotThrow(() -> matchDataCollectionZgzcwService.collectAndProcessMatches(-1,2));

        // --- 3. Assert (断言) ---
        // 由於遠端 API 的數據是動態變化的，我們驗證資料庫中是否成功寫入了數據，
        // 而不是檢查具體的數值。
        long matchCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_matches", Long.class);
        long leagueCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_leagues", Long.class);
        long teamCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_teams", Long.class);
        long oddsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_odds", Long.class);

        assertTrue(matchCount > 0, "應至少採集到一場比賽");
        assertTrue(leagueCount > 0, "應至少採集到一個聯賽");
        assertTrue(teamCount > 0, "應至少採集到一支球隊");
        assertTrue(oddsCount > 0, "應至少採集到一筆賠率");
    }

    @Test
    @DisplayName("【場景二】應能處理已完賽的比賽數據並觸發結算")
    @Sql(scripts = "/sql/zgzcw-collection-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_FinalizeMatch_WithFinishedMatchData() {
        // --- 1. Arrange (安排) ---
        // 預先在資料庫中插入一場“未開始”的比賽及其賠率，以模擬結算場景
        jdbcTemplate.update("INSERT INTO biz_matches (match_id, status, match_status) VALUES (300002, 'NotStarted', '0')");
        jdbcTemplate.update("INSERT INTO biz_odds (match_id, pool_code, goal_line) VALUES (300002, 'HHAD', '+1')");

        // 模擬一個已完賽的比賽VO物件
        BizMatchesVo finishedMatchVo = new BizMatchesVo();
        finishedMatchVo.setMatchId(300002L);
        finishedMatchVo.setFullScore("1:1");
        finishedMatchVo.setHalfScore("0:1");

        // --- 2. Act (執行) ---
        assertDoesNotThrow(() -> matchDataCollectionZgzcwService.finalizeMatch(finishedMatchVo));

        // --- 3. Assert (断言) ---
        // a. 驗證比賽狀態是否已更新為 "Payout"
        Map<String, Object> match = jdbcTemplate.queryForMap("SELECT status, match_status, full_score FROM biz_matches WHERE match_id = 300002");
        assertEquals("Payout", match.get("status"), "比賽的內部狀態應更新為 Payout");
        assertEquals("-1", match.get("match_status"), "比賽的源狀態應更新為 -1 (完場)");
        assertEquals("1:1", match.get("full_score"), "全場比分應被更新");

        // b. 驗證是否已生成賽果
        List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT * FROM biz_match_results WHERE match_id = 300002");
        assertTrue(results.size() > 0, "應為完場比賽生成賽果");

        // c. 驗證讓球勝平負賽果是否正確
        Map<String, Object> hhadResult = jdbcTemplate.queryForMap(
            "SELECT combination FROM biz_match_results WHERE match_id = 300002 AND pool_code = 'HHAD'"
        );
        // 主隊1:1，受讓+1球後變為2:1，結果為主勝
        assertEquals("H", hhadResult.get("combination"), "根據比分1:1和讓球+1，讓球勝平負賽果應為'H'(主勝)");
    }
}

