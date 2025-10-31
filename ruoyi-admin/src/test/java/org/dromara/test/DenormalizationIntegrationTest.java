package org.dromara.test;

import org.dromara.biz.domain.dto.FollowSchemeDto;
import org.dromara.biz.service.IBizRewardService;
import org.dromara.biz.service.IBizSchemeWorkflowService;
import org.dromara.common.core.domain.model.RegisterBody;
import org.dromara.system.domain.SysUser;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.test.helper.SchemeTestHelper;
import org.dromara.web.service.SysRegisterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 反正規化（user_name 欄位）整合測試
 *
 * @description: 該測試類旨在驗證在各個核心業務流程中，user_name 欄位是否被正確地填充。
 */
public class DenormalizationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private IBizSchemeWorkflowService bizSchemeWorkflowService;
    @Autowired
    private IBizRewardService bizRewardService;
    @Autowired
    private SysRegisterService sysRegisterService;
    @Autowired
    private SysUserMapper userMapper;

    // 【核心修正】注入方案測試的輔助類
    @Autowired
    private SchemeTestHelper schemeTestHelper;

    /**
     * 場景一: 驗證投注流程中的 user_name 填充
     */
    @Test
    @DisplayName("【場景一】驗證投注流程：user_name 是否被正確填充")
    @Sql(scripts = "/sql/test-denormalization-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_UserName_IsPopulated_DuringBettingProcess() {
        // -> 初始狀態: test-denormalization-setup.sql 已建立用戶(ID=2, user_name='testuser')。
        final long periodId = 1L;
        // 【核心修正】使用注入的輔助類來呼叫方法
        schemeTestHelper.setupPendingPeriod(periodId, 101L, "W", "1.50");

        // -> 模擬操作:
        loginAsTestUser();
        FollowSchemeDto followDto = new FollowSchemeDto();
        followDto.setPeriodId(periodId);
        followDto.setBetAmount(new BigDecimal("10.00"));
        bizSchemeWorkflowService.followScheme(followDto);

        // 2. 驗證 biz_user_follows
        String followsUserName = jdbcTemplate.queryForObject("SELECT user_name FROM biz_user_follows WHERE user_id = ?", String.class, TEST_USER_ID);
        assertEquals(TEST_USER_NAME, followsUserName, "biz_user_follows 中的 user_name 應被填充");

        // 3. 驗證 biz_transactions
        String transactionUserName = jdbcTemplate.queryForObject("SELECT user_name FROM biz_transactions WHERE user_id = ?", String.class, TEST_USER_ID);
        assertEquals(TEST_USER_NAME, transactionUserName, "biz_transactions 中的 user_name 應被填充");
    }

    /**
     * 場景二: 驗證理賠申請流程中的 user_name 填充
     */
    @Test
    @DisplayName("【場景二】驗證理賠申請：user_name 是否被正確填充")
    @Sql(scripts = "/sql/test-denormalization-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_UserName_IsPopulated_DuringRewardClaimProcess() {

        jdbcTemplate.update(
            "UPDATE biz_system_reserve_summary SET total_reserve_amount=100000"
        );
        // -> 初始狀態: 為用戶(2)設定可領取理賠的條件。
        jdbcTemplate.update(
            "UPDATE biz_user_progress SET consecutive_losses = 8,consecutive_losses_amount = 10, can_claim_reward = 1, bet_type = 'double' WHERE user_id = ?",
            TEST_USER_ID
        );
        jdbcTemplate.update(
            "UPDATE sys_user SET pay_password = '"+CORRECT_PAY_PASSWORD_HASH+"', pay_password_seted = 1 WHERE user_id = ?",
            TEST_USER_ID
        );

        // -> 模擬操作:
        loginAsTestUser();
        bizRewardService.claimRewardForCurrentUser(CORRECT_PAY_PASSWORD);

        // -> 驗證目標:
        String claimUserName = jdbcTemplate.queryForObject("SELECT user_name FROM biz_reward_claim WHERE user_id = ?", String.class, TEST_USER_ID);
        assertEquals(TEST_USER_NAME, claimUserName, "biz_reward_claim 中的 user_name 應被填充");
    }

    /**
     * 場景三: 驗證用戶註冊流程中的 user_name 填充
     */
    @Test
    @DisplayName("【場景三】驗證用戶註冊：user_name 是否被正確填充")
    @Sql(scripts = "/sql/test-denormalization-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_UserName_IsPopulated_DuringRegistrationProcess() {
        // -> 初始狀態: test-denormalization-setup.sql 已準備好邀請人和可用資源。
        final String newUsername = "newRegisteredUser";

        // -> 模擬操作:
        RegisterBody registerBody = new RegisterBody();
        registerBody.setUsername(newUsername);
        registerBody.setPassword("password123");
        registerBody.setInvitationCode("INVITE321");
        registerBody.setUserType("app_user");
        registerBody.setCode("9999l9999");
        registerBody.setEmail(newUsername + "@example.com");
        sysRegisterService.register(registerBody);

        // -> 驗證目標:
        SysUser newUser = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, newUsername)
        );
        assertNotNull(newUser, "新用戶應被成功建立");
        Long newUserId = newUser.getUserId();

        // 1. 驗證 biz_user_progress
        String progressUserName = jdbcTemplate.queryForObject("SELECT user_name FROM biz_user_progress WHERE user_id = ?", String.class, newUserId);
        assertEquals(newUsername, progressUserName, "新用戶的 biz_user_progress 中的 user_name 應被填充");

        // 2. 驗證 biz_deposit_wallets
        String walletUserName = jdbcTemplate.queryForObject("SELECT user_name FROM biz_deposit_wallets WHERE user_id = ?", String.class, newUserId);
        assertEquals(newUsername, walletUserName, "新用戶的 biz_deposit_wallets 中的 user_name 應被填充");
    }
}

