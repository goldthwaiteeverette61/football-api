package org.dromara.test;

import jakarta.annotation.Resource;
import org.dromara.biz.service.impl.MatchDataCollection500ServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 500.com 數據採集服務整合測試
 *
 * @description: 該測試類旨在驗證 MatchDataCollection500ServiceImpl 的核心功能，
 * 包括從真實API中採集數據並儲存。
 */
@Transactional
class MatchDataCollection500ServiceIntegrationTest extends BaseIntegrationTest {

    @Resource
    private MatchDataCollection500ServiceImpl matchDataCollection500Service;

    @Test
    @DisplayName("【場景一】應能成功從遠端API採集比賽數據並存入資料庫")
    @Sql(scripts = "/sql/500-collection-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_CollectMatches_FromLiveApi() {
        // --- 1. Arrange (安排) ---
        // @Sql 註解已確保執行前資料庫是乾淨的。

        // --- 2. Act (執行) ---
        // 執行實際的採集方法
        assertDoesNotThrow(() -> matchDataCollection500Service.collectAndProcessMatches());

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

}

