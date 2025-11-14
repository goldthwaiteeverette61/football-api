package org.dromara.biz.jobs;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.parser.ZgzcwDataProcessor;
import org.dromara.biz.service.impl.MatchDataCollectionZgzcwServiceImpl;
import org.springframework.stereotype.Component;

/**
 * 比赛数据定时采集任务
 *
 * @author Gemini
 */
@Slf4j
@RequiredArgsConstructor
@Component
@JobExecutor(name = "matchDataCollectionJobExecutor")
public class MatchDataCollectionJob {

    private final MatchDataCollectionZgzcwServiceImpl matchDataCollectionService;
    private final ZgzcwDataProcessor zgzcwDataProcessor;

    public ExecuteResult jobExecute(JobArgs jobArgs) {

        try {
            matchDataCollectionService.collectAndProcessMatches();
            zgzcwDataProcessor.processAllPoolsAndSave();
        } catch (Exception e) {
            return ExecuteResult.failure("比赛数据采集异常");
        }
        return ExecuteResult.success("比赛数据采集完成");
    }
}
