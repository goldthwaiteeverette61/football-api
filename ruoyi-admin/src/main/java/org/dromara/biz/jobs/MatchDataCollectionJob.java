package org.dromara.biz.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.parser.ZgzcwDataProcessor;
import org.dromara.biz.service.impl.MatchDataCollectionZgzcwServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 比赛数据定时采集任务
 *
 * @author Gemini
 */
@Slf4j
@RequiredArgsConstructor
@Component
//@EnableAsync
public class MatchDataCollectionJob {

    private final MatchDataCollectionZgzcwServiceImpl matchDataCollectionService;
    private final ZgzcwDataProcessor zgzcwDataProcessor;

//    @Async
    @Scheduled(cron = "0 */2 * * * ?")
    public void execute() {
        try {
            matchDataCollectionService.collectAndProcessMatches();
            zgzcwDataProcessor.processAllPoolsAndSave();
            log.info("定时任务结束：比赛数据采集成功。");
        } catch (Exception e) {
            log.error("定时任务执行失败：比赛数据采集中出现异常。", e);
        } finally {
//            running.set(false);
        }
    }
}
