package org.dromara.biz.service;

public interface IBizRewardService {

    String generateCompensationCode();

    /**
     * 为当前登录用户处理理赔金领取逻辑。
     * 业务点 1: 用户只统计自己的连输8次来判决自己领取理赔金的资格。
     */
    void claimRewardForCurrentUser(String payPassword);

    /**
     * 为当前登录用户重置连输进度。
     * 业务点 2: 用户可以重置自己的连输次数。
     */
    void resetConsecutiveLossesForCurrentUser(String payPassword);
}
