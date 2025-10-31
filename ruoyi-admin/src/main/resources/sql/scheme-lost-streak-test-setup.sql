-- 清理旧数据，确保测试环境纯净
DELETE FROM biz_user_follows;
DELETE FROM biz_user_progress;
DELETE FROM biz_scheme_periods;
DELETE FROM sys_user WHERE user_id IN (2);
DELETE FROM sys_config WHERE config_key = 'sys.biz.periodsLostCountLimt';

-- 插入一个有足够余额的测试用户
INSERT INTO sys_user (user_id, user_name, nick_name, password, balance, status)
VALUES (2, 'test_user_2', '测试用户', '$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Pu', 1000.00, '0');

-- 插入用户的投注进度记录
INSERT INTO biz_user_progress (progress_id, user_id, bet_type, consecutive_losses, consecutive_losses_amount, bet_amount, commission_rate)
VALUES (1, 2, 'normal', 0, 0.00, 10.00, 0.5);

-- 插入两个连黑次数不同的方案
-- 方案101: 连黑2次 (未达到门槛)
INSERT INTO biz_scheme_periods (period_id, name, status, deadline_time, limit_amount, accumulated_amount, lost_streak_since_last_win)
VALUES (101, '测试方案-连黑2期', 'pending', DATEADD('DAY', 1, NOW()), 1000.00, 0.00, 2);

-- 方案102: 连黑5次 (已达到门槛)
INSERT INTO biz_scheme_periods (period_id, name, status, deadline_time, limit_amount, accumulated_amount, lost_streak_since_last_win)
VALUES (102, '测试方案-连黑5期', 'pending', DATEADD('DAY', 1, NOW()), 1000.00, 0.00, 5);

-- 插入真实的系统配置，用于测试
INSERT INTO sys_config (config_id, config_name, config_key, config_value, config_type, remark)
VALUES (100, '方案连黑投注限制', 'sys.biz.periodsLostCountLimt', '5', 'N', '测试配置：要求方案连黑5期后方可投注');

