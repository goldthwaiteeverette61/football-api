package org.dromara.biz.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.service.IBizBetOrdersService;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.biz.service.ISchemeSettlementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchemeSettlementJob {

    private final IBizBetOrdersService bizBetOrdersService;
    private final IBizSchemePeriodsService bizSchemePeriodsService;
    private final ISchemeSettlementService schemeSettlementService;

    /**
     * 【新增】定时取消超时的草稿订单
     * 执行时机：每分钟的第 15 秒
     */
    @Scheduled(cron = "15 * * * * ?")
    public void cancelExpiredDraftOrders() {
        try {
            bizBetOrdersService.processExpiredDraftOrders();
        } catch (Exception e) {
            log.error("【取消超时DRAFT订单】任务执行时发生未预期异常", e);
        }
        log.info("【取消超时DRAFT订单】任务执行完毕。");
    }

    /**
     * 定時結算【普通投注訂單】
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void settlePendingBetOrders() {
        try {
            // 呼叫服務層方法來處理業務邏輯
            bizBetOrdersService.settlePendingOrdersJob();
        } catch (Exception e) {
            log.error("【普通投注訂單】結算任務執行時發生未預期異常", e);
        }
        log.info("【普通投注訂單】結算任務執行完畢。");
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void execute() {
        log.info("开始执行方案结算定时任务...");

        BizSchemePeriodsBo queryBo = new BizSchemePeriodsBo();
        queryBo.setStatus("pending");
        List<BizSchemePeriodsVo> pendingPeriods = bizSchemePeriodsService.queryList(queryBo);

        if (pendingPeriods.isEmpty()) {
            log.info("没有待结算的方案期数。");
            return;
        }

        for (BizSchemePeriodsVo period : pendingPeriods) {
            try {
                // 核心修改：调用新服务的方法，这个调用会触发事务
                schemeSettlementService.settlePeriod(period);
            } catch (Exception e) {
                // 这里的 catch 块只记录异常，不会影响其他期数的结算
                log.error("结算期数ID: {} 时发生严重异常，事务已回滚", period.getPeriodId(), e);
            }
        }
        log.info("方案结算定时任务执行完毕。");
    }
}
