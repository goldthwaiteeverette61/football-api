package org.dromara.test;

import jakarta.annotation.Resource;
import org.dromara.biz.jobs.MatchDataCollectionJob;
import org.dromara.biz.service.impl.MatchDataCollection500ServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * 比赛数据定时采集任务整合测试
 *
 * @description: 该测试类旨在验证 MatchDataCollectionJob 是否能被Spring正确加载，
 * 并且其 execute 方法能够成功调用底层的采集服务。
 */
@Transactional
class MatchDataCollectionJobIntegrationTest extends BaseIntegrationTest {

    @Resource
    private MatchDataCollectionJob matchDataCollectionJob;

    // 使用 @MockBean 将真实的采集服务替换为模拟对象，
    // 这样我们就可以在不发起真实网络请求的情况下，验证 Job 是否正确调用了 Service。
    @MockBean
    private MatchDataCollection500ServiceImpl matchDataCollection500Service;

    @Test
    @DisplayName("【场景一】执行定时任务应成功调用500.com数据采集服务")
    @Sql(scripts = "/sql/job-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test_JobExecution_Should_Trigger_500Service() {
        // --- 1. Arrange (安排) ---
        // 使用 Mockito 设定当 collectAndProcessMatches 方法被调用时，不执行任何实际操作。
        // 我们只关心 Job 是否调用了 Service，而不关心 Service 内部的具体实现。
        doNothing().when(matchDataCollection500Service).collectAndProcessMatches();

        // --- 2. Act (执行) ---
        // 直接调用定时任务的公开方法来触发其逻辑
        matchDataCollectionJob.execute();

        // --- 3. Assert (断言) ---
        // 验证 matchDataCollection500Service 的 collectAndProcessMatches 方法是否被调用了恰好一次。
        // 如果 Job 的逻辑正确，这个验证将会通过。
        verify(matchDataCollection500Service).collectAndProcessMatches();
    }
}
