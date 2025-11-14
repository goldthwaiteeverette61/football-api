
package org.dromara.biz.jobs;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.service.BscDepositPoller;
import org.dromara.biz.service.IBscWalletService;
import org.dromara.system.service.ISysConfigService;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@JobExecutor(name = "bscJobExecutor")
public class BscJobExecutor {

    private final IBscWalletService iBscWalletService;
    private final BscDepositPoller bscDepositPoller;
    private final ISysConfigService iSysConfigService;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        String c = iSysConfigService.selectConfigByKey("sys.biz.change");

        if(!c.equals("1")){
            return ExecuteResult.failure("系统充值未开发，不进行充值扫描");
        }

        try {
            bscDepositPoller.pollNewBlocks();
        } catch (Exception e) {
            return ExecuteResult.failure("BSC钱包扫描时发生异常");
        }

        return ExecuteResult.success("BSC钱包扫描完成");
    }
}
