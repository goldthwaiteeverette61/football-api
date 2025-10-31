-- 清理潛在的舊測試數據
DELETE FROM biz_bet_orders WHERE user_id = 2;
DELETE FROM biz_bet_order_details WHERE order_id IN (101, 102, 103);
DELETE FROM biz_transactions WHERE user_id = 2;

-- 為測試用戶 (ID=2) 設置一個基準餘額
-- 注意：用戶在下注時已被扣款，所以這裡設置為 1000，方便計算退款
UPDATE sys_user SET balance = 1000.00 WHERE user_id = 2;

-- 插入三筆用於測試的訂單
-- 1. 一筆 DRAFT 狀態且已過期的訂單 (應該被取消)
INSERT INTO biz_bet_orders (order_id, user_id, user_name, bet_amount, combination_type, status, expiration_time, create_time)
VALUES (101, 2, 'test_user_2', 50.00, '1x1', 'draft', NOW() - INTERVAL '1' MINUTE, NOW() - INTERVAL '10' MINUTE);

-- 2. 一筆 DRAFT 狀態但未過期的訂單 (不應該被取消)
INSERT INTO biz_bet_orders (order_id, user_id, user_name, bet_amount, combination_type, status, expiration_time, create_time)
VALUES (102, 2, 'test_user_2', 20.00, '1x1', 'draft', NOW() + INTERVAL '10' MINUTE, NOW() - INTERVAL '1' MINUTE);

-- 3. 一筆 PENDING 狀態且已過期的訂單 (不應該被取消，因為狀態不是 draft)
INSERT INTO biz_bet_orders (order_id, user_id, user_name, bet_amount, combination_type, status, expiration_time, create_time)
VALUES (103, 2, 'test_user_2', 30.00, '2x1', 'pending', NOW() - INTERVAL '1' MINUTE, NOW() - INTERVAL '20' MINUTE);
