package org.dromara.biz.service;


import java.io.IOException;

public interface IBscWalletService {

    /**
     * 扫描所有已启用的 BSC 充值钱包，并处理新的充值交易。
     */
    void scanAllWallets();

//    long getLatestBlockNumber() throws IOException;
}
