package org.dromara.test;

import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.service.IBizBetOrdersService;
import org.dromara.biz.service.IBizTransactionsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 整合測試：驗證訂單自動取消的定時任務邏輯
 * 【已重構】移除了 @Transactional 註解，以避免測試事務與業務事務衝突。
 */
@SpringBootTest
@ActiveProfiles("test")
public class OrderCancellationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private IBizBetOrdersService bizBetOrdersService;
    @Autowired
    private IBizTransactionsService bizTransactionsService;

    @Test
    @DisplayName("【成功場景】超時的 DRAFT 訂單應被取消並退款")
    @Sql(scripts = "/sql/order-cancellation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_ExpiredDraftOrder_IsCanceledAndRefunded() {
        // --- 1. 執行核心業務 ---
        // 直接呼叫定時任務的核心方法
        bizBetOrdersService.processExpiredDraftOrders();

        // --- 2. 驗證結果 ---
        // a. 驗證超時訂單 (ID=101) 的狀態已變為 'void'
        Map<String, Object> expiredOrder = jdbcTemplate.queryForMap("SELECT status FROM biz_bet_orders WHERE order_id = 101");
        assertEquals("void", expiredOrder.get("status"), "超時訂單的狀態應更新為 void");

        // b. 驗證用戶 (ID=2) 的餘額已退還
        Map<String, Object> user = jdbcTemplate.queryForMap("SELECT balance FROM sys_user WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, new java.math.BigDecimal("1050.00").compareTo((java.math.BigDecimal) user.get("balance")), "用戶餘額應從 1000 恢復到 1050");

        // c. 驗證已生成退款流水
        assertFalse(bizTransactionsService.queryList(new BizTransactionsBo().setSourceId("101").setTransactionType("REFUND")).isEmpty(), "應為訂單 101 生成退款流水");
    }

    @Test
    @DisplayName("【失敗場景】當退款失敗時，訂單取消操作應完整回滾")
    @Sql(scripts = "/sql/order-cancellation-failure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_CancellationFails_WhenRefundIsUnsuccessful() {
        // --- 1. 執行核心業務 ---
        // 【已修正】業務方法內部會捕獲並記錄單筆訂單的異常，但不會中斷整個任務，所以這裡不應拋出異常
        assertDoesNotThrow(() -> {
            bizBetOrdersService.processExpiredDraftOrders();
        }, "批次處理單筆訂單失敗時，主任務不應拋出異常");

        // --- 2. 驗證事務回滾的結果 ---
        // a. 驗證訂單 (ID=101) 的狀態仍然是 'draft'，未被錯誤更新
        Map<String, Object> failedOrder = jdbcTemplate.queryForMap("SELECT status FROM biz_bet_orders WHERE order_id = 101");
        assertEquals("draft", failedOrder.get("status"), "退款失敗時，訂單狀態應保持 draft 不變");

        // b. 驗證沒有生成任何退款流水
        assertTrue(bizTransactionsService.queryList(new BizTransactionsBo().setSourceId("101").setTransactionType("REFUND")).isEmpty(), "退款失敗時，不應生成任何流水記錄");
    }

    @Test
    @DisplayName("【邊界場景】當沒有超時訂單時，任務應安全執行且無任何副作用")
    @Sql(scripts = "/sql/order-cancellation-no-op-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_JobRunsCleanly_WhenNoExpiredOrdersExist() {
        // --- 1. 執行核心業務 ---
        assertDoesNotThrow(() -> {
            bizBetOrdersService.processExpiredDraftOrders();
        }, "當沒有可處理的訂單時，任務不應拋出異常");

        // --- 2. 驗證資料未被錯誤修改 ---
        // a. 驗證未過期訂單 (ID=102) 的狀態仍然是 'draft'
        Map<String, Object> notExpiredOrder = jdbcTemplate.queryForMap("SELECT status FROM biz_bet_orders WHERE order_id = 102");
        assertEquals("draft", notExpiredOrder.get("status"), "未過期的訂單狀態不應被改變");

        // b. 驗證狀態不符訂單 (ID=103) 的狀態仍然是 'pending'
        Map<String, Object> wrongStatusOrder = jdbcTemplate.queryForMap("SELECT status FROM biz_bet_orders WHERE order_id = 103");
        assertEquals("pending", wrongStatusOrder.get("status"), "狀態不符的訂單不應被改變");

        // c. 驗證用戶餘額未發生變化
        Map<String, Object> user = jdbcTemplate.queryForMap("SELECT balance FROM sys_user WHERE user_id = ?", TEST_USER_ID);
        assertEquals(0, new java.math.BigDecimal("1000.00").compareTo((java.math.BigDecimal) user.get("balance")), "用戶餘額不應有任何變化");
    }
}

