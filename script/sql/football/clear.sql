truncate table biz_matches;
truncate table biz_match_results;

truncate table biz_teams;
truncate table biz_leagues;
truncate table biz_sports;

truncate table biz_schemes;
truncate table biz_scheme_periods;
truncate table biz_scheme_period_details;

truncate table biz_user_follows;
truncate table biz_odds;
# truncate table
# truncate table


UPDATE football.biz_scheme_periods t SET t.status = 'pending' WHERE t.period_id = 1952632108452159489
UPDATE football.biz_user_follows t SET t.status = 'bought' WHERE t.follow_id = 1952988776528728066




truncate table biz_user_follows;
truncate table biz_scheme_periods;
truncate table biz_scheme_period_details;
truncate table biz_system_reserve;
UPDATE football.biz_system_reserve_summary t SET t.total_reserve_amount = 0.00 WHERE t.summary_id = 1;
truncate table biz_user_progress;
UPDATE football.sys_user t SET t.balance = 1000.00 WHERE t.user_id = 1;
