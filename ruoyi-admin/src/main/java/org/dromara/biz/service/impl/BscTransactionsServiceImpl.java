package org.dromara.biz.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizDepositWalletsBo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.biz.service.IBizDepositWalletsService;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.biz.service.IBscTransactionsService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * BSC 交易處理服務實現類
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BscTransactionsServiceImpl implements IBscTransactionsService {

    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;
    private final IBizDepositWalletsService iBizDepositWalletsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processTransactionsAsBatch(JsonArray transactions, Map<String, BizDepositWalletsVo> walletMap) {
        for (JsonElement txElement : transactions) {
            JsonObject txObject = txElement.getAsJsonObject();
            processSingleTransaction(txObject, walletMap);
        }
    }

    /**
     * 處理單筆交易的內部邏輯
     */
    private void processSingleTransaction(JsonObject tx, Map<String, BizDepositWalletsVo> walletMap) {
        String toAddress = tx.get("to").getAsString();

        BizDepositWalletsVo wallet = walletMap.get(toAddress.toLowerCase());
        if (wallet == null) {
            return; // 這筆交易與我們無關
        }

        String txID = tx.get("hash").getAsString();
        if (bizTransactionsService.isTransactionProcessed(txID)) {
            return; // 防止重複處理
        }

        String fromAddress = tx.get("from").getAsString();
        String value = tx.get("value").getAsString();
        int decimals = tx.has("tokenDecimal") ? tx.get("tokenDecimal").getAsInt() : 18;
        BigDecimal divisor = BigDecimal.TEN.pow(decimals);
        BigDecimal amount = new BigDecimal(value).divide(divisor, 6, RoundingMode.DOWN);

        creditUserBalance(txID, wallet, fromAddress, toAddress, amount, "USDT-BEP20");
    }

    /**
     * 為用戶增加餘額並記錄流水的核心方法
     */
    private void creditUserBalance(String txID, BizDepositWalletsVo wallet, String fromAddress, String toAddress, BigDecimal amount, String currency) {
        Long userId = wallet.getUserId();
        BizTransactionsBo txBo = new BizTransactionsBo();
        txBo.setTransactionHash(txID);
        txBo.setUserId(userId);
        txBo.setFromAddress(fromAddress);
        txBo.setToAddress(toAddress);
        txBo.setAmount(amount);
        txBo.setUserName(wallet.getUserName());
        txBo.setCurrency(currency);
        txBo.setStatus("CONFIRMED");
        txBo.setTransactionType("RECHARGE");
        bizTransactionsService.insertByBo(txBo);

        BizDepositWalletsBo bo = new BizDepositWalletsBo();
        bo.setWalletId(wallet.getWalletId());
        bo.setHasBalance(1);
        iBizDepositWalletsService.updateByBo(bo);

        boolean success = sysUserService.addBalance(userId, amount);
        if (!success) {
            throw new ServiceException("为用户 " + userId + " 增加余额失败！交易哈希: " + txID);
        }
    }
}

