package org.dromara.test;

import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 投注模式切换功能整合测试
 *
 * @description: 该测试类旨在验证 IBizUserProgressService.updateBetType 方法的核心业务逻辑。
 * 核心验证点：1. 无连败时可成功切换； 2. 存在连败时禁止切换。
 */
public class UpdateBetTypeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private IBizUserProgressService bizUserProgressService;

    /**
     * 场景一: 成功切换 - 用户无连败记录，成功将模式从 'normal' 切换至 'double'
     */
    @Test
    @DisplayName("【场景一】成功切换模式：无连败记录")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Success_WhenNoLosses() {
        // -> 初始状态: 用户(2)无连败记录 (consecutive_losses = 0)，模式为 'normal'。
        //    (该状态由 test-data-setup.sql 默认提供)

        // -> 模拟操作:
        loginAsTestUser();
        bizUserProgressService.updateBetType(TEST_USER_ID, "double");

        // -> 验证目标: 用户的 bet_type 和 commission_rate 应被成功更新。
        Map<String, Object> progress = jdbcTemplate.queryForMap("SELECT * FROM biz_user_progress WHERE user_id = ?", TEST_USER_ID);

        assertEquals("double", progress.get("bet_type"), "投注模式应更新为 'double'");
        // 根据 test-data-setup.sql, 'sys.biz.rewardPersent' 的值是 0.5
        assertEquals(0, new BigDecimal("0.500").compareTo((BigDecimal) progress.get("commission_rate")), "佣金率应更新为 Double 模式的佣金率");
    }

    /**
     * 场景二: 切换失败 - 用户存在连败记录，尝试切换模式时应被系统拒绝
     */
    @Test
    @DisplayName("【场景二】切换失败：存在连败记录")
    @Sql(scripts = "/sql/test-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_Fail_WhenHasLosses() {
        // -> 初始状态: 手动设置用户(2)存在连败记录。
        jdbcTemplate.update("UPDATE biz_user_progress SET consecutive_losses = 1 WHERE user_id = ?", TEST_USER_ID);

        // -> 模拟操作 & 验证目标:
        //    调用 updateBetType 应该抛出 ServiceException。
        loginAsTestUser();
        Exception exception = assertThrows(ServiceException.class, () -> {
            bizUserProgressService.updateBetType(TEST_USER_ID, "double");
        }, "存在连败记录时，切换模式应抛出异常");

        assertTrue(exception.getMessage().contains("请先重置倍投后再进行此项操作"), "异常信息应正确提示用户");

        // -> 进一步验证: 数据库中的数据未被改变。
        String currentBetType = jdbcTemplate.queryForObject("SELECT bet_type FROM biz_user_progress WHERE user_id = ?", String.class, TEST_USER_ID);
        assertEquals("normal", currentBetType, "投注模式不应被改变");
    }
}
