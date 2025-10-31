-- 清理旧的测试数据
DELETE FROM biz_withdrawals WHERE user_id = 4;
DELETE FROM biz_transactions WHERE user_id = 4 AND transaction_type = 'WITHDRAWAL';
DELETE FROM sys_user WHERE user_id = 4;
DELETE FROM sys_config WHERE config_key = 'withdrawalFee';

-- 1. 创建一个用于测试提现的用户
--    - user_id = 4
--    - user_name = 'withdrawalUser'
--    - balance = 1000.00 (初始余额)
--    - pay_password = '123456' (BCrypt 加密后)
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `balance`, `pay_password`, `pay_password_seted`)
VALUES
    (4, 103, 'withdrawalUser', '提现测试员', 'app_user', 'withdraw@example.com', '13800000004', '0', null, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.BtvMmM/31N3Hu/GNTCuhUrS', '0', '0', '127.0.0.1', NOW(), 1, NOW(), 1, NOW(), '用于提现功能整合测试的用户', 1000.00, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.BtvMmM/31N3Hu/GNTCuhUrS', 1);

-- 2. 插入提现手续费的系统配置项
--    - config_key = 'withdrawalFee'
--    - config_value = '1.50' (用于测试的固定手续费)
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
VALUES
    (999, '提现手续费', 'withdrawalFee', '1.50', 'Y', 1, NOW(), '用于整合测试的提现手续费');
