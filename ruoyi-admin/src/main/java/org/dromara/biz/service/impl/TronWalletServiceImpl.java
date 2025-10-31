//// ===================================================================================
//// 文件: TronWalletServiceImpl.java (已重构)
//// 路径: org/dromara/biz/service/impl/TronWalletServiceImpl.java
//// 描述: 已重构为扫描新的 biz_deposit_wallets 表，并为关联的用户进行充值。
//// ===================================================================================
//package org.dromara.biz.service.impl;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.dromara.biz.domain.bo.BizDepositWalletsBo;
//import org.dromara.biz.domain.bo.BizTransactionsBo;
//import org.dromara.biz.domain.vo.BizDepositWalletsVo;
//import org.dromara.biz.service.IBizDepositWalletsService;
//import org.dromara.biz.service.IBizTransactionsService;
//import org.dromara.biz.service.ITronWalletService;
//import org.dromara.common.core.exception.ServiceException;
//import org.dromara.system.service.ISysUserService;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class TronWalletServiceImpl implements ITronWalletService {
//
//    // 核心修改：注入新的充值钱包服务
//    private final IBizDepositWalletsService bizDepositWalletsService;
//    private final ISysUserService sysUserService;
//    private final IBizTransactionsService bizTransactionsService;
//
//    private static final OkHttpClient httpClient = new OkHttpClient();
//    private static final Gson gson = new Gson();
//
//    // Shasta 测试网
//    private static final String TRC20_API_URL = "https://api.trongrid.io/v1/accounts/%s/transactions/trc20?limit=50&only_to=true";
//    private static final String USDT_CONTRACT_ADDRESS = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
//    private static final String myApiKey ="db01eb04-a6e9-4f1a-b9d1-8adcd7e432b1";
//
//    private static final BigDecimal USDT_DECIMALS = new BigDecimal("1000000");
//
//    @Override
//    public void scanAllWallets() {
//        // 核心修改：从新的充值钱包表中获取所有启用的钱包
//        BizDepositWalletsBo queryBo = new BizDepositWalletsBo();
//        queryBo.setStatus("active");
//        List<BizDepositWalletsVo> wallets = bizDepositWalletsService.queryList(queryBo);
//
//        if (wallets.isEmpty()) {
//            log.info("数据库中没有需要扫描的充值钱包地址。");
//            return;
//        }
//
//        log.info("开始扫描 {} 个充值钱包地址...", wallets.size());
//        for (BizDepositWalletsVo wallet : wallets) {
//            // 确保钱包有关联的用户ID，否则无法充值
//            if (wallet.getUserId() == null) {
//                log.warn("充值钱包ID {} [{}] 没有关联的用户ID，跳过扫描。", wallet.getWalletId(), wallet.getWalletAddress());
//                continue;
//            }
//            scanAddressForTrc20(wallet);
//        }
//        log.info("所有充值钱包地址扫描完毕。");
//    }
//
//    /**
//     * 扫描TRC-20代币交易 (如USDT)
//     */
//    private void scanAddressForTrc20(BizDepositWalletsVo wallet) {
//        String url = String.format(TRC20_API_URL, wallet.getWalletAddress());
//        Request request = new Request.Builder().url(url).header("Accept", "application/json").header("TRON-PRO-API-KEY", myApiKey).build();
//
//        try (Response response = httpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                log.error("请求TRC20 API失败 for address {}: {}", wallet.getWalletAddress(), response);
//                return;
//            }
//
//            JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
//            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
//                for (JsonElement txElement : jsonObject.getAsJsonArray("data")) {
//                    processTrc20Transaction(txElement.getAsJsonObject(), wallet);
//                }
//            }
//        } catch (IOException e) {
//            log.error("扫描TRC20交易时发生网络错误 for address {}: {}", wallet.getWalletAddress(), e.getMessage());
//        }
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public void processTrc20Transaction(JsonObject trc20Tx, BizDepositWalletsVo wallet) {
//        String txID = trc20Tx.get("transaction_id").getAsString();
//        if (bizTransactionsService.isTransactionProcessed(txID)) return;
//
//        String contractAddress = trc20Tx.getAsJsonObject("token_info").get("address").getAsString();
//        // 只处理 USDT 的交易
//        if (USDT_CONTRACT_ADDRESS.equalsIgnoreCase(contractAddress)) {
//            BigDecimal amount = new BigDecimal(trc20Tx.get("value").getAsString()).divide(USDT_DECIMALS, 6, RoundingMode.DOWN);
//            creditUserBalance(txID, wallet, trc20Tx.get("from").getAsString(), trc20Tx.get("to").getAsString(), amount, "USDT");
//        }
//    }
//
//    /**
//     * 为用户增加余额并创建流水记录
//     */
//    private void creditUserBalance(String txID, BizDepositWalletsVo wallet, String fromAddress, String toAddress, BigDecimal amount, String currency) {
//        // 核心修改：使用从 biz_deposit_wallets 表中获取的 userId
//        Long userId = wallet.getUserId();
//
//        BizTransactionsBo txBo = new BizTransactionsBo();
//        // 1. 创建交易记录
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
//        // 2. 为用户增加余额
//        boolean success = sysUserService.addBalance(userId, amount);
//        if (!success) {
//            // 抛出异常以触发事务回滚
//            throw new ServiceException("为用户 " + userId + " 增加余额失败！");
//        }
//
//        log.info("[{} 充值] 成功处理一笔充值！ txID: {}, 用户ID: {}, 金额: {}", currency, txID, userId, amount);
//    }
//}
