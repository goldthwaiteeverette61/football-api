-- 文件：order-lifecycle-setup.sql
-- 說明：此腳本為訂單生命週期相關的整合測試準備數據。

-- 清理潛在的舊測試數據
DELETE FROM biz_matches WHERE match_id IN (3543615, 3715685);
DELETE FROM biz_bet_orders WHERE user_id = 2;
DELETE FROM biz_bet_order_details WHERE order_id IN (SELECT order_id FROM biz_bet_orders WHERE user_id = 2);
DELETE FROM biz_match_results WHERE match_id IN (3543615, 3715685);
DELETE FROM biz_transactions WHERE user_id = 2;

-- 為測試用戶 (ID=2) 設置初始餘額
UPDATE sys_user SET balance = 1000.00 WHERE user_id = 2;

-- 【新增】插入用於測試的比賽基礎資訊，包含不同的比賽時間
INSERT INTO biz_matches (match_id, match_name, match_datetime, status) VALUES
                                                                           (3543615, '測試比賽 A', '2025-10-16 12:00:00', 'Pending'),
                                                                           (3715685, '測試比賽 B', '2025-10-17 10:00:00', 'Pending');

-- 插入用於【中獎】場景的比賽結果
INSERT INTO biz_match_results (match_id, pool_code, combination, is_settled) VALUES (3543615, 'HAD', 'H', 0);
INSERT INTO biz_match_results (match_id, pool_code, combination, is_settled) VALUES (3715685, 'TTG', '3', 0);

