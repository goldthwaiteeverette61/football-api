package org.dromara.test;

import org.dromara.biz.service.IBizUserFollowsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 投注校驗功能整合測試
 *
 * @description: 該測試類旨在驗證 IBizUserFollowsService.getMinimumBetAmount 方法的核心業務邏輯。
 */
public class BettingValidationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private IBizUserFollowsService bizUserFollowsService;

    /**
     * 場景一: 新輪次開始 - 驗證用戶首次投注或上一輪獲勝後的最小投注金額
     */
    @Test
    @DisplayName("【場景一】獲取最小投注額：新輪次（首次投注）")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_MinimumBetAmount_IsBaseAmount_ForNewRound() {
        // -> 初始狀態: 用戶(2)的 progress.last_bet_amount 為 0, 代表一個新輪次的開始。
        //    系統基礎投注額 (sys.biz.baseBetAmount) 在 SQL 腳本中設定為 1。
        //    (該狀態由 test-data-setup.sql 預設提供)

        // -> 模擬操作:
        loginAsTestUser();
        BigDecimal minimumBetAmount = bizUserFollowsService.getMinimumBetAmount();

        // -> 驗證目標: 返回的最小投注額應為系統設定的基礎金額 1.00。
        // 【註】根據 /sql/test-data-setup.sql, 'sys.biz.baseBetAmount' 的值設定為 1
        assertEquals(0, new BigDecimal("1.00").compareTo(minimumBetAmount), "新輪次的最小投注額應為系統基礎投注額");
    }

    /**
     * 場景二: 連敗輪次中 - 驗證用戶在連敗期間的最小投注金額
     */
    @Test
    @DisplayName("【場景二】獲取最小投注額：連敗輪次中")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_MinimumBetAmount_IsDoubled_WhenOnLosingStreak() {
        // -> 初始狀態: 手動設定用戶(2)處於連敗輪次中，且上一筆已失敗的投注金額 (last_bet_amount) 為 15.00。
        jdbcTemplate.update("UPDATE biz_user_progress SET last_bet_amount = 15.00 WHERE user_id = ?", TEST_USER_ID);

        // -> 模擬操作:
        loginAsTestUser();
        BigDecimal minimumBetAmount = bizUserFollowsService.getMinimumBetAmount();

        // -> 驗證目標: 返回的最小投注額應為上一筆金額的兩倍 (15.00 * 2 = 30.00)。
        assertEquals(0, new BigDecimal("30.00").compareTo(minimumBetAmount), "連敗輪次中的最小投注額應為上一筆的兩倍");
    }
}

