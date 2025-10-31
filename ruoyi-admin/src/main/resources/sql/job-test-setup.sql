-- 基礎投注額: 10
-- 佣金抽成比例: 0.5 (50%)
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`) VALUES
                                                                                                       (100, '基礎投注額', 'sys.biz.baseBetAmount', '10', 'Y'),
                                                                                                       (101, '獎勵佣金比例', 'sys.biz.rewardPersent', '0.5', 'Y');
