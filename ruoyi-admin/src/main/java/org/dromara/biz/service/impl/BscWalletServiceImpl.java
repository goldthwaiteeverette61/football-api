package org.dromara.biz.service.impl;

import com.google.gson.JsonArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.config.BscScanConfig;
import org.dromara.biz.domain.bo.BizDepositWalletsBo;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.biz.service.IBizChainSyncStateService;
import org.dromara.biz.service.IBizDepositWalletsService;
import org.dromara.biz.service.IBscTransactionsService;
import org.dromara.biz.service.IBscWalletService;
import org.dromara.biz.utils.EthScanUtil;
import org.dromara.system.service.ISysConfigService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BSC链钱包服务实现 (V2 兼容版) - 高效轮询备用方案
 * 使用 Etherscan API的account#tokentx方法，单次请求获取所有USDT转账事件，然后在本地进行匹配。
 *
 * @author YourName
 * @date 2025-09-29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BscWalletServiceImpl implements IBscWalletService {

    private final IBizDepositWalletsService bizDepositWalletsService;
    private final BscScanConfig bscScanConfig;
    private final IBizChainSyncStateService bizChainSyncStateService;
    private final ISysConfigService iSysConfigService;
    private final IBscTransactionsService bscTransactionsService;


    @Override
    public void scanAllWallets() {
        BizDepositWalletsBo queryBo = new BizDepositWalletsBo();
        queryBo.setStatus("active");
        List<BizDepositWalletsVo> wallets = bizDepositWalletsService.queryList(queryBo);

        if (wallets.isEmpty()) {
            log.info("BSC - 数据库中没有需要扫描的充值钱包地址。");
            return;
        }

        Map<String, BizDepositWalletsVo> walletMap = wallets.stream()
            .collect(Collectors.toMap(w -> w.getWalletAddress().toLowerCase(), w -> w));

        long lastSyncedBlock = bizChainSyncStateService.getLastSyncedBlock("BSC",bscScanConfig.getChainId());
        long startBlock = lastSyncedBlock < 0 ? 0 : lastSyncedBlock + 1;
        long endBlock = 0;
        long latestBlock = 0;

        try {
            // --- 【已重構】步驟一：強制獲取最新區塊號 ---
            try {
                latestBlock = EthScanUtil.getBscLatestBlockNumber(bscScanConfig);
                log.info("-----当前系统：{}，区块链最新高度：{}------偏差：{}", lastSyncedBlock, latestBlock, (latestBlock - lastSyncedBlock));
            } catch (Exception e) {
                // 如果無法獲取最新區塊號，則中止本次任務，這是防止跳過區塊的關鍵
                log.error("BSC - [高效备用轮询] 获取最新区块号失败，无法确定安全扫描范围，任务中止。错误: {}", e.getMessage());
                return;
            }

            // --- 步驟二：計算安全掃描範圍 ---
            long scanSpan = Long.parseLong(iSysConfigService.selectConfigByKey("bsc.scanSpan"));
            endBlock = startBlock + scanSpan - 1;
            endBlock = Math.min(endBlock, latestBlock); // 確保掃描終點不超過已知的最新區塊

            if (startBlock > endBlock) {
                return;
            }

            JsonArray transactions = EthScanUtil.fetchTransactions(bscScanConfig,startBlock, endBlock);
            if (transactions == null) {
                log.error("BSC - [高效备用轮询] 从API获取区块 {}->{} 的交易失败，任务提前中止。", startBlock, endBlock);
                return;
            }

            if (!transactions.isEmpty()) {
                // 呼叫事務方法處理整個批次，如果失敗會拋出異常
                bscTransactionsService.processTransactionsAsBatch(transactions, walletMap);
            }

            // --- 步驟四：成功後更新區塊高度 ---
            // 只有在前面所有步驟都未拋出異常時，才會執行到這裡
            bizChainSyncStateService.updateLastSyncedBlock("BSC",bscScanConfig.getChainId(), endBlock);

        } catch (Exception e) {
            // 捕獲來自 bscTransactionsService 的批次處理異常
            log.error("BSC - [高效备用轮询] 处理交易批次时发生错误，整个批次已回滚。将在下次任务重试区块范围 {} -> {}", startBlock, endBlock, e);
            // 此處不執行任何操作，區塊同步高度將保持不變
        }
    }

}

