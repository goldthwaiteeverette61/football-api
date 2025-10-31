-- 測試初始化腳本
-- 該腳本會在每個測試方法執行前運行，以準備一個乾淨的、包含基礎數據的環境。
-- @Transactional 註解會確保測試結束後，所有數據（包括這裡插入的）都會被回滾。

-- 1. 插入系統配置
-- 使用 MERGE INTO 以確保冪等性 (如果鍵已存在則更新，否則插入)
MERGE INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`)
    KEY(`config_id`)
    VALUES
    (100, '基礎投注額', 'sys.biz.baseBetAmount', '1', 'Y'),
    (101, 'Double模式佣金比例', 'sys.biz.rewardPersent', '0.5', 'Y'),
    (102, 'Normal模式佣金比例', 'sys.biz.rewardPersentNormal', '0.0', 'Y'),
    (103, '连败理赔阈值', 'sys.biz.lossesThresholdForReward', '8', 'Y'); -- 【核心修正】補上缺失的連敗理賠閾值設定


-- 為測試用戶初始化進度記錄
MERGE INTO `biz_user_progress` (`user_id`, `bet_type`, `consecutive_losses`, `consecutive_losses_amount`, `can_claim_reward`, `bet_amount`, `commission_rate`)
    KEY(`user_id`)
    VALUES (2, 'normal', 0, 0.00, 0, 0.00, 0.00);

-- 3. 插入用於測試的比賽基礎數據

-- 【核心修正】第一步：先插入一個用於測試的聯賽，確保比賽數據有關聯的聯賽
MERGE INTO `biz_leagues` (`league_id`, `name`)
    KEY(`league_id`)
    VALUES (99, '測試聯賽');

-- 【核心修正】第二步：在插入比賽數據時，明確指定 league_id
MERGE INTO `biz_matches` (`match_id`, `league_id`, `home_team_name`, `away_team_name`, `status`)
    KEY(`match_id`)
    VALUES
    (101, 99, '主隊A', '客隊A', 'Scheduled'),
    (102, 99, '主隊B', '客隊B', 'Scheduled'),
    (103, 99, '主隊C', '客隊C', 'Scheduled');

