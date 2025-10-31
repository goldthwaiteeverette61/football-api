package org.dromara.biz.service;

public interface ITronWalletService {

    /**
     * 扫描所有已知的用户钱包地址以查找新的充值记录。
     */
    void scanAllWallets();

}
