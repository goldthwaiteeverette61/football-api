-- 文件：order-cancellation-failure-setup.sql
-- 說明：此腳本為【取消失敗】場景準備數據，插入一筆關聯到不存在用戶的超時訂單。

-- 清理潛在的舊測試數據
DELETE FROM biz_bet_orders WHERE user_id IN (2, 999);
DELETE FROM biz_bet_order_details WHERE order_id IN (SELECT order_id FROM biz_bet_orders WHERE user_id IN (2, 999));
DELETE FROM biz_transactions WHERE user_id IN (2, 999);

-- 插入一筆【註定會取消失敗】的訂單：
-- 訂單本身已超時且狀態為 draft，但關聯的 user_id = 999 在 sys_user 表中不存在。
-- 這將導致 cancelOrderAndRefund 方法在執行 sysUserService.addBalance 時失敗，從而觸發事務回滾。
INSERT INTO biz_bet_orders (order_id, user_id, user_name, bet_amount, combination_type, status, expiration_time, create_time, update_time)
VALUES (101, 999, 'ghost_user', 50.00, '1x1', 'draft', DATEADD('HOUR', -1, NOW()), NOW(), NOW());

