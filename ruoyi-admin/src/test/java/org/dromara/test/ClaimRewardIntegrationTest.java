package org.dromara.test;

import org.dromara.biz.service.IBizRewardService;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 领取理赔金业务整合测试 (新版)
 *
 * @description: 该测试类旨在验证新的“理赔申请”流程，并加入了支付密码校验。
 */
public class ClaimRewardIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private IBizRewardService bizRewardService;



    /**
     * 场景一: 成功申请 - 用户满足所有条件，并提供正确支付密码
     */
    @Test
    @DisplayName("【场景一】成功：提供正確支付密碼並創建理賠申請")
    @Sql(scripts = "/sql/test-claim-reward-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testScenario1_ClaimSuccessfully_WithCorrectPassword() {
        // -> 初始状态: 用户(2)连败8次，总损失100，模式为 'dou$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Puble'，可以领取。
        final BigDecimal totalLoss = new BigDecimal("100.00");
        jdbcTemplate.update("UPDATE biz_user_progress SET consecutive_losses = 8, consecutive_losses_amount = ?, can_claim_reward = 1, bet_type = 'double' WHERE user_id = ?",
            totalLoss, TEST_USER_ID);

        // -> 模拟操作:
        loginAsTestUser();
        assertDoesNotThrow(() -> {
            bizRewardService.claimRewardForCurrentUser(CORRECT_PAY_PASSWORD);
        }, "提供正確的支付密碼時，不應拋出異常");

        // -> 验证目标:
        Map<String, Object> claim = jdbcTemplate.queryForMap("SELECT * FROM biz_reward_claim WHERE user_id = ?", TEST_USER_ID);
        assertNotNull(claim, "理賠申請記錄應被創建");
        assertEquals("PENDING", claim.get("status"), "理賠單狀態應為 PENDING");
    }

    /**
     * 场景二: 失败 - 支付密码错误
     */
    @Test
    @DisplayName("【场景二】申请失败：支付密码错误")
    @Sql(scripts = "/sql/test-claim-reward-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testScenario2_ClaimFails_WithWrongPayPassword() {
        // -> 初始状态: 用户满足所有领取条件
        jdbcTemplate.update("UPDATE biz_user_progress SET consecutive_losses = 8, can_claim_reward = 1, bet_type = 'double' WHERE user_id = ?", TEST_USER_ID);

        // -> 模拟操作 & 验证目标:
        loginAsTestUser();
        Exception exception = assertThrows(ServiceException.class, () -> {
            bizRewardService.claimRewardForCurrentUser("wrong_password"); // 传入错误的密码
        });
        assertTrue(exception.getMessage().contains("支付密码错误"));
    }

    /**
     * 场景三: 失败 - 用户尚未设置支付密码
     */
    @Test
    @DisplayName("【场景三】申请失败：未设置支付密码")
    @Sql(scripts = "/sql/test-claim-reward-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testScenario3_ClaimFails_WhenPayPasswordNotSet() {
        // -> 初始状态: 用户满足所有领取条件，但 pay_password_seted 标记为 0
        jdbcTemplate.update("UPDATE biz_user_progress SET consecutive_losses = 8, can_claim_reward = 1, bet_type = 'double' WHERE user_id = ?", TEST_USER_ID);
        jdbcTemplate.update("UPDATE sys_user SET pay_password_seted = 0 WHERE user_id = ?", TEST_USER_ID);

        // -> 模拟操作 & 验证目标:
        loginAsTestUser();
        Exception exception = assertThrows(ServiceException.class, () -> {
            bizRewardService.claimRewardForCurrentUser(CORRECT_PAY_PASSWORD);
        });
        assertTrue(exception.getMessage().contains("请先设定您的支付密码"));
    }


    /**
     * 场景四: 失败 - 其他业务条件不满足 (例如：模式为 normal)
     */
    @Test
    @DisplayName("【场景四】申请失败：業務條件不滿足（模式為 Normal）")
    @Sql(scripts = "/sql/test-claim-reward-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testScenario4_ClaimFails_WhenBusinessRuleViolated() {
        // -> 初始状态: 用户(2)连败8次，can_claim_reward=1, 但模式是 'normal'。
        jdbcTemplate.update("UPDATE biz_user_progress SET consecutive_losses = 8, can_claim_reward = 1, bet_type = 'normal' WHERE user_id = ?", TEST_USER_ID);

        // -> 模拟操作 & 验证目标:
        loginAsTestUser();
        Exception exception = assertThrows(ServiceException.class, () -> {
            // 即使提供了正确的密码，也应该因为业务规则而失败
            bizRewardService.claimRewardForCurrentUser(CORRECT_PAY_PASSWORD);
        });
        assertTrue(exception.getMessage().contains("普通模式无法申请理赔金"), "异常信息应正确提示模式错误");
    }

}

