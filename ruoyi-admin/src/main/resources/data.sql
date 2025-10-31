-- 2. 插入測試用戶
-- 【核心修改】為測試用戶設定一個預設的、經過 BCrypt 加密的支付密碼 (明文為: password123)
-- 同時將 pay_password_seted 設為 1
MERGE INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `balance`, `pay_password`, `pay_password_seted`)
    KEY(`user_id`)
    VALUES (2, 'testuser', '理賠測試用戶', 1000.00, '$2a$10$rA3BckgDN7WgwnaIbKDOTOSet3XSL4WpasOZ0zqYf6Ry7NR7kt2Pu', 1);
