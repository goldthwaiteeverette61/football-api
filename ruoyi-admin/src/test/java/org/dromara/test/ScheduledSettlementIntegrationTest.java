package org.dromara.test;

import org.dromara.biz.domain.bo.BizBetOrderDetailsBo;
import org.dromara.biz.domain.dto.BatchUpdateOddsDto;
import org.dromara.biz.domain.dto.BetOrderDto;
import org.dromara.biz.domain.vo.BizBetOrderDetailsVo;
import org.dromara.biz.service.IBetPlacementService;
import org.dromara.biz.service.IBizBetOrderDetailsService;
import org.dromara.biz.service.IBizBetOrdersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 定時結算任務整合測試
 * @description: 驗證由定時任務觸發的批量結算流程，確保其能正確處理複雜投注場景。
 */
public class ScheduledSettlementIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private IBetPlacementService betPlacementService;
    @Autowired
    private IBizBetOrdersService bizBetOrdersService;
    @Autowired
    private IBizBetOrderDetailsService bizBetOrderDetailsService;

    @Test
    @DisplayName("【場景】定時任務觸發複雜訂單結算")
    @Sql(scripts = "/sql/complex-betting-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_ScheduledJob_SettlesComplexBetCorrectly() {
        // --- 1. 準備階段 (Arrange) ---
        // 模擬用戶登入並準備投注數據
        loginAsTestUser();
        final BigDecimal totalBetAmount = new BigDecimal("100.00");
        final long matchId1 = 3543615L;
        final long matchId2 = 3715685L;

        BetOrderDto betOrderDto = new BetOrderDto();
        betOrderDto.setBetAmount(totalBetAmount);
        betOrderDto.setCombinationType("system");
        betOrderDto.setDetails(Arrays.asList(
            createDetail(matchId1, "HAD", "H"),
            createDetail(matchId1, "HAD", "D"),
            createDetail(matchId2, "HAD", "H"),
            createDetail(matchId1, "CRS", "2:1"),
            createDetail(matchId1, "CRS", "3:1")
        ));

        // --- 2. 執行下注並確認賠率，使訂單進入 'pending' 狀態 (Act 1 & 2) ---
        betPlacementService.placeBetOrder(betOrderDto);
        Long orderId = jdbcTemplate.queryForObject("SELECT order_id FROM biz_bet_orders WHERE user_id = ?", Long.class, TEST_USER_ID);
        assertNotNull(orderId);

        List<BizBetOrderDetailsVo> details = bizBetOrderDetailsService.queryList(new BizBetOrderDetailsBo().setOrderId(orderId));
        List<BatchUpdateOddsDto.Detail> oddsUpdateDetails = details.stream().map(d -> {
            BatchUpdateOddsDto.Detail detail = new BatchUpdateOddsDto.Detail();
            detail.setDetailId(d.getDetailId());
            detail.setOdds(getOddsForSelection(d.getMatchId(), d.getPoolCode(), d.getSelection()));
            return detail;
        }).collect(Collectors.toList());

        BatchUpdateOddsDto oddsDto = new BatchUpdateOddsDto();
        oddsDto.setDetails(oddsUpdateDetails);
        bizBetOrdersService.batchUpdateOdds(oddsDto);

        Map<String, Object> orderAfterConfirm = jdbcTemplate.queryForMap("SELECT * FROM biz_bet_orders WHERE order_id = ?", orderId);
        assertEquals("pending", orderAfterConfirm.get("status"), "訂單應處於 'pending' 狀態，等待定時任務結算");

        // --- 3. 執行結算任務 (Act 3) ---
        // 【核心】直接調用定時任務所觸發的業務邏輯方法
        System.out.println("\n========= 開始模擬定時任務，執行批量結算 =========\n");
        bizBetOrdersService.settlePendingOrdersJob();
        System.out.println("\n========= 批量結算執行完畢 =========\n");

        // --- 4. 驗證結算後狀態 (Assert) ---
        Map<String, Object> orderAfterSettle = jdbcTemplate.queryForMap("SELECT * FROM biz_bet_orders WHERE order_id = ?", orderId);
        BigDecimal finalBalance = jdbcTemplate.queryForObject("SELECT balance FROM sys_user WHERE user_id = ?", BigDecimal.class, TEST_USER_ID);

        BigDecimal expectedPayout = new BigDecimal("600.00");
        BigDecimal expectedBalance = new BigDecimal("1500.00");

        assertEquals("won", orderAfterSettle.get("status"), "訂單最終狀態應為 'won'");
        assertEquals(0, expectedPayout.compareTo((BigDecimal) orderAfterSettle.get("payout_amount")), "派彩金額應為 600.00");
        assertEquals(0, expectedBalance.compareTo(finalBalance), "用戶最終餘額應為 1500.00");
    }

    /**
     * 輔助方法：創建投注詳情DTO
     */
    private BetOrderDto.BetDetailDto createDetail(long matchId, String poolCode, String selection) {
        BetOrderDto.BetDetailDto detail = new BetOrderDto.BetDetailDto();
        detail.setMatchId(matchId);
        detail.setPoolCode(poolCode);
        detail.setSelection(selection);
        return detail;
    }

    /**
     * 輔助方法：根據選項獲取預設賠率
     */
    private BigDecimal getOddsForSelection(long matchId, String poolCode, String selection) {
        if (matchId == 3543615L) { // 比賽1
            if ("HAD".equals(poolCode)) {
                if ("H".equals(selection)) return new BigDecimal("2.00"); // 勝
                if ("D".equals(selection)) return new BigDecimal("3.50"); // 平
            }
            if ("CRS".equals(poolCode)) {
                if ("2:1".equals(selection)) return new BigDecimal("7.50");
                if ("3:1".equals(selection)) return new BigDecimal("8.00"); // 中獎項
            }
        }
        if (matchId == 3715685L) { // 比賽2
            if ("HAD".equals(poolCode) && "H".equals(selection)) {
                return new BigDecimal("1.50"); // 勝
            }
        }
        return BigDecimal.ONE; // 預設賠率
    }
}
