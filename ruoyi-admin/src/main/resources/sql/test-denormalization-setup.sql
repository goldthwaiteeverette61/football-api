-- 反正規化（user_name 欄位）整合測試 - 初始化腳本

-- 1. 插入系統配置
MERGE INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`)
    KEY(`config_id`)
    VALUES
    (100, '基礎投注額', 'sys.biz.baseBetAmount', '10', 'Y'),
    (101, 'Double模式佣金比例', 'sys.biz.rewardPersent', '0.5', 'Y'),
    (102, 'Normal模式佣金比例', 'sys.biz.rewardPersentNormal', '0.0', 'Y'),
    (103, '连败理赔阈值', 'sys.biz.lossesThresholdForReward', '8', 'Y');

-- 2. 插入測試用戶 (ID=2) 和一個邀請人 (ID=3)
MERGE INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `balance`, `invitation_code`)
    KEY(`user_id`)
    VALUES
    (2, 'testuser', '測試用戶A', 1000.00, 'MYCODE123'),
    (3, 'inviter', '邀請人', 1000.00, 'INVITE321');

-- 3. 為用戶初始化進度記錄
MERGE INTO `biz_user_progress` (`user_id`, `bet_type`)
    KEY(`user_id`)
    VALUES
    (2, 'normal'),
    (3, 'normal');

-- 4. 插入用於投注流程的比賽和聯賽數據
MERGE INTO `biz_leagues` (`league_id`, `name`) KEY(`league_id`) VALUES (99, '測試聯賽');
MERGE INTO `biz_matches` (`match_id`, `league_id`, `home_team_name`, `away_team_name`, `status`)
    KEY(`match_id`)
    VALUES (101, 99, '主隊A', '客隊A', 'Scheduled');

-- 5. 準備用於註冊流程的可用資源
MERGE INTO `biz_invitation_codes` (`code_id`, `invitation_code`, `status`)
    KEY(`code_id`)
    VALUES (1, 'NEWCODE456', 'available');

-- 【核心修正】根據最新的表結構，補全了 NOT NULL 的 wallet_address 和 status 欄位。
MERGE INTO `biz_deposit_wallets` (`wallet_id`, `wallet_address`, `status`)
    KEY(`wallet_id`)
    VALUES (1, 'TEST_WALLET_ADDRESS_123', 'active');

-- 【核心修正】移除了不存在的 status 欄位，以匹配您提供的 sys_dict_type 表結構
MERGE INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`)
    KEY(`dict_id`)
    VALUES(100, '用戶默認頭像', 'default_user_logo');

MERGE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `is_default`)
    KEY(`dict_code`)
    VALUES (1000, 0, '默認頭像1', '1962723770180399106', 'default_user_logo', 'Y');



