//package org.dromara.biz.service.impl;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import io.reactivex.disposables.Disposable;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dromara.biz.config.BscScanConfig;
//import org.dromara.biz.domain.bo.BizDepositWalletsBo;
//import org.dromara.biz.domain.bo.BizTransactionsBo;
//import org.dromara.biz.domain.vo.BizDepositWalletsVo;
//import org.dromara.biz.service.IBizChainSyncStateService;
//import org.dromara.biz.service.IBizDepositWalletsService;
//import org.dromara.biz.service.IBizTransactionsService;
//import org.dromara.common.core.exception.ServiceException;
//import org.dromara.system.service.ISysUserService;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.web3j.abi.EventEncoder;
//import org.web3j.abi.FunctionReturnDecoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.Event;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.request.EthFilter;
//import org.web3j.protocol.websocket.WebSocketService;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.net.ConnectException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * BSC 链实时订阅服务
// * @description: 通过 WebSocket 订阅 USDT 合约的 Transfer 事件，实现即时充值監聽。
// */
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class BscSubscriptionService {
//
//    private final IBizDepositWalletsService bizDepositWalletsService;
//    private final ISysUserService sysUserService;
//    private final IBizTransactionsService bizTransactionsService;
//    private final BscScanConfig bscConfig;
//
//    private Web3j web3j;
//    private Disposable subscription;
//    private WebSocketService wsService;
//    private static final int USDT_DECIMALS = 18; // BEP-20 USDT 是 18 位小数
//
//    @PostConstruct
//    public void init() {
//        // 在新线程中执行，避免阻塞主程序启动
//        new Thread(this::connectAndSubscribe).start();
//    }
//
//    private void connectAndSubscribe() {
//        try {
//            wsService = new WebSocketService(bscConfig.getWssUrl(), true);
//            wsService.connect();
//            web3j = Web3j.build(wsService);
//            log.info("BSC - [实时订阅] 成功连接到 WebSocket 节点: {}", bscConfig.getChainId());
//            subscribeToTransfers();
//        } catch (ConnectException e) {
//            log.error("BSC - [实时订阅] 无法连接到 WebSocket 节点: {}. 将在1分钟后重试。", bscConfig.getWssUrl(), e);
//            reconnect();
//        } catch (Exception e) {
//            log.error("BSC - [实时订阅] 初始化时发生未知错误，将在1分钟后重试。", e);
//            reconnect();
//        }
//    }
//
//    private void subscribeToTransfers() {
//        Event transferEvent = new Event("Transfer", Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>(false) {}));
//        EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, bscConfig.getUsdtContractAddress());
//        filter.addSingleTopic(EventEncoder.encode(transferEvent));
//
//        log.info("BSC - [实时订阅] 开始订阅 USDT 合约 [{}] 的转账事件...", bscConfig.getUsdtContractAddress());
//
//        subscription = web3j.ethLogFlowable(filter).subscribe(logMsg -> {
//            try {
//                String txHash = logMsg.getTransactionHash();
//                List<String> topics = logMsg.getTopics();
//                if (topics.size() < 3) return;
//
//                String fromAddress = "0x" + topics.get(1).substring(26);
//                String toAddress = "0x" + topics.get(2).substring(26);
//                List<Type> nonIndexedArgs = FunctionReturnDecoder.decode(logMsg.getData(), transferEvent.getNonIndexedParameters());
//                BigDecimal amount = new BigDecimal(((Uint256) nonIndexedArgs.get(0)).getValue())
//                    .divide(BigDecimal.TEN.pow(USDT_DECIMALS), 6, RoundingMode.DOWN);
//
//                BizDepositWalletsBo queryBo = new BizDepositWalletsBo();
//                queryBo.setWalletAddress(toAddress);
//                queryBo.setStatus("active");
//                List<BizDepositWalletsVo> wallets = bizDepositWalletsService.queryList(queryBo);
//
//                if (!wallets.isEmpty()) {
//                    log.info("BSC - [实时订阅] 接收到一笔转账！TxHash: {}, To: {}, Amount: {}", txHash, toAddress, amount);
//                    processDeposit(txHash, fromAddress, toAddress, amount, "USDT-BEP20", wallets.get(0));
//                }
//            } catch (Exception e) {
//                log.error("BSC - [实时订阅] 处理事件时出错: {}", logMsg, e);
//            }
//        }, error -> {
//            log.error("BSC - [实时订阅] 订阅时发生错误，将在一分钟后尝试重新连接...", error);
//            reconnect();
//        });
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public void processDeposit(String txID, String fromAddress, String toAddress, BigDecimal amount, String currency, BizDepositWalletsVo wallet) {
//        if (bizTransactionsService.isTransactionProcessed(txID)) {
//            log.warn("BSC - 交易 {} 已被处理过，跳过。", txID);
//            return;
//        }
//
//        Long userId = wallet.getUserId();
//        if (userId == null) {
//            log.error("BSC - 钱包地址 {} 未关联用户，无法完成充值！", toAddress);
//            return;
//        }
//
//        BizTransactionsBo txBo = new BizTransactionsBo();
//        txBo.setTransactionHash(txID);
//        txBo.setUserId(userId);
//        txBo.setFromAddress(fromAddress);
//        txBo.setToAddress(toAddress);
//        txBo.setAmount(amount);
//        txBo.setUserName(wallet.getUserName());
//        txBo.setCurrency(currency);
//        txBo.setStatus("CONFIRMED");
//        txBo.setTransactionType("RECHARGE");
//        bizTransactionsService.insertByBo(txBo);
//
//        boolean success = sysUserService.addBalance(userId, amount);
//        if (!success) {
//            throw new ServiceException("为用户 " + userId + " 增加余额失败！交易哈希: " + txID);
//        }
//        log.info("BSC - [入账成功] [{}] 充值！ txID: {}, 用户ID: {}, 金额: {}", currency, txID, userId, amount);
//    }
//
//    private void reconnect() {
//        cleanup();
//        try {
//            TimeUnit.MINUTES.sleep(1);
//            connectAndSubscribe();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    @PreDestroy
//    public void cleanup() {
//        log.info("正在关闭 BSC WebSocket 订阅...");
//        if (subscription != null && !subscription.isDisposed()) {
//            subscription.dispose();
//        }
//        if (wsService != null) {
//            try {
//                wsService.close();
//            } catch (Exception e) {
//                log.error("关闭 WebSocket 服务时出错", e);
//            }
//        }
//    }
//}
//
