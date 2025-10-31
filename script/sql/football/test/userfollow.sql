delete from biz_user_follows where period_id = 1959988305739505665



select * from biz_user_follows where period_id = 1959988305739505665
select * from biz_user_follow_details where period_id = 1959988305739505665
SELECT follow_id, user_id, period_id, bet_amount, status, payout_amount, created_at, payout_odds, bet_odds_desc,
       payout_odds_desc, result_status, combination_type
           update_time FROM biz_user_follows WHERE (period_id = 1959988305739505665) ORDER BY follow_id DESC
SELECT follow_id, user_id, period_id, bet_amount, status, payout_amount, created_at, payout_odds, bet_odds_desc,

       payout_odds_desc, result_status, combination_type
           update_time FROM biz_user_follows WHERE (period_id = 1959988305739505665) ORDER BY follow_id DESC


UPDATE football.biz_scheme_periods t SET t.status = 'pending' WHERE t.period_id = 1959988305739505665
UPDATE football.biz_user_follows t SET t.status = 'in_cart' WHERE t.follow_id = 1960201297564123138


delete from biz_user_follow_details where period_id = 1959988305739505665
delete from biz_user_follows where user_id = 1



create table football.biz_scheme_period_details_odds
(
    detail_odds_id   bigint auto_increment comment '详情ID, 主键'
        primary key,
    scheme_period_detail_id  bigint not null comment '所属期数ID (关联 biz_scheme_periods)',
    period_id   bigint                       not null comment '所属期数ID (关联 biz_scheme_periods)',
    odds        decimal(10, 2)               not null comment '选择时的赔率',
    user_id bigint                       null comment '用户id'
)
    comment '用户方案期数详情表' charset = utf8mb4;

create index idx_period_id
    on football.biz_scheme_period_details (period_id);


select * from biz_scheme_periods where period_id = 1960589678907236353
select * from biz_scheme_period_details where period_id = 1960589678907236353
select * from biz_user_follows where period_id = 1960589678907236353
select * from biz_user_follow_details where period_id = 1960589678907236353


delete from biz_scheme_periods where period_id = 1960589678907236353
delete from biz_scheme_period_details where period_id = 1960589678907236353
delete from biz_user_follows where period_id = 1960589678907236353
delete from biz_user_follow_details where period_id = 1960589678907236353
