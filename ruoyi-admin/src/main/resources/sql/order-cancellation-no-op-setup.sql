-- 文件：order-cancellation-no-op-setup.sql
-- 說明：此腳本為邊界場景測試準備數據，確保資料庫中沒有任何符合自動取消條件的訂單。

-- 清理潛在的舊測試數據
DELETE FROM biz_bet_orders WHERE user_id = 2;
DELETE FROM biz_bet_order_details WHERE order_id IN (SELECT order_id FROM biz_bet_orders WHERE user_id = 2);
DELETE FROM biz_transactions WHERE user_id = 2;

-- 為測試用戶 (ID=2) 設置初始餘額
UPDATE sys_user SET balance = 1000.00 WHERE user_id = 2;

-- 插入兩筆【不應被取消】的訂單：
-- 1. 一筆未過期的 DRAFT 訂單 (過期時間在未來1小時)
INSERT INTO biz_bet_orders (order_id, user_id, user_name, bet_amount, combination_type, status, expiration_time, create_time, update_time)
VALUES (102, 2, 'test_user_2', 100.00, '1x1', 'draft', NOW() + INTERVAL '1' HOUR, NOW(), NOW());

-- 2. 一筆已過期但狀態為 PENDING 的訂單 (過期時間在1小時前)
INSERT INTO biz_bet_orders (order_id, user_id, user_name, bet_amount, combination_type, status, expiration_time, create_time, update_time)
VALUES (103, 2, 'test_user_2', 200.00, '1x1', 'pending', NOW() - INTERVAL '1' HOUR, NOW(), NOW());

