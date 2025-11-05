
package org.dromara.biz.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.service.BscDepositPoller;
import org.dromara.biz.service.IBscWalletService;
import org.dromara.system.service.ISysConfigService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BscScanJob {

    private final IBscWalletService iBscWalletService;
    private final BscDepositPoller bscDepositPoller;
    private final ISysConfigService iSysConfigService;

    /**
     * 每分钟执行一次钱包扫描任务
     */
//    @Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "*/10 * * * * ?")
    public void executeScan() {
        String c = iSysConfigService.selectConfigByKey("sys.biz.change");
        if(!c.equals("1")){
            log.info("系统充值未开发，不进行充值扫描");
            return ;
        }

        try {
            bscDepositPoller.pollNewBlocks();
//            iBscWalletService.scanAllWallets();
            log.info("定时任务: BSC钱包扫描完成。");
        } catch (Exception e) {
            log.error("定时任务: BSC钱包扫描时发生异常。", e);
        }
    }
}
