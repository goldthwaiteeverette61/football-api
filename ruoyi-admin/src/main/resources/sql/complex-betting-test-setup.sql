-- 清理潛在的舊測試數據
DELETE FROM biz_bet_orders WHERE user_id = 2;
DELETE FROM biz_bet_order_details WHERE order_id IN (SELECT order_id FROM biz_bet_orders WHERE user_id = 2);
DELETE FROM biz_match_results WHERE match_id IN (3543615, 3715685);
DELETE FROM biz_transactions WHERE user_id = 2;

-- 為測試用戶 (ID=2) 設置初始餘額
UPDATE sys_user SET balance = 1000.00 WHERE user_id = 2;

-- 插入用於結算的比賽結果
-- 賽果定義:
-- 比賽1 (3543615): 主勝 (H), 比分 (CRS) 3:1
-- 比賽2 (3715685): 主勝 (H)
INSERT INTO biz_match_results (match_id, pool_code, combination, is_settled) VALUES (3543615, 'HAD', 'H', 0);
INSERT INTO biz_match_results (match_id, pool_code, combination, is_settled) VALUES (3543615, 'CRS', '3:1', 0);
INSERT INTO biz_match_results (match_id, pool_code, combination, is_settled) VALUES (3715685, 'HAD', 'H', 0);

