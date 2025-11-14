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
@JobExecutor(name = "CancelExpireDraftOrderJobExecutor")
public class CancelExpireDraftOrder {

    private final IBizBetOrdersService bizBetOrdersService;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        try {
            bizBetOrdersService.processExpiredDraftOrders();
        } catch (Exception e) {
            return ExecuteResult.failure("【取消超时DRAFT订单】任务执行时发生未预期异常");
        }

        return ExecuteResult.success("【取消超时DRAFT订单】任务执行完成");
    }
}
