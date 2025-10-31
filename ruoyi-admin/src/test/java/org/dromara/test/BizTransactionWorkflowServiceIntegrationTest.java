package org.dromara.biz.service.impl;

import jakarta.annotation.Resource;
import org.dromara.biz.domain.dto.UserTransferDto;
import org.dromara.biz.service.IBizTransactionWorkflowService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysUserService;
import org.dromara.test.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 站内转账服务整合测试
 *
 * @description: 该测试类旨在验证用户之间的站内转账功能，包括成功转账、余额不足、
 * 收款人不存在、不能给自己转账以及支付密码错误等核心业务场景。
 */
@Transactional
class BizTransactionWorkflowServiceIntegrationTest extends BaseIntegrationTest {

    @Resource
    private IBizTransactionWorkflowService bizTransactionWorkflowService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    // 根据 internal-transfer-test-setup.sql 文件定义常量
    private static final Long TO_USER_ID = 3L;
    private static final String WRONG_PAY_PASSWORD = "wrongpassword";

    /**
     * 测试场景一: 成功的站内转账
     */
    @Test
    @DisplayName("【场景一】当所有信息正确时，转账应成功")
    @Sql(scripts = "/sql/internal-transfer-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Successful_InternalTransfer() {
        // --- 1. Arrange (安排) ---
        loginAsTestUser();

        UserTransferDto dto = new UserTransferDto();
        dto.setToUserName("toUser");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setPayPassword(CORRECT_PAY_PASSWORD); // 【修改】提供正确的支付密码

        // --- 2. Act (执行) ---
        assertDoesNotThrow(() -> bizTransactionWorkflowService.initiateTransfer(dto));

        // --- 3. Assert (断言) ---
        SysUserVo fromUserAfter = sysUserService.selectUserById(TEST_USER_ID);
        SysUserVo toUserAfter = sysUserService.selectUserById(TO_USER_ID);

        assertEquals(0, new BigDecimal("900.00").compareTo(fromUserAfter.getBalance()), "转账后，fromUser 的余额应为 900.00");
        assertEquals(0, new BigDecimal("100.00").compareTo(toUserAfter.getBalance()), "转账后，toUser 的余额应为 100.00");

        Map<String, Object> transferOutRecord = jdbcTemplate.queryForMap(
            "SELECT user_name, amount FROM biz_transactions WHERE user_id = ? AND transaction_type = 'INTERNAL_TRANSFER_OUT'",
            TEST_USER_ID
        );
        Map<String, Object> transferInRecord = jdbcTemplate.queryForMap(
            "SELECT user_name, amount FROM biz_transactions WHERE user_id = ? AND transaction_type = 'INTERNAL_TRANSFER_IN'",
            TO_USER_ID
        );

        assertNotNull(transferOutRecord, "应存在转出流水");
        assertEquals(TEST_USER_NAME, transferOutRecord.get("user_name"));
        assertEquals(0, new BigDecimal("-100.00").compareTo((BigDecimal) transferOutRecord.get("amount")), "转出金额应为 -100.00");

        assertNotNull(transferInRecord, "应存在转入流水");
        assertEquals("toUser", transferInRecord.get("user_name"));
        assertEquals(0, new BigDecimal("100.00").compareTo((BigDecimal) transferInRecord.get("amount")), "转入金额应为 100.00");
    }

    /**
     * 测试场景二: 支付密码错误导致转账失败
     */
    @Test
    @DisplayName("【场景二】当支付密码错误时，转账应失败")
    @Sql(scripts = "/sql/internal-transfer-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Transfer_ShouldFail_WithIncorrectPayPassword() {
        // --- 1. Arrange (安排) ---
        loginAsFromUser();

        UserTransferDto dto = new UserTransferDto();
        dto.setToUserName("toUser");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setPayPassword(WRONG_PAY_PASSWORD); // 【修改】提供错误的支付密码

        // --- 2. Act & 3. Assert (执行与断言) ---
        ServiceException exception = assertThrows(
            ServiceException.class,
            () -> bizTransactionWorkflowService.initiateTransfer(dto),
            "支付密码错误时，应该抛出业务异常"
        );
        assertEquals("支付密码错误", exception.getMessage());
    }


    /**
     * 测试场景三: 余额不足导致转账失败
     */
    @Test
    @DisplayName("【场景三】当用户余额不足时，转账应失败")
    @Sql(scripts = "/sql/internal-transfer-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Transfer_ShouldFail_WithInsufficientBalance() {
        // --- 1. Arrange (安排) ---
        loginAsFromUser();

        UserTransferDto dto = new UserTransferDto();
        dto.setToUserName("toUser");
        dto.setAmount(new BigDecimal("2000.00")); // 该金额超过 fromUser 的余额
        dto.setPayPassword(CORRECT_PAY_PASSWORD); // 【修改】提供正确的支付密码以通过密码验证

        // --- 2. Act & 3. Assert (执行与断言) ---
        ServiceException exception = assertThrows(
            ServiceException.class,
            () -> bizTransactionWorkflowService.initiateTransfer(dto),
            "余额不足时，应该抛出业务异常"
        );
        assertEquals("您的余额不足", exception.getMessage());
    }

    /**
     * 测试场景四: 用户不能给自己转账
     */
    @Test
    @DisplayName("【场景四】当用户尝试向自己转账时，应失败")
    @Sql(scripts = "/sql/internal-transfer-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Transfer_ShouldFail_WhenTransferringToSelf() {
        // --- 1. Arrange (安排) ---
        loginAsFromUser();

        UserTransferDto dto = new UserTransferDto();
        dto.setToUserName("fromUser");
        dto.setAmount(new BigDecimal("50.00"));
        dto.setPayPassword(CORRECT_PAY_PASSWORD); // 【修改】提供正确的支付密码

        // --- 2. Act & 3. Assert (执行与断言) ---
        ServiceException exception = assertThrows(
            ServiceException.class,
            () -> bizTransactionWorkflowService.initiateTransfer(dto),
            "尝试给自己转账时，应该抛出业务异常"
        );
        assertEquals("不能给自己转账", exception.getMessage());
    }

    // 辅助方法，用于模拟 'fromUser' 登录
    private void loginAsFromUser() {
        loginAsTestUser();
    }
}

