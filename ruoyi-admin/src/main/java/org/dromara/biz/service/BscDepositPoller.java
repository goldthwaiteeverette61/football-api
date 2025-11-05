package org.dromara.biz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.config.BscScanConfig;
import org.dromara.biz.domain.bo.BizDepositWalletsBo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.web3j.tx.Contract.staticExtractEventParameters;


@Slf4j
@RequiredArgsConstructor
@Service
public class BscDepositPoller {

    // ... (配置区、注入、Event 定义都保持不变) ...
    // ==================== 配置区 (优化为 static final) ====================
    // 2. 移除了硬编码的钱包列表
    // private static final Set<String> WALLET_ADDRESSES = Set.of("0xBA0e02dD1610F717fb1ab8E5Ff3d2c5FD5306ef5".toLowerCase());
    // private static final int BLOCK_RANGE = 20; // 3. 移除硬编码，改用 scanSpan
    // 3. 移除了硬编码的合约地址
    // private static final String TOKEN_CONTRACT = "0xF761eA5f045eCB07d2bB57ce6C87D98d9FE4EAc0".toLowerCase();
    // ================================================

    // 4. 注入 SysConfigService
    private final ISysConfigService iSysConfigService;

    private final Web3j web3j;
    // 4. 注入 Service 和 Config
    private final IBizDepositWalletsService bizDepositWalletsService;
    private final BscScanConfig bscScanConfig;
    // 2. 注入业务逻辑所需的 Service
    private final IBizTransactionsService bizTransactionsService;
    private final ISysUserService sysUserService;
    // 注入区块状态管理服务
    private final IBizChainSyncStateService bizChainSyncStateService;

    // 移除了内存中的 lastProcessedBlock
    // private volatile BigInteger lastProcessedBlock = null;

    // ==================== ERC-20 Transfer Event (优化为 static final) ====================
    private static final Event TRANSFER_EVENT = new Event("Transfer",
        Arrays.asList(
            new TypeReference<Address>(true) { }, // indexed _from
            new TypeReference<Address>(true) { }, // indexed _to
            new TypeReference<Uint256>(false) { }  // non-indexed _value
        )
    );
    private static final String TRANSFER_EVENT_HASH = EventEncoder.encode(TRANSFER_EVENT);
    // ======================================================================

    /**
     * 6. 移除了 @PostConstruct 初始化
     */
    // @PostConstruct
    // public void initialize() { ... }

