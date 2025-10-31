package org.dromara.test;

import org.dromara.biz.domain.bo.UserFollowDetailsSaveBo;
import org.dromara.biz.domain.dto.FollowSchemeDto;
import org.dromara.biz.service.*;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 業務整合測試類
 *
 * @description: 該測試類旨在驗證從用戶投注到週期結算的完整業務流程，特別是圍繞 `betType` 的輪次（Round）概念。
 * 它同時測試了 BizSchemeWorkflowServiceImpl, BizUserFollowsServiceImpl, 和 SchemeSettlementServiceImpl 之間的協作。
 */
public class SchemeBusinessIntegrationTest extends BaseIntegrationTest {

    // 核心業務服務
    @Autowired
    private IBizSchemeWorkflowService bizSchemeWorkflowService;
    @Autowired
    private ISchemeSettlementService schemeSettlementService;
    @Autowired
    private IBizSchemePeriodsService bizSchemePeriodsService;
    @Autowired
    private IBizUserFollowsService bizUserFollowsService;

    @Autowired
    private IBizRewardService bizRewardService;

    /**
     * 場景一: 驗證狀態鎖 - 當用戶處於“投注中”狀態時，應禁止理賠和重置操作
     */
    @Test
    @DisplayName("【場景一】驗證狀態鎖：投注中禁止理賠與重置")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_ActionsLocked_WhenBetIsInFlight() {
        // -> 初始狀態: 手動將用戶(2)的 bet_amount 設置為非零值，模擬“投注中”狀態。
        //    同時給予用戶可領取理賠的資格，以確保測試的唯一變量是“投注中”狀態。
        jdbcTemplate.update(
            "UPDATE biz_user_progress SET bet_amount = 10.00, consecutive_losses = 8, can_claim_reward = 1 WHERE user_id = ?",
            TEST_USER_ID
        );

        // -> 模擬操作 & 驗證目標:
        loginAsTestUser();

        // 1. 驗證領取理賠被拒絕
        Exception claimException = assertThrows(ServiceException.class, () -> {
            bizRewardService.claimRewardForCurrentUser(CORRECT_PAY_PASSWORD);
        }, "投注中應禁止領取理賠");
        assertTrue(claimException.getMessage().contains("当前正在投注中，不允许此项操作"));

        // 2. 驗證手動重置被拒絕
        Exception resetException = assertThrows(ServiceException.class, () -> {
            bizRewardService.resetConsecutiveLossesForCurrentUser(CORRECT_PAY_PASSWORD);
        }, "投注中應禁止手動重置");
        assertTrue(resetException.getMessage().contains("当前正在投注中，不允许此项操作"));
    }

