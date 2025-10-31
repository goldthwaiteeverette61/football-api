package org.dromara.test;

import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.dto.BetOrderDto;
import org.dromara.biz.domain.dto.BatchUpdateOddsDto;
import org.dromara.biz.service.IBetPlacementService;
import org.dromara.biz.service.IBizBetOrdersService;
import org.dromara.biz.service.IBizTransactionsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 整合測試：驗證訂單從創建到結算的完整生命週期
 */
public class OrderLifecycleIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private IBetPlacementService betPlacementService;
    @Autowired
    private IBizBetOrdersService bizBetOrdersService;
    @Autowired
    private IBizTransactionsService bizTransactionsService;

    @Test
    @DisplayName("【場景】用戶下注 -> 管理員確認賠率 -> 系統結算為中獎")
    @Sql(scripts = "/sql/order-lifecycle-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_PlaceBet_ConfirmOdds_AndSettleAsWin() {
        // --- 1. 準備階段 (Arrange) ---
        loginAsTestUser();
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal betAmount = new BigDecimal("100.00");

        // 構造一個 2x1 的投注項，這兩個選項在 setup.sql 中被設定為會中獎
        BetOrderDto betOrderDto = new BetOrderDto();
        betOrderDto.setBetAmount(betAmount);
        betOrderDto.setCombinationType("2x1");
        BetOrderDto.BetDetailDto detail1 = new BetOrderDto.BetDetailDto();
        detail1.setMatchId(3543615L);
        detail1.setPoolCode("HAD");
        detail1.setSelection("H");
        BetOrderDto.BetDetailDto detail2 = new BetOrderDto.BetDetailDto();
        detail2.setMatchId(3715685L);
        detail2.setPoolCode("TTG");
        detail2.setSelection("3");
        betOrderDto.setDetails(List.of(detail1, detail2));

        // --- 2. 執行下注 (Act 1) ---
        betPlacementService.placeBetOrder(betOrderDto);

        // --- 3. 驗證下注後狀態 (Assert 1) ---
        // a. 驗證訂單已創建，狀態為 DRAFT
        Long orderId = jdbcTemplate.queryForObject("SELECT order_id FROM biz_bet_orders WHERE user_id = ?", Long.class, TEST_USER_ID);
        assertNotNull(orderId, "訂單應已創建");
        Map<String, Object> orderAfterBet = jdbcTemplate.queryForMap("SELECT status, bet_amount FROM biz_bet_orders WHERE order_id = ?", orderId);
        assertEquals("draft", orderAfterBet.get("status"), "訂單初始狀態應為 draft");
        assertEquals(0, betAmount.compareTo((BigDecimal) orderAfterBet.get("bet_amount")), "訂單金額應正確");

        // b. 驗證用戶餘額已被扣除
        BigDecimal balanceAfterBet = jdbcTemplate.queryForObject("SELECT balance FROM sys_user WHERE user_id = ?", BigDecimal.class, TEST_USER_ID);
        assertEquals(0, initialBalance.subtract(betAmount).compareTo(balanceAfterBet), "用戶餘額應被扣除");

        // c. 驗證已生成投注流水
        assertFalse(bizTransactionsService.queryList(new BizTransactionsBo().setSourceId(orderId.toString()).setTransactionType("BET")).isEmpty(), "應生成投注流水");

        // --- 4. 執行確認賠率 (Act 2) ---
        List<Map<String, Object>> details = jdbcTemplate.queryForList("SELECT detail_id FROM biz_bet_order_details WHERE order_id = ?", orderId);
        BatchUpdateOddsDto oddsDto = new BatchUpdateOddsDto();
        BatchUpdateOddsDto.Detail oddsDetail1 = new BatchUpdateOddsDto.Detail();
        oddsDetail1.setDetailId((Long) details.get(0).get("detail_id"));
        oddsDetail1.setOdds(new BigDecimal("2.00"));
        BatchUpdateOddsDto.Detail oddsDetail2 = new BatchUpdateOddsDto.Detail();
        oddsDetail2.setDetailId((Long) details.get(1).get("detail_id"));
        oddsDetail2.setOdds(new BigDecimal("3.00"));
        oddsDto.setDetails(List.of(oddsDetail1, oddsDetail2));
        bizBetOrdersService.batchUpdateOdds(oddsDto);

        // --- 5. 驗證確認賠率後狀態 (Assert 2) ---
        Map<String, Object> orderAfterConfirm = jdbcTemplate.queryForMap("SELECT status, odds_desc FROM biz_bet_orders WHERE order_id = ?", orderId);
        assertEquals("pending", orderAfterConfirm.get("status"), "確認賠率後訂單狀態應為 pending");
        assertEquals("2.00 * 3.00", orderAfterConfirm.get("odds_desc"));

        // --- 6. 執行結算 (Act 3) ---
        // 【已修正】模擬定時任務，掃描並結算所有待開獎的訂單
        bizBetOrdersService.settlePendingOrdersJob();

        // --- 7. 驗證結算後狀態 (Assert 3) ---
        BigDecimal expectedPayout = new BigDecimal("100.00").multiply(new BigDecimal("2.00")).multiply(new BigDecimal("3.00")); // 100 * 2 * 3 = 600
        Map<String, Object> orderAfterSettle = jdbcTemplate.queryForMap("SELECT status, payout_amount FROM biz_bet_orders WHERE order_id = ?", orderId);
        assertEquals("won", orderAfterSettle.get("status"), "訂單結算後狀態應為 won");
        assertEquals(0, expectedPayout.compareTo((BigDecimal) orderAfterSettle.get("payout_amount")), "派彩金額應為 600.00");

        // b. 驗證獎金已返還至用戶餘額
        BigDecimal finalBalance = jdbcTemplate.queryForObject("SELECT balance FROM sys_user WHERE user_id = ?", BigDecimal.class, TEST_USER_ID);
        assertEquals(0, balanceAfterBet.add(expectedPayout).compareTo(finalBalance), "用戶最終餘額應為投注後餘額加上派彩");

        // c. 驗證已生成派獎流水
        assertFalse(bizTransactionsService.queryList(new BizTransactionsBo().setSourceId(orderId.toString()).setTransactionType("PAYOUT")).isEmpty(), "應生成派獎流水");
    }

    @Test
    @DisplayName("【新增場景】下注時應正確生成訂單的失效時間")
    @Sql(scripts = "/sql/order-lifecycle-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_ExpirationTime_IsSetToEarliestMatchTime() throws Exception {
        // --- 1. 準備階段 (Arrange) ---
        loginAsTestUser();

        // 構造一個包含兩場不同時間比賽的投注單
        BetOrderDto betOrderDto = new BetOrderDto();
        betOrderDto.setBetAmount(new BigDecimal("10.00"));
        betOrderDto.setCombinationType("2x1");
        BetOrderDto.BetDetailDto detail1 = new BetOrderDto.BetDetailDto(); // 比賽時間: 2025-10-16 12:00:00
        detail1.setMatchId(3543615L);
        detail1.setPoolCode("HAD");
        detail1.setSelection("H");
        BetOrderDto.BetDetailDto detail2 = new BetOrderDto.BetDetailDto(); // 比賽時間: 2025-10-16 10:00:00 (最早)
        detail2.setMatchId(3715685L);
        detail2.setPoolCode("TTG");
        detail2.setSelection("3");
        betOrderDto.setDetails(List.of(detail1, detail2));

        // 定義預期的最早比賽時間
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expectedExpirationTime = sdf.parse("2025-10-16 12:00:00");

        // --- 2. 執行下注 (Act) ---
        betPlacementService.placeBetOrder(betOrderDto);

        // --- 3. 驗證結果 (Assert) ---
        // 從資料庫中獲取新訂單的 expiration_time
        Timestamp actualExpirationTime = jdbcTemplate.queryForObject(
            "SELECT expiration_time FROM biz_bet_orders WHERE user_id = ?", Timestamp.class, TEST_USER_ID);

        assertNotNull(actualExpirationTime, "訂單的失效時間不應為 null");
        assertEquals(expectedExpirationTime.getTime(), actualExpirationTime.getTime(), "訂單的失效時間應等於所有比賽中最早的開賽時間");
    }
}