    /**
     * 8. 核心优化：移除 @Scheduled，改回普通 public 方法
     * (原名 scheduledPollNewBlocks)
     * 7. 增加 @Transactional，确保批处理的事务性
     */
    @Transactional(rollbackFor = Exception.class) // <-- 事务注解
    public void pollNewBlocks() { // <-- 方法名改回 pollNewBlocks

        // 5. 使用 scanSpan
        long scanSpan = Long.parseLong(iSysConfigService.selectConfigByKey("bsc.scanSpan"));

        // 8. 使用数据库持久化区块状态 (参考 BscWalletServiceImpl)
        long lastSyncedBlock = bizChainSyncStateService.getLastSyncedBlock("BSC", bscScanConfig.getChainId());
        long startBlock = lastSyncedBlock < 0 ? 0 : lastSyncedBlock + 1;

        // ================== 1. (非事务性) RPC 数据获取 ==================
        long endBlock;
        EthLog ethLog;
        long pollStartTime = System.currentTimeMillis(); // 日志时间

        try {
            BigInteger latest = web3j.ethBlockNumber().send().getBlockNumber().subtract(BigInteger.valueOf(30));

            // 9. 使用 long 类型的区块号进行计算
            endBlock = latest.longValue(); // 扫描到最新区块
            // 6. 使用 scanSpan 替换 BLOCK_RANGE
            endBlock = Math.min(endBlock, startBlock + scanSpan - 1); // 确保不超过 scanSpan

            if (startBlock > endBlock) {
                log.info("[日志] ----> 没有新区块 (from: {}, to: {}), 跳过", startBlock, endBlock);
                return; // 正常退出，没有新块，无需更新
            }

            log.info("\n[日志] 扫描区块范围: {} ~ {}(最新{}，偏差{})", startBlock, endBlock,latest,(latest.longValue() - endBlock));

            // ==================== 核心修改: 使用 ethGetLogs (网络操作) ====================

            // 2. 创建过滤器 (使用 bscScanConfig)
            EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)), // 10. 使用 long 转换
                DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)),   // 10. 使用 long 转换
                bscScanConfig.getUsdtContractAddress() // 6. 使用配置中的合约地址
            );

            // 3. 添加 Topics
            filter.addSingleTopic(TRANSFER_EVENT_HASH); // Topic 0: 必须是 Transfer 事件
            filter.addNullTopic();                      // Topic 1: (from) 任意发送者
            // filter.addOptionalTopics(paddedAddresses); // 4. (已移除) 不再通过 RPC 过滤 'to' 地址

            long rpcCallStart = System.currentTimeMillis();
            ethLog = web3j.ethGetLogs(filter).send(); // <-- 执行 RPC 调用
            long rpcCallEnd = System.currentTimeMillis();

        } catch (Exception e) {
            // 16. 捕获所有 RPC 和设置异常
            // 发生异常时，不更新 lastSyncedBlock，以便下次重试
            log.error("[日志] 轮询时获取网络数据异常，区块范围 {}->{} 将在下次任务重试: {}", startBlock, (startBlock + scanSpan - 1), e.getMessage(), e);
            return; // 退出，不执行事务逻辑
        }

        // ================== 2. (事务性) 数据库处理 ==================
        // ** 此处代码不再被 try...catch 包裹 **
        // 任何 ServiceException 都会上浮并触发 @Transactional 回滚

        // ==================== 5. 从数据库动态获取钱包 (事务内) ====================
        BizDepositWalletsBo queryBo = new BizDepositWalletsBo();
        queryBo.setStatus("active"); // 只扫描激活的钱包
        List<BizDepositWalletsVo> wallets = bizDepositWalletsService.queryList(queryBo);

        if (wallets == null || wallets.isEmpty()) {
            log.info("[日志] ----> 数据库中没有需要扫描的充值钱包地址。");
            // 关键修复：我们成功扫描了[startBlock, endBlock]但发现没钱包，
            // 必须更新区块高度，否则会卡住。
            bizChainSyncStateService.updateLastSyncedBlock("BSC", bscScanConfig.getChainId(), endBlock);
            return;
        }

        // 将钱包列表转为 Map，方便快速查找
        Map<String, BizDepositWalletsVo> walletMap = wallets.stream()
            .collect(Collectors.toMap(w -> w.getWalletAddress().toLowerCase(), w -> w));
        // ==========================================================


        List<EthLog.LogResult> logs = ethLog.getLogs();
        if (logs == null || logs.isEmpty()) {
//            log.info("[日志] --------> 在此范围未发现相关日志");
            // 11. 即使没有日志，也要更新区块 (参考 BscWalletServiceImpl)
            // 这是此区块范围的唯一“写”操作，是事务性的
            bizChainSyncStateService.updateLastSyncedBlock("BSC", bscScanConfig.getChainId(), endBlock);
            return;
        }

        log.info("[日志] --------> G发现 {} 条相关日志，开始处理...", logs.size());

        // ==================== 日志点 6: 本地交易处理耗时 ====================
        long txProcessStart = System.currentTimeMillis();
        int foundCount = 0;

        for (EthLog.LogResult<?> logResult : logs) {
            Log logData = ((EthLog.LogObject) logResult).get();

            if (logData.isRemoved()) {
                log.warn("[警告] 发现一条被移除的日志: {}", logData.getTransactionHash());
                continue;
            }

            // 12. ================== 开始移植业务逻辑 ==================
            // (参考 BscTransactionsServiceImpl)

            // === 优化后的顺序 ===
            // 1. 先解码 (本地操作)
            EventValues eventValues = staticExtractEventParameters(TRANSFER_EVENT, logData);
            Address fromAddrObj = (Address) eventValues.getIndexedValues().get(0);
            Address toAddrObj = (Address) eventValues.getIndexedValues().get(1);
            Uint256 valueObj = (Uint256) eventValues.getNonIndexedValues().get(0);

            String fromAddress = fromAddrObj.getValue();
            String toAddr = toAddrObj.getValue();
            BigInteger value = valueObj.getValue();

            // 2. 检查是否是我们的钱包 (内存操作，最快)
            // (这是方案 B 的核心，RPC 返回了所有日志，我们在这里过滤)
            BizDepositWalletsVo walletVo = walletMap.get(toAddr.toLowerCase());
            if (walletVo == null) {
                continue; // 不是我们的钱包，跳过
            }

            // 3. 获取 txID
            String txID = logData.getTransactionHash();

            // 4. 检查是否重复处理 (数据库操作，较慢)
            if (bizTransactionsService.isTransactionProcessed(txID)) {
                log.warn("[日志] 交易 {} 已被处理，跳过", txID);
                continue;
            }
            // === 顺序优化结束 ===

            // 12.3 金额计算 (参考 BscTransactionsServiceImpl)
            // 假设 18 位精度 (BEP20 USDT 通常是 18)
            int decimals = 18;
            BigDecimal divisor = BigDecimal.TEN.pow(decimals);
            BigDecimal amount = new BigDecimal(value).divide(divisor, 6, RoundingMode.DOWN);

            // 12.4 移植 creditUserBalance 逻辑
            Long userId = walletVo.getUserId();
            BizTransactionsBo txBo = new BizTransactionsBo();
            txBo.setTransactionHash(txID);
            txBo.setUserId(userId);
            txBo.setFromAddress(fromAddress);
            txBo.setToAddress(toAddr);
            txBo.setAmount(amount);
            txBo.setUserName(walletVo.getUserName());
            txBo.setCurrency("USDT-BEP20"); // 13. 硬编码 (参考 BscTransactionsServiceImpl)
            txBo.setStatus("CONFIRMED");
            txBo.setTransactionType("RECHARGE");
            bizTransactionsService.insertByBo(txBo); // 插入交易记录

            BizDepositWalletsBo bo = new BizDepositWalletsBo();
            bo.setWalletId(walletVo.getWalletId());
            bo.setHasBalance(1);
            bizDepositWalletsService.updateByBo(bo); // 更新钱包状态

            boolean success = sysUserService.addBalance(userId, amount); // 增加用户余额
            if (!success) {
                log.error("为用户 {} 增加余额失败！交易哈希: {}", userId, txID);
                // 抛出异常以触发整个 @Transactional 回滚
                throw new ServiceException("为用户 " + userId + " 增加余额失败！交易哈希: " + txID);
            }

            // 12.5 打印日志
            log.info("=========================================");
            log.info("  充值成功！ (已写入数据库)");
            log.info("  用户ID: {}", walletVo.getUserId());
            log.info("  地址: {}", toAddr);
            log.info("  金额: {}", amount);
            log.info("  区块: {}", logData.getBlockNumber());
            // 14. 修正 bscscan 链接 (移除 testnet)
            log.info("  Tx: https://bscscan.com/tx/{}", txID);
            log.info("=========================================");

            foundCount++;
            // 12. ================== 业务逻辑移植结束 ==================
        }

        long txProcessEnd = System.currentTimeMillis();
        if (foundCount > 0) {
            log.info("[日志] --------> 本地处理 {} 笔日志 (发现 {} 笔) 耗时: {} ms", logs.size(), foundCount, (txProcessEnd - txProcessStart));
        }
        // ==========================================================

        // 15. 成功处理完所有日志后，更新区块高度
        // 这是此事务的最后一步
        bizChainSyncStateService.updateLastSyncedBlock("BSC", bscScanConfig.getChainId(), endBlock);

        long pollEndTime = System.currentTimeMillis();
        // 修正总耗时计算
        log.info("[日志] ==> 轮询任务完成，总耗时: {} ms", (System.currentTimeMillis() - pollStartTime));
    }

    // 17. 移除了 creditUserBalance 方法，因为逻辑已被内联到 pollNewBlocks 中
    // 并由 pollNewBlocks 上的 @Transactional 统一管理。
}