    /**
     * 場景二: 驗證 last_bet_amount - 用戶投注失敗後，系統應正確更新 last_bet_amount 並重置 bet_amount
     */
    @Test
    @DisplayName("【場景二】驗證結算：last_bet_amount 在失敗後被正確更新")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_LastBetAmount_IsUpdatedCorrectly_AfterLoss() {
        // -> 初始狀態: 用戶(2)處於新輪次，last_bet_amount 和 bet_amount 均為 0。
        final long periodId = 1L;
        setupPendingPeriod(periodId, 101L, "W", "1.50");
        final BigDecimal currentBetAmount = new BigDecimal("15.00");

        // -> 模擬操作:
        //    1. 用戶投注
        loginAsTestUser();
        FollowSchemeDto followDto = new FollowSchemeDto();
        followDto.setPeriodId(periodId);
        followDto.setBetAmount(currentBetAmount);
        bizSchemeWorkflowService.followScheme(followDto);

        //    驗證點 1: 投注後，bet_amount 應被更新為“投注中”狀態，last_bet_amount 保持不變
        Map<String, Object> progressAfterBet = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, currentBetAmount.compareTo((BigDecimal) progressAfterBet.get("bet_amount")), "投注後，bet_amount 應更新為當前投注額");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progressAfterBet.get("last_bet_amount")), "投注後，last_bet_amount 應保持為0");

        //    2. 確認投注並結算為“失敗”
        Long followId = getFollowId(TEST_USER_ID, periodId);
        saveFollowDetails(followId, periodId, 101L, "W", "1.50");
        confirmFollow(TEST_USER_ID, periodId);
        finalizeMatchAsLoss(101L);
        schemeSettlementService.settlePeriod(bizSchemePeriodsService.queryById(periodId));

        // -> 驗證目標: 結算失敗後，last_bet_amount 被更新為本次投注額，bet_amount 被重置為 0
        Map<String, Object> progressAfterSettlement = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, currentBetAmount.compareTo((BigDecimal) progressAfterSettlement.get("last_bet_amount")), "結算失敗後，last_bet_amount 應更新為本次投注額");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progressAfterSettlement.get("bet_amount")), "結算失敗後，bet_amount 應重置為0");
        assertEquals(1, progressAfterSettlement.get("consecutive_losses"), "連敗次數應為 1");
    }



    /**
     * 場景一: 新輪次開始 - 用戶投注並失敗，驗證 last_bet_amount 被正確設定
     */
    @Test
    @DisplayName("【場景一】新輪次投注並失敗")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_NewRound_Loses_And_LastBetAmountIsSet() {
        // -> 初始狀態: 用戶(2)處於新輪次，last_bet_amount 和 bet_amount 均為 0。
        final long periodId = 1L;
        setupPendingPeriod(periodId, 101L, "W", "1.50");
        final BigDecimal currentBetAmount = new BigDecimal("15.00");

        // -> 模擬操作:
        //    1. 用戶投注
        loginAsTestUser();
        FollowSchemeDto followDto = new FollowSchemeDto();
        followDto.setPeriodId(periodId);
        followDto.setBetAmount(currentBetAmount);
        bizSchemeWorkflowService.followScheme(followDto);

        //    驗證點 1: 投注後，bet_amount 應被更新為“投注中”狀態，last_bet_amount 保持不變
        Map<String, Object> progressAfterBet = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, currentBetAmount.compareTo((BigDecimal) progressAfterBet.get("bet_amount")), "投注後，bet_amount 應更新為當前投注額");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progressAfterBet.get("last_bet_amount")), "投注後，last_bet_amount 應保持為0");

        //    2. 確認投注並結算為“失敗”
        Long followId = getFollowId(TEST_USER_ID, periodId);
        saveFollowDetails(followId, periodId, 101L, "W", "1.50");
        confirmFollow(TEST_USER_ID, periodId);
        finalizeMatchAsLoss(101L);
        schemeSettlementService.settlePeriod(bizSchemePeriodsService.queryById(periodId));

        // -> 驗證目標: 結算失敗後，last_bet_amount 被更新為本次投注額，bet_amount 被重置為 0
        Map<String, Object> progressAfterSettlement = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, currentBetAmount.compareTo((BigDecimal) progressAfterSettlement.get("last_bet_amount")), "結算失敗後，last_bet_amount 應更新為本次投注額");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progressAfterSettlement.get("bet_amount")), "結算失敗後，bet_amount 應重置為0");
        assertEquals(1, progressAfterSettlement.get("consecutive_losses"), "連敗次數應為 1");
    }

    /**
     * 場景二: 連敗輪次中 - Double模式下翻倍投注並再次失敗
     */
    @Test
    @DisplayName("【場景二】連敗中翻倍投注並再次失敗")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_LosingStreak_LosesAgain_And_LastBetAmountIsUpdated() {
        // -> 初始狀態: 用戶(2)已連敗1次，上一筆失敗的投注額為 10.00。
        jdbcTemplate.update("UPDATE biz_user_progress SET last_bet_amount = 10.00, consecutive_losses = 1, consecutive_losses_amount = 10.00 WHERE user_id = ?", TEST_USER_ID);
        final long periodId = 2L;
        setupPendingPeriod(periodId, 102L, "D", "2.00");
        final BigDecimal currentBetAmount = new BigDecimal("20.00"); // 10.00 * 2

        // -> 模擬操作:
        loginAsTestUser();
        FollowSchemeDto followDto = new FollowSchemeDto();
        followDto.setPeriodId(periodId);
        followDto.setBetAmount(currentBetAmount);
        bizSchemeWorkflowService.followScheme(followDto);

        //    驗證點 1: 投注後，bet_amount 更新，last_bet_amount 不變
        Map<String, Object> progressAfterBet = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, currentBetAmount.compareTo((BigDecimal) progressAfterBet.get("bet_amount")), "投注後，bet_amount 應更新為當前投注額");
        assertEquals(0, new BigDecimal("10.00").compareTo((BigDecimal) progressAfterBet.get("last_bet_amount")), "投注後，last_bet_amount 應保持為上一筆的失敗金額");

        //    2. 確認投注並結算為“失敗”
        Long followId = getFollowId(TEST_USER_ID, periodId);
        saveFollowDetails(followId, periodId, 102L, "D", "2.00");
        confirmFollow(TEST_USER_ID, periodId);
        finalizeMatchAsLoss(102L);
        schemeSettlementService.settlePeriod(bizSchemePeriodsService.queryById(periodId));

        // -> 驗證目標: 結算失敗後，last_bet_amount 被更新為本次的投注額 20.00
        Map<String, Object> progressAfterSettlement = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, currentBetAmount.compareTo((BigDecimal) progressAfterSettlement.get("last_bet_amount")), "結算失敗後，last_bet_amount 應更新為本次投注額");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progressAfterSettlement.get("bet_amount")), "結算失敗後，bet_amount 應重置為0");
        assertEquals(2, progressAfterSettlement.get("consecutive_losses"), "連敗次數應累加為 2");
    }


    /**
     * 場景三: 輪次結束 - Double模式下投注並中獎 (驗證佣金和狀態重置)
     */
    @Test
    @DisplayName("【場景三】連敗中投注並中獎")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_LosingStreak_Wins_And_ProgressIsReset() {
        // -> 初始狀態: 用戶(2)連敗2次，上一筆失敗投注20，總損失30。
        jdbcTemplate.update("UPDATE biz_user_progress SET bet_type = 'double', last_bet_amount = 20.00, consecutive_losses = 2, consecutive_losses_amount = 30.00 WHERE user_id = ?", TEST_USER_ID);
        final long periodId = 3L;
        setupPendingPeriod(periodId, 103L, "W", "3.00");
        final BigDecimal currentBetAmount = new BigDecimal("40.00"); // 20.00 * 2

        // -> 模擬操作:
        loginAsTestUser();
        FollowSchemeDto followDto = new FollowSchemeDto();
        followDto.setPeriodId(periodId);
        followDto.setBetAmount(currentBetAmount);
        bizSchemeWorkflowService.followScheme(followDto);

        Long followId = getFollowId(TEST_USER_ID, periodId);
        saveFollowDetails(followId, periodId, 103L, "W", "3.00");
        confirmFollow(TEST_USER_ID, periodId);
        finalizeMatchAsWin(103L);
        schemeSettlementService.settlePeriod(bizSchemePeriodsService.queryById(periodId));

        // -> 驗證目標:
        //    本次獎金 (maxPayout) = 40 * 3.0 = 120
        //    歷史連輸 (consecutiveLossesAmount) = 30
        //    本次成本 (currentBetAmount) = 40
        //    總成本 = 30 + 40 = 70
        //    淨利潤 (commissionBase) = 120 - 70 = 50
        //    佣金 (commission) = 50 * 0.5 = 25
        //    最終派獎 (finalPayout) = 120 - 25 = 95
        BigDecimal payoutAmount = jdbcTemplate.queryForObject("SELECT payout_amount FROM biz_user_follows WHERE follow_id = ?", BigDecimal.class, followId);
        assertNotNull(payoutAmount);
        assertEquals(0, new BigDecimal("95.00").compareTo(payoutAmount), "派彩金額應為 95.00");

        Map<String, Object> progress = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, progress.get("consecutive_losses"), "輪次結束，連敗次數應重置為 0");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progress.get("last_bet_amount")), "輪次結束，last_bet_amount 應重置為 0");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progress.get("consecutive_losses_amount")), "輪次結束，consecutive_losses_amount 连黑金额應重置為 0");
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) progress.get("bet_amount")), "輪次結束，bet_amount 應重置為 0");
    }

    // ============================= 輔助方法 =============================

    public void setupPendingPeriod(long periodId, long matchId, String selection, String odds) {
        jdbcTemplate.update("INSERT INTO `biz_scheme_periods` (`period_id`, `name`, `status`, `deadline_time`) VALUES (?, '測試方案', 'pending', DATEADD('DAY', 1, NOW()))", periodId);
        jdbcTemplate.update("INSERT INTO `biz_scheme_period_details` (`detail_id`, `period_id`, `match_id`, `pool_code`, `selection`, `odds`) VALUES (?, ?, ?, 'hhad', ?, ?)",
            periodId, periodId, matchId, selection, new BigDecimal(odds)
        );
    }

    private Long getFollowId(long userId, long periodId) {
        return jdbcTemplate.queryForObject("SELECT follow_id FROM biz_user_follows WHERE user_id = ? AND period_id = ?", Long.class, userId, periodId);
    }

    private void saveFollowDetails(Long followId, Long periodId, Long matchId, String selection, String odds) {
        UserFollowDetailsSaveBo saveDetailsBo = new UserFollowDetailsSaveBo();
        saveDetailsBo.setFollowIds(List.of(followId));
        UserFollowDetailsSaveBo.DetailItem detailItem = new UserFollowDetailsSaveBo.DetailItem();
        detailItem.setPeriodId(periodId);
        detailItem.setPeriodDetailsId(periodId);
        detailItem.setMatchId(matchId);
        detailItem.setPoolCode("hhad");
        detailItem.setSelection(selection);
        detailItem.setOdds(new BigDecimal(odds));
        saveDetailsBo.setCombinations(List.of(detailItem));
        bizUserFollowsService.saveFollowDetails(saveDetailsBo);
    }

    private void confirmFollow(long userId, long periodId) {
        jdbcTemplate.update("UPDATE biz_user_follows SET status = 'bought' WHERE user_id = ? AND period_id = ?", userId, periodId);
    }

    private void finalizeMatchAsLoss(long matchId) {
        jdbcTemplate.update("INSERT INTO `biz_match_results` (`match_id`, `pool_code`, `combination`) VALUES (?, 'hhad', 'L')", matchId);
        jdbcTemplate.update("UPDATE biz_matches SET status = 'Payout' WHERE match_id = ?", matchId);
    }

    private void finalizeMatchAsWin(long matchId) {
        jdbcTemplate.update("INSERT INTO `biz_match_results` (`match_id`, `pool_code`, `combination`) VALUES (?, 'hhad', 'W')", matchId);
        jdbcTemplate.update("UPDATE biz_matches SET status = 'Payout' WHERE match_id = ?", matchId);
    }
}

