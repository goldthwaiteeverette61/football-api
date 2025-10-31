CREATE TABLE `biz_users` (
                             `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                             `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，如 @ethan.carter',
                             `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
                             `password_hash` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
                             `full_name` VARCHAR(100) COMMENT '全名，如 Ethan Carter',
                             `profile_picture_url` VARCHAR(255) COMMENT '头像图片链接',
                             `balance` DECIMAL(15, 4) NOT NULL DEFAULT 0.0000 COMMENT '账户余额 (以USDT计价)',
                             `referral_code` VARCHAR(20) NOT NULL UNIQUE COMMENT '用户的专属推荐码',
                             `referred_by_user_id` INT UNSIGNED NULL COMMENT '推荐该用户的用户ID',
                             `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='用户信息表';

CREATE TABLE `biz_sports` (
                              `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                              `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '体育项目名称，如 Soccer'
) COMMENT='体育项目表';
CREATE TABLE `biz_leagues` (
                               `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               `sport_id` INT UNSIGNED NOT NULL,
                               `name` VARCHAR(100) NOT NULL COMMENT '联赛名称，如 Premier League',
                               FOREIGN KEY (`sport_id`) REFERENCES `biz_sports`(`id`)
) COMMENT='联赛信息表';
CREATE TABLE `biz_teams` (
                             `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                             `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '队伍名称',
                             `logo_url` VARCHAR(255) COMMENT '队徽图片链接'
) COMMENT='队伍信息表';

CREATE TABLE `biz_matches` (
                               `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               `league_id` INT UNSIGNED NOT NULL,
                               `home_team_id` INT UNSIGNED NOT NULL,
                               `away_team_id` INT UNSIGNED NOT NULL,
                               `kickoff_time` DATETIME NOT NULL COMMENT '开赛时间',
                               `status` VARCHAR(20) NULL COMMENT '比赛状态',
                               `home_team_score` TINYINT UNSIGNED NULL COMMENT '主队得分',
                               `away_team_score` TINYINT UNSIGNED NULL COMMENT '客队得分',
                               `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               INDEX `idx_kickoff_time` (`kickoff_time`)
) COMMENT='比赛信息表';
CREATE TABLE `biz_bet_types` (
                                 `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                 `name` VARCHAR(100) NOT NULL COMMENT '投注类型，如 Win / Draw / Lose'
) COMMENT='投注类型定义表';

CREATE TABLE `biz_odds` (
                            `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                            `match_id` INT UNSIGNED NOT NULL,
                            `bet_type_id` INT UNSIGNED NOT NULL,
                            `option_name` VARCHAR(100) NOT NULL COMMENT '投注选项，如 Manchester United, Draw, Over 2.5 Goals',
                            `value` DECIMAL(8, 3) NOT NULL COMMENT '赔率值',
                            `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '赔率是否激活',
                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            UNIQUE KEY `uk_match_bet_option` (`match_id`, `bet_type_id`, `option_name`)
) COMMENT='比赛赔率表';

CREATE TABLE `biz_user_bets` (
                                 `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                 `user_id` INT UNSIGNED NOT NULL,
                                 `match_id` INT UNSIGNED NOT NULL,
                                 `odd_id` INT UNSIGNED NOT NULL,
                                 `stake` DECIMAL(15, 4) NOT NULL COMMENT '投注金额',
                                 `odds_value` DECIMAL(8, 3) NOT NULL COMMENT '下注时的赔率',
                                 `potential_payout` DECIMAL(15, 4) NOT NULL COMMENT '预计派彩金额',
                                 `status` ENUM('unsettled', 'won', 'lost', 'void') NOT NULL DEFAULT 'unsettled' COMMENT '注单状态',
                                 `placed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下注时间',
                                 `settled_at` TIMESTAMP NULL COMMENT '结算时间'
) COMMENT='用户投注记录表';

CREATE TABLE `biz_transactions` (
                                    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                    `user_id` INT UNSIGNED NOT NULL,
                                    `related_bet_id` BIGINT UNSIGNED NULL COMMENT '关联的注单ID',
                                    `type` ENUM('deposit', 'withdrawal', 'bet_stake', 'bet_payout', 'referral_bonus') NOT NULL COMMENT '交易类型',
                                    `amount` DECIMAL(15, 4) NOT NULL COMMENT '交易金额 (正数为入账，负数为出账)',
                                    `currency` VARCHAR(10) NOT NULL DEFAULT 'USDT' COMMENT '货币单位',
                                    `method` VARCHAR(50) COMMENT '方式，如 Crypto Wallet Transfer',
                                    `status`  VARCHAR(50) NULL COMMENT '交易状态',
                                    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) COMMENT='用户资金流水表';

CREATE TABLE `biz_referrals` (
                                 `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                 `referrer_user_id` INT UNSIGNED NOT NULL COMMENT '推荐人ID',
                                 `referred_user_id` INT UNSIGNED NOT NULL UNIQUE COMMENT '被推荐人ID',
                                 `status` VARCHAR(50) NULL COMMENT '状态(例如被推荐人完成首次存款或投注后变为completed)',
                                 `bonus_awarded` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已发放奖励',
                                 `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) COMMENT='好友推荐关系表';



#============ 系统要求的基础字段

alter table biz_matches
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';


alter table biz_bet_types
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_leagues
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_odds
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_referrals
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_sports
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_teams
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_transactions
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_user_bets
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_users
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';



alter table biz_bets
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';







#============ 字段描述

-- ----------------------------
-- 1. 修改联赛表 (biz_leagues)
-- ----------------------------
ALTER TABLE `biz_leagues`
    MODIFY COLUMN `league_id` VARCHAR(50) NOT NULL COMMENT '联赛ID (来自JSON中的leagueId), 主键',
    MODIFY COLUMN `name` VARCHAR(100) DEFAULT NULL COMMENT '联赛全称 (来自JSON中的leagueAllName)',
    MODIFY COLUMN `abbr_name` VARCHAR(50) DEFAULT NULL COMMENT '球队简称',
    MODIFY COLUMN `back_color` VARCHAR(10) DEFAULT NULL COMMENT '联赛背景颜色 (来自JSON中的leagueBackColor)',
    COMMENT = '联赛信息表';


-- ----------------------------
-- 2. 修改球队表 (biz_teams)
-- ----------------------------
ALTER TABLE `biz_teams`
    MODIFY COLUMN `team_id` INT NOT NULL COMMENT '球队ID (来自JSON中的homeTeamId/awayTeamId), 主键',
    MODIFY COLUMN `full_name` VARCHAR(100) DEFAULT NULL COMMENT '球队全称 (来自JSON中的allHomeTeam/allAwayTeam)',
    MODIFY COLUMN `abbr_name` VARCHAR(50) DEFAULT NULL COMMENT '球队简称 (来自JSON中的homeTeam/awayTeam)',
    COMMENT = '球队信息表';


-- ----------------------------
-- 3. 修改比赛表 (biz_matches)
-- ----------------------------
ALTER TABLE `biz_matches`
    MODIFY COLUMN `match_id` INT NOT NULL COMMENT '比赛ID (来自JSON中的matchId), 主键',
    MODIFY COLUMN `match_num_str` VARCHAR(20) DEFAULT NULL COMMENT '比赛编号字符串 (来自JSON中的matchNumStr, 例如 "周三001")',
    MODIFY COLUMN `business_date` DATE DEFAULT NULL COMMENT '业务日期 (来自JSON中的matchDate)',
    MODIFY COLUMN `match_datetime` DATETIME DEFAULT NULL COMMENT '比赛开赛时间 (可由 matchDate 和具体时间组合)',
    MODIFY COLUMN `league_id` VARCHAR(50) DEFAULT NULL COMMENT '联赛ID (来自JSON中的leagueId)',
    MODIFY COLUMN `home_team_id` INT DEFAULT NULL COMMENT '主队ID (来自JSON中的homeTeamId)',
    MODIFY COLUMN `away_team_id` INT DEFAULT NULL COMMENT '客队ID (来自JSON中的awayTeamId)',
    ADD COLUMN `half_score` VARCHAR(10) DEFAULT NULL COMMENT '上半场比分 (来自JSON中的sectionsNo1)' AFTER `away_team_id`,
    ADD COLUMN `full_score` VARCHAR(10) DEFAULT NULL COMMENT '全场比分 (来自JSON中的sectionsNo999)' AFTER `half_score`,
    ADD COLUMN `win_flag` VARCHAR(5) DEFAULT NULL COMMENT '赛果标识 (来自JSON中的winFlag, H/D/A)' AFTER `full_score`,
    MODIFY COLUMN `status` VARCHAR(20) DEFAULT NULL COMMENT '比赛状态 (来自JSON中的poolStatus, 例如 "Payout")',
    MODIFY COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
    MODIFY COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
    COMMENT = '比赛信息与赛果表';


-- ----------------------------
-- 4. 修改赔率表 (biz_odds)
-- ----------------------------
ALTER TABLE `biz_odds`
    MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID, 主键',
    MODIFY COLUMN `match_id` INT NOT NULL COMMENT '关联的比赛ID',
    MODIFY COLUMN `pool_code` VARCHAR(10) NOT NULL COMMENT '赔率类型/玩法代码 (例如 "HAD", "HHAD")',
    MODIFY COLUMN `goal_line` VARCHAR(10) DEFAULT NULL COMMENT '让球数 (来自JSON中的goalLine, 例如 "+1")',
    MODIFY COLUMN `home_odds` DECIMAL(10, 2) DEFAULT NULL COMMENT '主胜赔率 (来自JSON中的h)',
    MODIFY COLUMN `draw_odds` DECIMAL(10, 2) DEFAULT NULL COMMENT '平局赔率 (来自JSON中的d)',
    MODIFY COLUMN `away_odds` DECIMAL(10, 2) DEFAULT NULL COMMENT '客胜赔率 (来自JSON中的a)',
    MODIFY COLUMN `status` VARCHAR(20) DEFAULT NULL COMMENT '盘口状态 (来自JSON中的poolStatus)',
    MODIFY COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '赔率更新时间',
    COMMENT = '比赛赔率表';


-- ----------------------------
-- 5. 修改注单表 (biz_bets)
-- ----------------------------
ALTER TABLE `biz_bets`
    MODIFY COLUMN `bet_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '注单ID, 主键',
    MODIFY COLUMN `user_id` BIGINT NOT NULL COMMENT '用户ID (需要关联你的用户表)',
    MODIFY COLUMN `odds_id` BIGINT NOT NULL COMMENT '投注的赔率项ID (关联 biz_odds.id)',
    MODIFY COLUMN `bet_amount` DECIMAL(10, 2) NOT NULL COMMENT '投注金额',
    MODIFY COLUMN `status` ENUM('unsettled', 'won', 'lost', 'void') NOT NULL DEFAULT 'unsettled' COMMENT '注单状态',
    MODIFY COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN `settled_at` TIMESTAMP NULL DEFAULT NULL COMMENT '结算时间',
    COMMENT = '用户注单表';


alter table biz_match_results
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';



-- ----------------------------
-- SQL 数据插入脚本
-- 作者: Gemini
-- 时间: 2025-07-24
-- 描述: 根据提供的JSON数据，向 biz_ 系列表中插入数据。
-- ----------------------------

-- ----------------------------
-- 1. 插入联赛数据 (biz_leagues)
-- 使用 ON DUPLICATE KEY UPDATE 来确保数据存在且 back_color 被更新。
-- ----------------------------
INSERT INTO `biz_leagues` (`league_id`, `name`, `abbr_name`, `back_color`) VALUES
                                                                               ('1', '亚洲冠军联赛', '亚冠', NULL),
                                                                               ('2', '澳大利亚超级联赛', '澳超', NULL),
                                                                               ('3', '非洲杯', '非洲杯', NULL),
                                                                               ('4', '亚洲杯', '亚洲杯', NULL),
                                                                               ('5', '亚洲杯预选赛', '亚预赛', NULL),
                                                                               ('6', '巴西甲级联赛', '巴甲', NULL),
                                                                               ('7', '巴西杯', '巴西杯', NULL),
                                                                               ('10', '联合会杯', '联合会杯', NULL),
                                                                               ('11', '中北美金杯赛', '金杯赛', NULL),
                                                                               ('12', '俱乐部友谊赛', '俱乐部赛', NULL),
                                                                               ('13', '美洲杯', '美洲杯', NULL),
                                                                               ('14', '世界俱乐部杯', '世俱杯', NULL),
                                                                               ('15', '荷兰杯', '荷兰杯', NULL),
                                                                               ('17', '荷兰甲级联赛', '荷甲', NULL),
                                                                               ('18', '荷兰超级杯', '荷超杯', NULL),
                                                                               ('19', '欧洲U21锦标赛', '欧青赛', NULL),
                                                                               ('20', '英格兰冠军联赛', '英冠', NULL),
                                                                               ('21', '英格兰甲级联赛', '英甲', NULL),
                                                                               ('22', '英格兰乙级联赛', '英乙', NULL),
                                                                               ('23', '英格兰足总杯', '英足总杯', NULL),
                                                                               ('24', '英格兰联赛杯', '英联赛杯', NULL),
                                                                               ('25', '英格兰超级联赛', '英超', NULL),
                                                                               ('26', '英格兰社区盾杯', '社区盾杯', NULL),
                                                                               ('27', '欧洲杯', '欧洲杯', NULL),
                                                                               ('28', '欧洲杯预选赛', '欧预赛', NULL),
                                                                               ('29', '法国超级杯', '法超杯', NULL),
                                                                               ('30', '法国杯', '法国杯', NULL),
                                                                               ('31', '法国联赛杯', '法联赛杯', NULL),
                                                                               ('32', '法国甲级联赛', '法甲', NULL),
                                                                               ('34', '德国乙级联赛', '德乙', NULL),
                                                                               ('36', '德国杯', '德国杯', NULL),
                                                                               ('37', '德国甲级联赛', '德甲', NULL),
                                                                               ('38', '意大利杯', '意大利杯', NULL),
                                                                               ('39', '国际赛', '国际赛', NULL),
                                                                               ('40', '意大利甲级联赛', '意甲', NULL),
                                                                               ('41', '意大利超级杯', '意超杯', NULL),
                                                                               ('42', '日本职业联赛', '日职', '009900'),
                                                                               ('43', '日本乙级联赛', '日乙', NULL),
                                                                               ('45', '日本天皇杯', '天皇杯', NULL),
                                                                               ('46', '日本联赛杯', '日联赛杯', NULL),
                                                                               ('47', '日本超级杯', '日超杯', NULL),
                                                                               ('48', '韩国职业联赛', '韩职', '15DBAE'),
                                                                               ('49', '南美解放者杯', '解放者杯', NULL),
                                                                               ('50', '美国职业大联盟', '美职足', NULL),
                                                                               ('51', '挪威超级联赛', '挪超', NULL),
                                                                               ('52', '挪威杯', '挪威杯', NULL),
                                                                               ('54', '葡萄牙杯', '葡萄牙杯', NULL),
                                                                               ('55', '葡萄牙超级联赛', '葡超', NULL),
                                                                               ('56', '葡萄牙联赛杯', '葡联赛杯', NULL),
                                                                               ('57', '葡萄牙超级杯', '葡超杯', NULL),
                                                                               ('58', '瑞典超级联赛', '瑞超', '004488'),
                                                                               ('59', '东南亚锦标赛', '东南亚锦', NULL),
                                                                               ('60', '苏格兰足总杯', '苏足总杯', NULL),
                                                                               ('61', '西班牙国王杯', '国王杯', NULL),
                                                                               ('62', '西班牙甲级联赛', '西甲', NULL),
                                                                               ('63', '苏格兰联赛杯', '苏联赛杯', NULL),
                                                                               ('64', '苏格兰超级联赛', '苏超', NULL),
                                                                               ('66', '西班牙超级杯', '西超杯', NULL),
                                                                               ('67', '瑞典杯', '瑞典杯', NULL),
                                                                               ('69', '欧洲冠军联赛', '欧冠', 'F75000'),
                                                                               ('70', '欧罗巴联赛', '欧罗巴', NULL),
                                                                               ('71', '欧洲超级杯', '欧超杯', NULL),
                                                                               ('72', '世界杯', '世界杯', NULL),
                                                                               ('73', '世界杯预选赛', '世预赛', NULL),
                                                                               ('74', '女足世界杯', '女世界杯', NULL),
                                                                               ('75', '世界U20锦标赛', '世青赛', NULL),
                                                                               ('76', '杯赛', '杯赛', NULL),
                                                                               ('77', '阿根廷甲级联赛', '阿甲', NULL),
                                                                               ('78', '东亚四强赛', '四强赛', NULL),
                                                                               ('80', '荷兰乙级联赛', '荷乙', NULL),
                                                                               ('81', '法国乙级联赛', '法乙', NULL),
                                                                               ('82', '德国超级杯', '德超杯', NULL),
                                                                               ('83', '亚运会男足', '亚运男足', NULL),
                                                                               ('84', '瑞典超级杯', '瑞超杯', NULL),
                                                                               ('85', '挪威超级杯', '挪超杯', NULL),
                                                                               ('86', '英格兰锦标赛', '英锦标赛', NULL),
                                                                               ('87', '美国公开赛杯', '公开赛杯', NULL),
                                                                               ('88', '南美俱乐部杯', '俱乐部杯', NULL),
                                                                               ('89', '南美优胜者杯', '优胜者杯', NULL),
                                                                               ('90', '中北美冠军联赛', '中北美冠', NULL),
                                                                               ('92', '欧洲U21预选赛', '欧青预赛', NULL),
                                                                               ('93', '奥运会男足', '奥运男足', NULL),
                                                                               ('94', '奥运会女足', '奥运女足', NULL),
                                                                               ('95', '东亚女足四强赛', '女四强赛', NULL),
                                                                               ('96', '阿根廷杯', '阿根廷杯', NULL),
                                                                               ('97', '阿根廷超级杯', '阿超杯', NULL),
                                                                               ('98', '巴西圣保罗州锦赛', '圣保罗锦', NULL),
                                                                               ('100', '俄罗斯超级联赛', '俄超', NULL),
                                                                               ('101', '智利甲级联赛', '智利甲', NULL),
                                                                               ('102', '墨西哥超级联赛', '墨超', NULL),
                                                                               ('103', '俄罗斯杯', '俄罗斯杯', NULL),
                                                                               ('104', '智利杯', '智利杯', NULL),
                                                                               ('105', '墨西哥杯', '墨西哥杯', NULL),
                                                                               ('106', '亚运会女足', '亚运女足', NULL),
                                                                               ('108', '国际冠军杯', '国冠杯', NULL),
                                                                               ('109', '俄罗斯超级杯', '俄超杯', NULL),
                                                                               ('110', '墨西哥超级杯', '墨超杯', NULL),
                                                                               ('111', '智利超级杯', '智超杯', NULL),
                                                                               ('112', '墨西哥冠军杯', '墨冠杯', NULL),
                                                                               ('114', '澳大利亚杯', '澳杯', NULL),
                                                                               ('116', '比利时甲级联赛', '比甲', NULL),
                                                                               ('117', '比利时杯', '比利时杯', NULL),
                                                                               ('118', '亚洲U23锦标赛', '亚奥赛', NULL),
                                                                               ('120', '韩国足总杯', '韩足总杯', NULL),
                                                                               ('122', '比利时超级杯', '比超杯', NULL),
                                                                               ('123', '中国杯', '中国杯', NULL),
                                                                               ('125', '女足欧洲杯', '女欧洲杯', NULL),
                                                                               ('127', '欧洲国家联赛', '欧国联', NULL),
                                                                               ('1033103', '欧洲协会联赛', '欧协联', NULL),
                                                                               ('2064839', '芬兰超级联赛', '芬超', NULL)
ON DUPLICATE KEY UPDATE `back_color` = VALUES(`back_color`);

-- ----------------------------
-- 2. 插入球队数据 (biz_teams)
-- 使用 INSERT IGNORE 避免因主键重复导致错误。
-- ----------------------------
INSERT IGNORE INTO `biz_teams` (`team_id`, `full_name`, `abbr_name`) VALUES
                                                                         (304, '谢尔本', '谢尔本'),
                                                                         (1253, '卡拉巴赫', '卡拉巴赫'),
                                                                         (612, '布兰', '布兰'),
                                                                         (335, '萨尔茨堡', '萨尔茨堡'),
                                                                         (1090, '蔚山现代', '蔚山现代'),
                                                                         (1083, '大田市民', '大田市民'),
                                                                         (860, '全北现代', '全北现代'),
                                                                         (1192, '江原FC', '江原FC'),
                                                                         (1087, '济州SK', '济州SK'),
                                                                         (929, '首尔FC', '首尔FC'),
                                                                         (560, '浦和红钻', '浦和红钻'),
                                                                         (1019, '湘南海洋', '湘南海洋'),
                                                                         (220, '格拉斯哥流浪者', '流浪者'),
                                                                         (287, '帕纳辛纳科斯', '帕纳辛纳'),
                                                                         (1036994, '里加足球学校', '里加足校'),
                                                                         (606, '马尔默', '马尔默'),
                                                                         (1088, '浦项制铁', '浦项制铁'),
                                                                         (2309, '水原FC', '水原FC'),
                                                                         (1557, '光州FC', '光州FC'),
                                                                         (1084, '金泉尚武', '金泉尚武'),
                                                                         (973, '北雪平', '北雪平'),
                                                                         (1570, '韦纳穆', '韦纳穆');




-- ----------------------------
-- 2. 方案主表 (biz_schemes)
-- 描述: 存储由用户发起的投注方案。
-- ----------------------------
CREATE TABLE `biz_schemes` (
                               `scheme_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '方案ID, 主键',
                               `user_id` BIGINT NOT NULL COMMENT '方案发起人ID (关联 biz_users)',
                               `title` VARCHAR(255) NOT NULL COMMENT '方案标题 (例如: 张三的第5轮方案)',
                               `status` ENUM('ongoing', 'ended') NOT NULL DEFAULT 'ongoing' COMMENT '方案状态: ongoing-进行中, ended-已结束',
                               `current_period_number` INT NOT NULL DEFAULT 1 COMMENT '当前进行到第几期',
                               `consecutive_losses` INT NOT NULL DEFAULT 0 COMMENT '连续失败期数',
                               `reason_for_ending` VARCHAR(50) DEFAULT NULL COMMENT '方案结束原因 (例如: won, max_loss_reached)',
                               `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`scheme_id`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投注方案表';


-- ----------------------------
-- 3. 方案期数表 (biz_scheme_periods)
-- 描述: 记录一个方案中的每一期投注。
-- ----------------------------
CREATE TABLE `biz_scheme_periods` (
                                      `period_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '期数ID, 主键',
                                      `scheme_id` BIGINT NOT NULL COMMENT '所属方案ID (关联 biz_schemes)',
                                      `period_number` INT NOT NULL COMMENT '期数编号 (1-14)',
                                      `bet_amount` DECIMAL(10, 2) NOT NULL COMMENT '本期发起人投注金额',
                                      `status` ENUM('pending', 'won', 'lost') NOT NULL DEFAULT 'pending' COMMENT '本期状态: pending-待开奖, won-已中奖, lost-未中奖',
                                      `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      PRIMARY KEY (`period_id`),
                                      KEY `idx_scheme_id_period_number` (`scheme_id`, `period_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方案期数表';


-- ----------------------------
-- 4. 方案期数详情表 (biz_scheme_period_details)
-- 描述: 记录每一期方案的具体比赛选择。
-- ----------------------------
CREATE TABLE `biz_scheme_period_details` (
                                             `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '详情ID, 主键',
                                             `period_id` BIGINT NOT NULL COMMENT '所属期数ID (关联 biz_scheme_periods)',
                                             `match_id` INT NOT NULL COMMENT '比赛ID (关联 biz_matches)',
                                             `pool_code` VARCHAR(10) NOT NULL COMMENT '玩法代码 (例如: HAD, HHAD)',
                                             `selection` VARCHAR(20) NOT NULL COMMENT '投注选择 (例如: H, A, D)',
                                             `odds` DECIMAL(10, 2) NOT NULL COMMENT '选择时的赔率',
                                             PRIMARY KEY (`detail_id`),
                                             KEY `idx_period_id` (`period_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方案期数详情表';


-- ----------------------------
-- 5. 用户跟投记录表 (biz_user_follows)
-- 描述: 记录用户对某个方案期数的跟投行为。
-- ----------------------------
CREATE TABLE `biz_user_follows` (
                                    `follow_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '跟投ID, 主键',
                                    `user_id` BIGINT NOT NULL COMMENT '跟投用户ID (关联 biz_users)',
                                    `period_id` BIGINT NOT NULL COMMENT '跟投的期数ID (关联 biz_scheme_periods)',
                                    `bet_amount` DECIMAL(10, 2) NOT NULL COMMENT '跟投金额 (根据期数固定)',
                                    `status` ENUM('bought', 'failed', 'settled') NOT NULL DEFAULT 'bought' COMMENT '跟投状态: bought-已买, failed-买入失败, settled-已结算',
                                    `payout_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '派奖金额 (中奖后更新)',
                                    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '跟投时间',
                                    PRIMARY KEY (`follow_id`),
                                    KEY `idx_user_id` (`user_id`),
                                    KEY `idx_period_id` (`period_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户跟投记录表';




alter table biz_schemes
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_scheme_periods
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_scheme_period_details
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';


alter table biz_user_follows
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

-- ----------------------------
-- 修改赔率表 (biz_odds) - 新增是否单关字段
-- ----------------------------
ALTER TABLE `biz_odds`
    ADD COLUMN `single` TINYINT(1) DEFAULT 0 COMMENT '是否支持单关 (1:是, 0:否)';

alter table biz_wallet_transactions
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_user_wallets
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table sys_user
    add invitation_code    varchar(20)  null comment '邀请码',
    add inviter_id    bigint  null comment '邀请码id';


-- =================================================================
-- biz_transactions: 核心业务流水表
-- 设计目标：
-- 1. 记录所有用户资产相关的变动。
-- 2. 清晰区分链上操作（充值/提现）和站内操作（转账/手续费）。
-- 3. 保证数据准确性，支持对账和审计。
-- 4. 具备良好的查询性能和扩展性。
-- =================================================================

CREATE TABLE `biz_transactions` (
                                    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID，主键',
                                    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联的用户ID',
                                    `amount` DECIMAL(36, 18) NOT NULL COMMENT '交易金额 (正数表示收入, 负数表示支出)',
                                    `currency` VARCHAR(50) NOT NULL COMMENT '币种代码, 例如: USDT, BTC, TRX',

                                    `transaction_type` VARCHAR(50) NOT NULL COMMENT '交易类型 (RECHARGE:充值, WITHDRAWAL:提现, INTERNAL_TRANSFER_IN:站内转入, INTERNAL_TRANSFER_OUT:站内转出, FEE:手续费, ADJUSTMENT:系统调账)',
                                    `status` VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT '交易状态 (PENDING:处理中, CONFIRMED:已确认/成功, FAILED:失败, CANCELLED:已取消)',

    -- 关联与备注信息
                                    `reference_id` VARCHAR(255) NULL COMMENT '关联ID (用于将站内转账的收支两条记录关联起来)',
                                    `remarks` TEXT NULL COMMENT '备注信息 (例如: 失败原因, 系统调账说明等)',

    -- 链上交易专属字段 (站内操作时这些字段为NULL)
                                    `blockchain_network` VARCHAR(50) NULL COMMENT '区块链网络 (例如: TRON, ETHEREUM_ERC20, BSC)',
                                    `transaction_hash` VARCHAR(255) NULL COMMENT '区块链交易哈希 (TxID)',
                                    `from_address` VARCHAR(255) NULL COMMENT '链上付款方地址',
                                    `to_address` VARCHAR(255) NULL COMMENT '链上收款方地址',

    -- 时间戳
                                    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',

                                    PRIMARY KEY (`id`),

    -- ================== 索引定义 ==================
    -- 为用户ID建立索引，加速查询特定用户的流水
                                    INDEX `idx_user_id` (`user_id`),

    -- 为交易类型建立索引，方便分类统计
                                    INDEX `idx_transaction_type` (`transaction_type`),

    -- 为关联ID建立索引，快速查找关联交易
                                    INDEX `idx_reference_id` (`reference_id`),

    -- 为交易哈希建立唯一索引，防止重复记录同一笔链上交易
                                    UNIQUE INDEX `idx_unique_hash` (`transaction_hash`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='核心业务流水表';

alter table biz_transactions
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';


CREATE TABLE `biz_system_reserve` (
                                      `reserve_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '储备金记录ID',
                                      `source_type` VARCHAR(50) NOT NULL COMMENT '来源类型 (例如: scheme_payout_commission)',
                                      `source_id` VARCHAR(64) NOT NULL COMMENT '来源ID (例如: biz_user_follows表的follow_id)',
                                      `user_id` BIGINT(20) NOT NULL COMMENT '关联的用户ID',
                                      `amount` DECIMAL(10, 2) NOT NULL COMMENT '本次存入的储备金金额 (佣金)',
                                      `commission_rate` DECIMAL(5, 4) NOT NULL COMMENT '当时的佣金率 (例如: 0.1000)',
                                      `original_payout` DECIMAL(10, 2) NOT NULL COMMENT '用户原始应得奖金 (扣除佣金前)',
                                      `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                      `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                      `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                      PRIMARY KEY (`reserve_id`),
                                      KEY `idx_source` (`source_type`, `source_id`),
                                      KEY `idx_user_id` (`user_id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统储备金明细表';


CREATE TABLE `biz_system_reserve_summary` (
                                              `summary_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '汇总ID (主键)',
                                              `total_reserve_amount` DECIMAL(20, 2) NOT NULL DEFAULT '0.00' COMMENT '系统储备金总额',
                                              `last_calculation_time` DATETIME DEFAULT NULL COMMENT '最后一次计算汇总的时间',
                                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `remark` VARCHAR(500) DEFAULT '系统储备金总额汇总' COMMENT '备注',
                                              PRIMARY KEY (`summary_id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统储备金汇总表';

-- 初始化一条记录
INSERT INTO `biz_system_reserve_summary` (`summary_id`, `total_reserve_amount`, `last_calculation_time`, `remark`) VALUES (1, '0.00', NOW(), '系统储备金总额汇总');



-- ===================================================================================
-- 表: biz_withdrawals (提现申请表)
-- 描述: 记录用户发起的每一笔TRC20-USDT提现请求，并跟踪其审核和处理状态。
-- ===================================================================================
CREATE TABLE `biz_withdrawals` (
                                   `withdrawal_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提现申请的唯一ID, 主键',
                                   `user_id` BIGINT NOT NULL COMMENT '申请提现的用户ID, 关联 sys_user 表',
                                   `amount` DECIMAL(10, 2) NOT NULL COMMENT '申请提现的 USDT 金额',
                                   `network_fee` DECIMAL(10, 2) NOT NULL COMMENT '提现时扣除的网络手续费（矿工费）',
                                   `final_amount` DECIMAL(10, 2) NOT NULL COMMENT '用户最终实际收到的金额 (amount - network_fee)',
                                   `to_wallet_address` VARCHAR(100) NOT NULL COMMENT '收款的TRC20钱包地址',
                                   `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '提现申请的状态 (PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, FAILED)',
                                   `request_time` DATETIME NOT NULL COMMENT '用户提交申请的时间',
                                   `audit_by` BIGINT NULL COMMENT '审核该笔申请的管理员ID',
                                   `audit_time` DATETIME NULL COMMENT '审核操作的时间',
                                   `audit_remarks` VARCHAR(255) NULL COMMENT '审核备注, 特别是拒绝时需要填写原因',
                                   `tx_hash` VARCHAR(100) NULL COMMENT 'TRON链上的交易哈希 (Transaction Hash), 用于核对',
                                   `completion_time` DATETIME NULL COMMENT '提现完成（资金到账）的时间',
                                   `create_by` VARCHAR(64) NULL COMMENT '创建者',
                                   `create_time` DATETIME NULL COMMENT '创建时间',
                                   `update_by` VARCHAR(64) NULL COMMENT '更新者',
                                   `update_time` DATETIME NULL COMMENT '更新时间',
                                   PRIMARY KEY (`withdrawal_id`),
                                   INDEX `idx_user_id` (`user_id`),
                                   INDEX `idx_status` (`status`)
) COMMENT='用户提现申请表' CHARSET=utf8mb4;

alter table biz_withdrawals
    add create_dept  bigint                        null comment '创建部门',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';


CREATE TABLE `biz_user_progress` (
                                     `progress_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '进度记录的唯一ID, 主键',
                                     `user_id` BIGINT NOT NULL COMMENT '关联的用户ID (sys_user.user_id)',
                                     `consecutive_losses` INT NOT NULL DEFAULT 0 COMMENT '当前的连续失败次数，默认为 0',
                                     `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的最后更新时间',
                                     PRIMARY KEY (`progress_id`),
                                     UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '确保每个用户只有一条进度记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户跟投进度表';

alter table biz_user_progress
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';



-- 为 biz_matches 表添加字段
ALTER TABLE `biz_matches`
    ADD COLUMN `home_team_logo` VARCHAR(512) NULL COMMENT '主队logo地址',
    ADD COLUMN `away_team_logo` VARCHAR(512) NULL COMMENT '客队logo地址';


alter table biz_system_reserve_summary
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';
alter table biz_system_reserve
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';


alter table biz_system_reserve
    add create_dept  bigint                        null comment '创建部门'


ALTER TABLE `biz_user_follows`
    ADD COLUMN `payout_odds` DECIMAL(10, 2) NULL COMMENT '结算时赔率';


ALTER TABLE `biz_user_follows`
    ADD COLUMN `bet_odds_desc` varchar(100) NULL COMMENT '跟投时赔率详情 (例如: 1.38 * 1.45)',
    ADD COLUMN `payout_odds_desc` varchar(100) NULL COMMENT '结算时赔率详情';


ALTER TABLE `biz_deposit_wallets`
    add create_dept  bigint                        null comment '创建部门'


CREATE TABLE biz_reward_claim (
                                  id BIGINT AUTO_INCREMENT COMMENT '主键',
                                  user_id BIGINT NOT NULL COMMENT '申请用户ID',
                                  amount DECIMAL(18, 8) NOT NULL COMMENT '申请金额',
                                  currency VARCHAR(10) DEFAULT 'USDT' COMMENT '货币类型',
                                  status VARCHAR(20) NOT NULL COMMENT '状态 (PENDING, APPROVED, REJECTED)',
                                  remarks VARCHAR(500) COMMENT '备注',
                                  create_by BIGINT COMMENT '创建者',
                                  create_time DATETIME COMMENT '创建时间',
                                  update_by BIGINT COMMENT '更新者',
                                  update_time DATETIME COMMENT '更新时间',
                                  PRIMARY KEY (id)
) COMMENT '理赔申请表';

alter table biz_reward_claim
    add create_dept  bigint                        null comment '创建部门',
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';

alter table biz_user_follows
    add `result_status` varchar(255) NULL DEFAULT NULL COMMENT '结算结果状态'


ALTER TABLE `biz_scheme_periods`
    ADD COLUMN `combination_type` VARCHAR(20) NULL COMMENT '过关方式 (e.g., 2x1, 3x3)' AFTER `status`;

-- 修改 status 字段的 ENUM 定义，增加一个新的 'in_cart' 状态
ALTER TABLE `biz_user_follows`
    MODIFY COLUMN `status` ENUM('in_cart', 'bought', 'failed', 'settled')
        DEFAULT 'in_cart' NOT NULL COMMENT '跟投状态: in_cart-购物车, bought-已投注, failed-失败, settled-已结算';


alter table biz_user_follow_details
    add create_dept  bigint                        null comment '创建部门'

alter table biz_app_versions
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间'

alter table biz_device_updates
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者',
    add update_time  datetime                      null comment '更新时间'

CREATE TABLE IF NOT EXISTS `biz_chain_sync_state` (
                                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                                      `chain_name` varchar(50) NOT NULL COMMENT '链名称 (例如: BSC)',
    `last_synced_block` bigint NOT NULL COMMENT '最后成功同步的区块号',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_chain_name` (`chain_name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='区块链同步状态表';

-- 为 BSC 链插入一条初始记录，如果没有的话。-1 表示从头开始同步。
INSERT IGNORE INTO `biz_chain_sync_state` (chain_name, last_synced_block) VALUES ('BSC', -1);

alter table biz_chain_sync_state
    add create_dept  bigint                        null comment '创建部门',
    add create_by    bigint                        null comment '创建者',
    add create_time  datetime                      null comment '创建时间',
    add update_by    bigint                        null comment '更新者'

alter table biz_bet_order_details
    add update_time  datetime                      null comment '更新时间


alter table biz_bet_orders
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';
alter table biz_bet_order_details
    add tenant_id    varchar(20)  default '000000' null comment '租户编号';
