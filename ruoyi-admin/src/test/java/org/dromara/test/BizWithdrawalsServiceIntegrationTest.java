package org.dromara.biz.service.impl;

import jakarta.annotation.Resource;
import org.dromara.biz.domain.bo.WithdrawalApplyBo;
import org.dromara.biz.service.IBizWithdrawalsService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysUserService;
import org.dromara.test.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 提现服务整合测试
 *
 * @description: 该测试类旨在验证用户提现申请的核心业务流程，
 * 特别是动态手续费的计算和相关数据的正确记录。
 */
class BizWithdrawalsServiceIntegrationTest extends BaseIntegrationTest {

    @Resource
    private IBizWithdrawalsService bizWithdrawalsService;
    @Resource
    private ISysUserService sysUserService;

    // 根据 SQL 文件定义常量
    private static final String VALID_WALLET_ADDRESS = "Txxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    @Test
    @DisplayName("【场景一】当所有信息有效时，应成功提交提现申请")
    @Sql(scripts = "/sql/withdrawal-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Successful_WithdrawalApplication() {
        // --- 1. Arrange (安排) ---
        // 【修改】直接调用基类的登录方法
        loginAsTestUser();

        WithdrawalApplyBo applyBo = new WithdrawalApplyBo();
        applyBo.setAmount(new BigDecimal("100.00"));
        applyBo.setToWalletAddress(VALID_WALLET_ADDRESS);
        applyBo.setPayPassword(CORRECT_PAY_PASSWORD);

        // --- 2. Act (执行) ---
        assertDoesNotThrow(() -> bizWithdrawalsService.applyForWithdrawal(applyBo));

        // --- 3. Assert (断言) ---
        // a. 验证用户余额是否被正确扣除
        SysUserVo userAfter = sysUserService.selectUserById(TEST_USER_ID);
        assertEquals(0, new BigDecimal("900.00").compareTo(userAfter.getBalance()), "提现申请后，用户余额应被扣除");

        // b. 验证提现记录是否已创建，并且手续费和最终金额计算正确
        Map<String, Object> withdrawalRecord = jdbcTemplate.queryForMap(
            "SELECT * FROM biz_withdrawals WHERE user_id = ?", TEST_USER_ID
        );
        assertNotNull(withdrawalRecord, "应创建提现记录");
        assertEquals(0, new BigDecimal("100.00").compareTo((BigDecimal) withdrawalRecord.get("amount")), "提现申请金额应为 100.00");
        assertEquals(0, new BigDecimal("1.50").compareTo((BigDecimal) withdrawalRecord.get("network_fee")), "手续费应为从系统配置读取的 1.50");
        assertEquals(0, new BigDecimal("98.50").compareTo((BigDecimal) withdrawalRecord.get("final_amount")), "最终到账金额应为 98.50");
        assertEquals("PENDING", withdrawalRecord.get("status"), "提现状态应为待审核");

        // c. 验证是否生成了对应的待处理交易流水
        Map<String, Object> transactionRecord = jdbcTemplate.queryForMap(
            "SELECT * FROM biz_transactions WHERE user_id = ? AND transaction_type = 'WITHDRAWAL'", TEST_USER_ID
        );
        assertNotNull(transactionRecord, "应创建交易流水");
        assertEquals(0, new BigDecimal("-100.00").compareTo((BigDecimal) transactionRecord.get("amount")), "交易流水金额应为 -100.00");
        assertEquals("PENDING", transactionRecord.get("status"), "交易流水状态应为待处理");
        assertEquals(withdrawalRecord.get("withdrawal_id").toString(), transactionRecord.get("source_id"), "交易流水应关联到提现ID");
    }

    @Test
    @DisplayName("【场景二】当余额不足时，提现申请应失败")
    @Sql(scripts = "/sql/withdrawal-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Withdrawal_ShouldFail_WithInsufficientBalance() {
        // --- 1. Arrange (安排) ---
        // 【修改】直接调用基类的登录方法
        loginAsTestUser();

        WithdrawalApplyBo applyBo = new WithdrawalApplyBo();
        applyBo.setAmount(new BigDecimal("2000.00")); // 金额超过用户余额
        applyBo.setToWalletAddress(VALID_WALLET_ADDRESS);
        applyBo.setPayPassword(CORRECT_PAY_PASSWORD);

        // --- 2. Act & 3. Assert (执行与断言) ---
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            bizWithdrawalsService.applyForWithdrawal(applyBo);
        });
        assertEquals("账户余额不足", exception.getMessage());
    }

}

