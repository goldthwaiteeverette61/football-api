package org.dromara.biz.jobs;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.service.IBizBetOrdersService;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@JobExecutor(name = "schemeOrderJobExecutor")
public class SchemeOrderJob {

    private final IBizBetOrdersService bizBetOrdersService;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        try {
            // 呼叫服務層方法來處理業務邏輯
            bizBetOrdersService.settlePendingOrdersJob();
        } catch (Exception e) {
            return ExecuteResult.failure("【普通投注訂單】結算任務執行時發生未預期異常");
        }
        return ExecuteResult.success("【普通投注訂單】結算任務執行完畢。");
    }

}
