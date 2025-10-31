-- 清理旧的测试数据
DELETE FROM biz_user_follows WHERE user_id IN (2, 3);
DELETE FROM biz_user_progress WHERE user_id IN (2, 3);
DELETE FROM sys_user WHERE user_id IN (2, 3);
DELETE FROM biz_scheme_periods WHERE period_id = 101;
DELETE FROM sys_config WHERE config_key IN ('sys.biz.rewardPersentNormal', 'sys.biz.baseBetAmount');

-- 1. 创建用于测试的用户 A 和 B
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `balance`)
VALUES
    (2, 103, 'userA', '用户A', 'app_user', 1000.00),
    (3, 103, 'userB', '用户B', 'app_user', 1000.00);

-- 2. 为用户 A 和 B 创建投注进度记录
INSERT INTO `biz_user_progress` (`user_id`, `user_name`, `bet_type`)
VALUES
    (2, 'userA', 'normal'),
    (3, 'userB', 'normal');

-- 3. 创建一个有限额的方案期数
--    - limit_amount = 100.00 (本期总共最多只能投100元)
INSERT INTO `biz_scheme_periods` (`period_id`, `name`, `status`, `limit_amount`, `accumulated_amount`)
VALUES
    (101, '限额测试方案', 'pending', 100.00, 0.00);

-- 4. 插入必要的系统配置项
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`)
VALUES
    (1001, '普通返佣比例', 'sys.biz.rewardPersentNormal', '0.01', 'Y'),
    -- 【新增】为测试添加基础投注额配置
    (1002, '基础投注额', 'sys.biz.baseBetAmount', '10.00', 'Y');

