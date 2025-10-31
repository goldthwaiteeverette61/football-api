package org.dromara.test;

import cn.dev33.satoken.stp.SaLoginModel;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 整合測試基礎類別
 * @description: 提供所有整合測試共用的配置和輔助方法，例如模擬用戶登入。
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected final long TEST_USER_ID = 2L;
    protected final String TEST_USER_NAME="test_user_"+TEST_USER_ID;

    // 预设一个正确的支付密码，用于大部分测试场景
    protected final String CORRECT_PAY_PASSWORD = "password123";

    protected final String CORRECT_PAY_PASSWORD_HASH = "$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Pu";

    /**
     * 輔助方法：模擬測試用戶登入
     * @description: 抽離重複的登入邏輯，為當前執行緒建立一個有效的登入會話。
     */
    protected void loginAsTestUser() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername(TEST_USER_NAME);
        loginUser.setUserId(TEST_USER_ID);
        loginUser.setUserType("app_user"); // 根據您的用戶體系設定
        loginUser.setClientKey("e5cd7e4891bf95d1d19206ce24a7b32e"); // 模擬一個客戶端 Key
        loginUser.setDeviceType("pc"); // 模擬設備類型

        SaLoginModel model = new SaLoginModel();
        model.setDevice(loginUser.getUserType());

        // 【核心修正】將超時時間設定為一個足夠長但不會導致算術溢位的值 (例如：一天)
        // 原來的 Long.MAX_VALUE 在轉換為毫秒時會導致 long overflow
        long timeoutInSeconds = 86400L; // 24 * 60 * 60
        model.setTimeout(timeoutInSeconds);
        model.setActiveTimeout(timeoutInSeconds);
        model.setExtra("clientid", loginUser.getClientKey());

        LoginHelper.login(loginUser, model);
    }
}

