-- 清理旧数据
DELETE FROM biz_scheme_periods;

-- 插入三条不同状态的测试数据
-- 最近的一条是 pending
-- 已开奖记录中，ID最大的是 102
INSERT INTO biz_scheme_periods (period_id, name, status, lost_streak_since_last_win) VALUES
                                                                                         (101, '历史已开奖-输', 'lost', 3),
                                                                                         (102, '历史已开奖-赢', 'won', 0),
                                                                                         (103, '当前待开奖', 'pending', 0);
