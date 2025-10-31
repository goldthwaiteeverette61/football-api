-- 清理舊的測試資料，確保測試環境乾淨
DELETE FROM sys_user WHERE user_id = 100 OR user_name = 'testUser';
DELETE FROM biz_invitation_codes WHERE invitation_code = 'NEWCODE999';

-- 1. 建立一位邀請人 (Inviter)
--    - user_id = 100
--    - invitation_code = 'INVITE100'
--    這位使用者是 `register_shouldSucceed_whenUsingValidInvitationCode` 測試案例中有效的邀請碼提供者。
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `invitation_code`, `inviter_id`)
VALUES
    (100, 103, 'inviterUser', '我是邀請人', 'app_user', 'inviter@example.com', '13800000100', '0', null, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.BtvMmM/31N3Hu/GNTCuhUrS', '0', '0', '127.0.0.1', NOW(), 1, NOW(), 1, NOW(), '用於測試的邀請人帳號', 'INVITE100', 0);


-- 2. 準備一個可供新使用者分配的邀請碼 (根據新的 biz_invitation_codes 表結構)
--    - invitation_code = 'NEWCODE999'
--    - status = 'available' (可用)
--    當 `register_shouldSucceed_whenUsingValidInvitationCode` 測試中的新使用者註冊成功時，
--    系統應將此邀請碼分配給他。
INSERT INTO `biz_invitation_codes` (`invitation_code`, `status`, `create_by`, `create_time`)
VALUES
    ('NEWCODE999', 'available', 1, NOW());

