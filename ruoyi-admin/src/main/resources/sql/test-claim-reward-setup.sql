-- 理賠金整合測試 - 初始化腳本

-- 1. 插入系統配置
MERGE INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`)
    KEY(`config_id`)
    VALUES
    (103, '连败理赔阈值', 'sys.biz.lossesThresholdForReward', '8', 'Y'),
    (103, '提现费用', 'sys.biz.withdrawalFee', '1', 'Y'),
    (104, '理赔金比例', 'sys.biz.claimRewardRate', '0.1', 'Y');


-- 2. 插入測試用戶
-- 【核心修改】為測試用戶設定一個預設的、經過 BCrypt 加密的支付密碼 (明文為: password123)
-- 同時將 pay_password_seted 設為 1
MERGE INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `balance`, `pay_password`, `pay_password_seted`)
    KEY(`user_id`)
    VALUES (2, 'testuser', '理賠測試用戶', 1000.00, '$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Pu', 1);

-- 3. 為測試用戶初始化進度記錄
MERGE INTO `biz_user_progress` (`user_id`, `bet_type`, `consecutive_losses`, `consecutive_losses_amount`, `can_claim_reward`, `bet_amount`, `last_bet_amount`, `commission_rate`)
    KEY(`user_id`)
    VALUES (2, 'normal', 0, 0.00, 0, 0.00, 0.00, 0.00);

