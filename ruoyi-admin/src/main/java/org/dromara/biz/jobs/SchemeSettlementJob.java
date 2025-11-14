package org.dromara.biz.jobs;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.biz.service.ISchemeSettlementService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@JobExecutor(name = "schemeSettlementJobExecutor")
public class SchemeSettlementJob {

    private final IBizSchemePeriodsService bizSchemePeriodsService;
    private final ISchemeSettlementService schemeSettlementService;

    /**
     * 【新增】定时取消超时的草稿订单
     * 执行时机：每分钟的第 15 秒
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        BizSchemePeriodsBo queryBo = new BizSchemePeriodsBo();
        queryBo.setStatus("pending");
        List<BizSchemePeriodsVo> pendingPeriods = bizSchemePeriodsService.queryList(queryBo);

        if (pendingPeriods.isEmpty()) {
            return ExecuteResult.success("没有待结算的方案期数");
        }

        for (BizSchemePeriodsVo period : pendingPeriods) {
            try {
                // 核心修改：调用新服务的方法，这个调用会触发事务
                schemeSettlementService.settlePeriod(period);
            } catch (Exception e) {
                return ExecuteResult.failure("结算期数ID: "+period.getPeriodId()+" 时发生严重异常，事务已回滚");
            }
        }
        return ExecuteResult.success("方案期数结算完成");
    }

}
