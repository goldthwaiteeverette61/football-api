package org.dromara.biz.service;

import com.google.gson.JsonArray;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;

import java.util.Map;

/**
 * BSC 交易處理服務介面
 *
 * @author Lion Li
 */
public interface IBscTransactionsService {

    /**
     * 以事務方式處理整個交易批次
     *
     * @param transactions 從 API 獲取的交易數組
     * @param walletMap    系統內的錢包地址映射
     */
    void processTransactionsAsBatch(JsonArray transactions, Map<String, BizDepositWalletsVo> walletMap);

}

