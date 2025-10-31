-- 清理旧的测试数据，确保测试环境干净
DELETE FROM biz_transactions WHERE user_id IN (2, 3);
DELETE FROM sys_user WHERE user_id IN (2, 3);

-- 1. 创建转账方 (fromUser)
--    - user_id = 2 (对应测试代码中的 FROM_USER_ID)
--    - user_name = 'fromUser'
--    - balance = 1000.00 (初始余额)
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `balance` ,`pay_password_seted`, `pay_password`)
VALUES
    (2, 103, 'fromUser', '转账方', 'app_user', 'from@example.com', '13800000002', '0', null, '$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Pu', '0', '0', '127.0.0.1', NOW(), 1, NOW(), 1, NOW(), '用于测试的转账方账户', 1000.00,1, '$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Pu');

-- 2. 创建收款方 (toUser)
--    - user_id = 3 (对应测试代码中的 TO_USER_ID)
--    - user_name = 'toUser'
--    - balance = 0.00 (初始余额)
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `balance`)
VALUES
    (3, 103, 'toUser', '收款方', 'app_user', 'to@example.com', '13800000003', '0', null, '$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Pu', '0', '0', '127.0.0.1', NOW(), 1, NOW(), 1, NOW(), '用于测试的收款方账户', 0.00);

