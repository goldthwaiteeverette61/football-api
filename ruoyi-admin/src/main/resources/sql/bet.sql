-- 測試初始化腳本
-- 該腳本會在每個測試方法執行前運行，以準備一個乾淨的、包含基礎數據的環境。
-- @Transactional 註解會確保測試結束後，所有數據（包括這裡插入的）都會被回滾。

-- 插入系統配置
-- 基礎投注額: 10
-- 佣金抽成比例: 0.5 (50%)
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`) VALUES
                                                                                                       (100, '基礎投注額', 'sys.biz.baseBetAmount', '10', 'Y'),
                                                                                                       (101, '獎勵佣金比例', 'sys.biz.rewardPersent', '0.5', 'Y');

-- 插入一個測試用戶 (並給予足夠的初始餘額)
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `balance`) VALUES (2, 'testuser', '測試用戶A', 1000.00);

-- 1. 首先，插入一個用於測試的聯賽 (假設 league_id = 99)
-- 確保 biz_leagues 表存在 (由 schema.sql 建立)
INSERT INTO `biz_leagues` (`league_id`, `name`) VALUES (99, '測試聯賽');

-- 2. 接著，在插入比賽數據時，明確指定 league_id
-- (這裡是修改您現有的 INSERT 語句)
INSERT INTO `biz_matches` (`match_id`, `league_id`, `home_team_name`, `away_team_name`, `status`) VALUES
                                                                                                      (101, 99, '主隊A', '客隊A', 'Scheduled'),
                                                                                                      (102, 99, '主隊B', '客隊B', 'Scheduled'),
                                                                                                      (103, 99, '主隊C', '客隊C', 'Scheduled');
