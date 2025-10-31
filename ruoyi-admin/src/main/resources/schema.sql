-- 應用版本管理
create table if not exists biz_app_version
(
    id           bigint auto_increment primary key,
    platform     varchar(20) default 'android' not null,
    version_code int                           not null,
    version_name varchar(50)                   not null,
    version_desc varchar(500)                  null,
    app_name     varchar(100)                  not null,
    download_url varchar(255)                  not null,
    is_active    tinyint     default 1         not null,
    create_dept  bigint                        null,
    create_by    bigint                        null,
    create_time  datetime                      null,
    update_by    bigint                        null,
    tenant_id    varchar(20)                   null,
    update_time  datetime                      null
    );

-- 投注類型定義表
create table if not exists biz_bet_types
(
    id          int auto_increment primary key,
    name        varchar(100)                 not null,
    create_dept bigint                       null,
    create_by   bigint                       null,
    create_time datetime                     null,
    update_by   bigint                       null,
    update_time datetime                     null,
    tenant_id   varchar(20) default '000000' null
    );

-- 用戶注單表
create table if not exists biz_bets
(
    bet_id      bigint auto_increment primary key,
    user_id     bigint                                 not null,
    odds_id     bigint                                 not null,
    bet_amount  decimal(10, 2)                         not null,
    status      varchar(20) default 'unsettled'       not null,
    created_at  timestamp    default CURRENT_TIMESTAMP not null,
    settled_at  timestamp                              null,
    create_dept bigint                                 null,
    create_by   bigint                                 null,
    create_time datetime                               null,
    update_by   bigint                                 null,
    update_time datetime                               null,
    tenant_id   varchar(20)  default '000000'          null
    );

-- 平台充值錢包表
create table if not exists biz_deposit_wallets
(
    wallet_id      bigint auto_increment primary key,
    wallet_name    varchar(100)                 null,
    wallet_address varchar(100)                 not null,
    status         varchar(20) default 'active' not null,
    qr_code_url    varchar(255)                 null,
    create_by     bigint default 0       null,
    create_time    datetime                     null,
    update_by      bigint default 0       null,
    update_time    datetime                     null,
    user_id        bigint                       null,
    create_dept    bigint                       null,
    constraint uk_wallet_address
    unique (wallet_address)
    );

-- 預生成邀請碼池表
create table if not exists biz_invitation_codes
(
    code_id          bigint auto_increment primary key,
    invitation_code  varchar(20)                           not null,
    status           varchar(20) default 'available'       not null,
    assignee_user_id bigint                                null,
    assign_time      datetime                              null,
    create_time      datetime    default CURRENT_TIMESTAMP null,
    update_time      datetime    default CURRENT_TIMESTAMP null,
    tenant_id        varchar(20) default '000000'          null,
    create_dept      bigint                                null,
    create_by        bigint                                null,
    update_by        bigint                                null,
    constraint uk_invitation_code
    unique (invitation_code)
    );

-- 聯賽資訊表
create table if not exists biz_leagues
(
    league_id   varchar(50)                  not null primary key,
    name        varchar(100)                 null,
    abbr_name   varchar(50)                  null,
    back_color  varchar(10)                  null,
    create_dept bigint                       null,
    create_by   bigint                       null,
    create_time datetime                     null,
    update_by   bigint                       null,
    update_time datetime                     null,
    tenant_id   varchar(20) default '000000' null
    );

-- 創建 biz_match_results 表 (H2 相容版, 已新增 is_settled 欄位)
CREATE TABLE biz_match_results
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id         INT NOT NULL,
    pool_id          BIGINT,
    pool_code        VARCHAR(10),
    combination      VARCHAR(20),
    combination_desc VARCHAR(50),
    goal_line        VARCHAR(10),
    odds             DECIMAL(10, 2),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_settled       TINYINT DEFAULT 0 NOT NULL,
    create_dept      BIGINT,
    create_by        BIGINT,
    create_time      DATETIME,
    update_by        BIGINT,
    update_time      DATETIME,
    tenant_id        VARCHAR(20) DEFAULT '000000',
    CONSTRAINT uk_match_pool UNIQUE (match_id, pool_code)
);

-- 比賽資訊與賽果表
create table if not exists biz_matches
(
    match_id            int                                   not null primary key,
    match_num           int                                   null,
    match_num_str       varchar(20)                           null,
    match_phase_tc      varchar(10)                           null,
    match_week          varchar(20)                           null,
    business_date       date                                  null,
    home_team_name      varchar(100)                          null,
    match_status        varchar(10)                           null,
    match_datetime      datetime                              null,
    league_id           varchar(50)                           null,
    home_team_id        int                                   null,
    away_team_id        int                                   null,
    away_team_name      varchar(100)                          null,
    half_score          varchar(10)                           null,
    full_score          varchar(10)                           null,
    win_flag            varchar(5)                            null,
    status              varchar(20)                           null,
    sell_status         varchar(20)                           null,
    remark              varchar(255)                          null,
    created_at          timestamp   default CURRENT_TIMESTAMP not null,
    updated_at          timestamp   default CURRENT_TIMESTAMP not null,
    create_dept         bigint                                null,
    create_by           bigint                                null,
    create_time         datetime                              null,
    update_by           bigint                                null,
    update_time         datetime                              null,
    tenant_id           varchar(20) default '000000'          null,
    home_team_logo      varchar(512)                          null,
    away_team_logo      varchar(512)                          null,
    match_minute        varchar(10)                           null,
    match_phase_tc_name varchar(20)                           null,
    match_name          varchar(150)                          null
    );

-- 比賽賠率表
create table if not exists biz_odds
(
    id          bigint auto_increment primary key,
    match_id    int                                   not null,
    pool_code   varchar(10)                           not null,
    goal_line   varchar(10)                           null,
    home_odds   decimal(10, 2)                        null,
    draw_odds   decimal(10, 2)                        null,
    away_odds   decimal(10, 2)                        null,
    status      varchar(20)                           null,
    single      tinyint     default 0                 null,
    updated_at  timestamp   default CURRENT_TIMESTAMP not null,
    create_dept bigint                                null,
    create_by   bigint                                null,
    create_time datetime                              null,
    update_by   bigint                                null,
    update_time datetime                              null,
    tenant_id   varchar(20) default '000000'          null,
    odds_data   clob                                  null,
    constraint uk_match_pool_odds
    unique (match_id, pool_code)
    );

-- 理賠申請表
create table if not exists biz_reward_claim
(
    id          bigint auto_increment primary key,
    biz_code    varchar(20)                  null,
    user_id     bigint                       not null,
    amount      decimal(18, 2)               not null,
    currency    varchar(10) default 'USDT'   null,
    status      varchar(20)                  null,
    remarks     varchar(500)                 null,
    create_by   bigint                       null,
    create_time datetime                     null,
    update_by   bigint                       null,
    update_time datetime                     null,
    create_dept bigint                       null,
    tenant_id   varchar(20) default '000000' null,
    lost_count  int         default 0        null
    );

-- 方案期數詳情表
create table if not exists biz_scheme_period_details
(
    detail_id   bigint auto_increment primary key,
    period_id   bigint                       not null,
    match_id    int                          not null,
    match_name  varchar(200)                 null,
    pool_code   varchar(10)                  not null,
    selection   varchar(20)                  not null,
    odds        decimal(10, 2)               not null,
    create_dept bigint                       null,
    goal_line   varchar(10)                  null,
    create_by   bigint                       null,
    create_time datetime                     null,
    update_by   bigint                       null,
    update_time datetime                     null,
    tenant_id   varchar(20) default '000000' null
    );

-- 方案期數表
create table if not exists biz_scheme_periods
(
    period_id             bigint auto_increment primary key,
    result_time           datetime                              null,
    status                varchar(20) default 'pending'         not null,
    combination_type      varchar(20)                           null,
    created_at            timestamp   default CURRENT_TIMESTAMP not null,
    create_dept           bigint                                null,
    create_by             bigint                                null,
    create_time           datetime                              null,
    update_by             bigint                                null,
    update_time           datetime                              null,
    tenant_id             varchar(20) default '000000'          null,
    name                  varchar(50)                           null,
    deadline_time         datetime                              null,
    last_match_updatetime datetime                              null,
    final_pl_amount       decimal(18,2)                         null
    );

-- 兼容性调整：为 H2 测试数据库添加缺失的栏位
-- 注意：如果您的测试框架每次都会重建表结构，可以移除以下 ALTER 语句
ALTER TABLE biz_scheme_periods ADD COLUMN IF NOT EXISTS limit_amount DECIMAL(18, 2) DEFAULT -1.00;
ALTER TABLE biz_scheme_periods ADD COLUMN IF NOT EXISTS accumulated_amount DECIMAL(18, 2) DEFAULT 0.00;
ALTER TABLE biz_scheme_periods ADD COLUMN IF NOT EXISTS lost_streak_since_last_win int DEFAULT 0;
-- 投注方案表
create table if not exists biz_schemes
(
    scheme_id             bigint auto_increment primary key,
    user_id               bigint                                not null,
    title                 varchar(255)                          not null,
    status                varchar(20) default 'ongoing'         not null,
    current_period_number int          default 1                 not null,
    consecutive_losses    int          default 0                 not null,
    reason_for_ending     varchar(50)                           null,
    created_at            timestamp    default CURRENT_TIMESTAMP not null,
    updated_at            timestamp    default CURRENT_TIMESTAMP not null,
    create_dept           bigint                                null,
    create_by             bigint                                null,
    create_time           datetime                              null,
    update_by             bigint                                null,
    update_time           datetime                              null,
    tenant_id             varchar(20)  default '000000'          null,
    multiple              int          default 1                 null
    );

-- 體育專案表
create table if not exists biz_sports
(
    id          bigint auto_increment primary key,
    name        varchar(50)                  not null,
    create_dept bigint                       null,
    create_by   bigint                       null,
    create_time datetime                     null,
    update_by   bigint                       null,
    update_time datetime                     null,
    tenant_id   varchar(20) default '000000' null,
    constraint name
    unique (name)
    );

-- 系統儲備金明細表
create table if not exists biz_system_reserve
(
    reserve_id      bigint auto_increment primary key,
    source_type     varchar(50)                           not null,
    source_id       varchar(64)                           not null,
    user_id         bigint                                not null,
    amount          decimal(10, 2)                        not null,
    commission_rate decimal(5, 4)                         not null,
    original_payout decimal(10, 2)                        not null,
    create_by       varchar(64) default ''                null,
    create_time     datetime    default CURRENT_TIMESTAMP null,
    update_by       varchar(64) default ''                null,
    update_time     datetime    default CURRENT_TIMESTAMP null,
    remark          varchar(500)                          null,
    tenant_id       varchar(20) default '000000'          null,
    create_dept     bigint                                null
    );

-- 系統儲備金彙總表
create table if not exists biz_system_reserve_summary
(
    summary_id            bigint auto_increment primary key,
    total_reserve_amount  decimal(20, 2) default 0.00                 not null,
    last_calculation_time datetime                                    null,
    create_time           datetime       default CURRENT_TIMESTAMP    null,
    update_time           datetime       default CURRENT_TIMESTAMP    null,
    remark                varchar(500)   default '系统储备金总额汇总' null,
    tenant_id             varchar(20)    default '000000'             null,
    create_dept           bigint                                      null,
    create_by             bigint                                      null,
    update_by             bigint                                      null,
    id                    bigint                                      null
    );

-- 球隊資訊表
create table if not exists biz_teams
(
    team_id       int                                                                                           not null primary key,
    full_name     varchar(100)                                                                                  null,
    abbr_name     varchar(50)                                                                                   null,
    ranks         varchar(50)                                                                                   null,
    create_dept   bigint                                                                                        null,
    create_by     bigint                                                                                        null,
    create_time   datetime                                                                                      null,
    update_by     bigint                                                                                        null,
    update_time   datetime                                                                                      null,
    tenant_id     varchar(20)  default '000000'                                                                 null,
    logo          varchar(255) default 'https://static.sporttery.cn/res_1_0/jcw/upload/teamlogo/teamdef_zq.png' null,
    live_sport_id int                                                                                           null,
    code          varchar(20)                                                                                   null
    );

-- 核心業務流水表
create table if not exists biz_transactions
(
    id                 bigint auto_increment primary key,
    user_id            bigint                                not null,
    amount             decimal(36, 2)                        not null,
    currency           varchar(50) default 'USDT'            null,
    transaction_type   varchar(50)                           not null,
    status             varchar(50) default 'PENDING'         not null,
    reference_id       varchar(255)                          null,
    remarks            clob                                  null,
    blockchain_network varchar(50)                           null,
    transaction_hash   varchar(255)                          null,
    from_address       varchar(255)                          null,
    to_address         varchar(255)                          null,
    created_at         timestamp   default CURRENT_TIMESTAMP not null,
    updated_at         timestamp   default CURRENT_TIMESTAMP not null,
    create_dept        bigint                                null,
    create_by          bigint                                null,
    create_time        datetime                              null,
    update_by          bigint                                null,
    update_time        datetime                              null,
    tenant_id          varchar(20) default '000000'          null,
    source_id          varchar(100)                          null,
    constraint idx_unique_hash
    unique (transaction_hash)
    );

-- 使用者投注記錄表
create table if not exists biz_user_bets
(
    id               bigint auto_increment primary key,
    user_id          int                                     not null,
    match_id         int                                     not null,
    odd_id           int                                     not null,
    stake            decimal(15, 4)                          not null,
    odds_value       decimal(8, 3)                           not null,
    potential_payout decimal(15, 4)                          not null,
    status           varchar(20) default 'unsettled'       not null,
    placed_at        timestamp    default CURRENT_TIMESTAMP null,
    settled_at       timestamp                               null,
    create_dept      bigint                                  null,
    create_by        bigint                                  null,
    create_time      datetime                                null,
    update_by        bigint                                  null,
    update_time      datetime                                null,
    tenant_id        varchar(20)  default '000000'          null
    );

-- 使用者跟投詳情表
create table if not exists biz_user_follow_details
(
    follow_detail_id  bigint auto_increment primary key,
    follow_id         bigint                       not null,
    period_id         bigint                       null,
    period_details_id bigint                       null,
    match_id          bigint                       not null,
    match_name        varchar(200)                 null,
    pool_code         varchar(10)                  not null,
    selection         varchar(20)                  not null,
    odds              decimal(10, 2)               not null,
    goal_line         varchar(10)                  null,
    create_time       datetime                     null,
    update_time       datetime                     null,
    create_by         bigint                       null,
    update_by         bigint                       null,
    tenant_id         varchar(20) default '000000' null,
    create_dept       bigint                       null
    );

-- 使用者跟投記錄表
create table if not exists biz_user_follows
(
    follow_id        bigint auto_increment primary key,
    user_id          bigint                                      not null,
    period_id        bigint                                      not null,
    period_name      varchar(50)                                 null,
    bet_amount       decimal(10, 2)                              not null,
    payout_odds      decimal(10, 2)                              null,
    status           varchar(20) default 'in_cart'         not null,
    payout_amount    decimal(10, 2)    default 0.00              null,
    created_at       timestamp         default CURRENT_TIMESTAMP not null,
    create_dept      bigint                                      null,
    create_by        bigint                                      null,
    create_time      datetime                                    null,
    update_by        bigint                                      null,
    update_time      datetime                                    null,
    tenant_id        varchar(20)       default '000000'          null,
    bet_type        varchar(20)                 null,
    bet_odds_desc    varchar(300)                                null,
    payout_odds_desc varchar(100)                                null,
    result_status    varchar(255)                                null,
    remark           varchar(200)                                null
    );

ALTER TABLE `biz_user_follows` ADD COLUMN `commission_rate` DECIMAL(6, 3) NULL;

-- 使用者邀請記錄表
create table if not exists biz_user_invitations
(
    invitation_id        bigint auto_increment primary key,
    inviter_id           bigint                                not null,
    invitee_id           bigint                                not null,
    invitation_code_used varchar(20)                           not null,
    status               varchar(20) default 'registered'      null,
    create_time          datetime    default CURRENT_TIMESTAMP null,
    update_time          datetime    default CURRENT_TIMESTAMP null,
    tenant_id            varchar(20) default '000000'          null,
    create_dept          bigint                                null,
    create_by            bigint                                null,
    update_by            bigint                                null,
    constraint uk_invitee_id
    unique (invitee_id)
    );


CREATE TABLE biz_user_progress
(
    progress_id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                     BIGINT NOT NULL,
    consecutive_losses          INT DEFAULT 0 NOT NULL,
    can_claim_reward            TINYINT DEFAULT 0 NOT NULL,
    update_time                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_dept                 BIGINT,
    create_by                   BIGINT,
    create_time                 TIMESTAMP,
    update_by                   BIGINT,
    bet_amount                  DECIMAL(18, 2),
    last_bet_amount                  DECIMAL(18, 2) DEFAULT 0,
    tenant_id                   VARCHAR(20) DEFAULT '000000',
    consecutive_losses_amount   DECIMAL(18, 2),
    bet_type                    VARCHAR(20),
    commission_rate             DECIMAL(6, 3),
    CONSTRAINT uk_user_id UNIQUE (user_id)
);
ALTER TABLE `biz_user_progress` ADD COLUMN `current_bet_amount` DECIMAL(18, 2) NULL;

-- 使用者錢包地址表
create table if not exists biz_user_wallets
(
    wallet_id             bigint auto_increment primary key,
    user_id               bigint                                null,
    address               varchar(64)                           not null,
    private_key_encrypted varchar(255)                          null,
    name                  varchar(100)                          null,
    created_at            timestamp   default CURRENT_TIMESTAMP not null,
    create_dept           bigint                                null,
    create_by             bigint                                null,
    create_time           datetime                              null,
    update_by             bigint                                null,
    update_time           datetime                              null,
    tenant_id             varchar(20) default '000000'          null,
    note                  varchar(200)                          null,
    constraint uk_address
    unique (address)
    );

-- 使用者資訊表
create table if not exists biz_users
(
    id                  int auto_increment primary key,
    username            varchar(50)                              not null,
    email               varchar(100)                             not null,
    password_hash       varchar(255)                             not null,
    full_name           varchar(100)                             null,
    profile_picture_url varchar(255)                             null,
    balance             decimal(15, 4) default 0.0000            not null,
    referral_code       varchar(20)                              not null,
    referred_by_user_id int                                      null,
    created_at          timestamp      default CURRENT_TIMESTAMP null,
    updated_at          timestamp      default CURRENT_TIMESTAMP null,
    create_dept         bigint                                   null,
    create_by           bigint                                   null,
    create_time         datetime                                 null,
    update_by           bigint                                   null,
    update_time         datetime                                 null,
    tenant_id           varchar(20)    default '000000'          null,
    constraint email
    unique (email),
    constraint referral_code
    unique (referral_code),
    constraint username
    unique (username)
    );

-- 好友推薦關係表
create table if not exists biz_referrals
(
    id               int auto_increment primary key,
    referrer_user_id int                                   not null,
    referred_user_id int                                   not null,
    status           varchar(50)                           null,
    bonus_awarded    tinyint     default 0                 not null,
    created_at       timestamp   default CURRENT_TIMESTAMP null,
    create_dept      bigint                                null,
    create_by        bigint                                null,
    create_time      datetime                              null,
    update_by        bigint                                null,
    update_time      datetime                              null,
    tenant_id        varchar(20) default '000000'          null,
    constraint referred_user_id
    unique (referred_user_id)
    );

-- 使用者提現申請表
create table if not exists biz_withdrawals
(
    withdrawal_id     bigint auto_increment primary key,
    user_id           bigint                        not null,
    amount            decimal(10, 2)                not null,
    network_fee       decimal(10, 2)                not null,
    final_amount      decimal(10, 2)                not null,
    to_wallet_address varchar(100)                  not null,
    status            varchar(20) default 'PENDING' not null,
    request_time      datetime                      not null,
    audit_by          bigint                        null,
    audit_time        datetime                      null,
    audit_remarks     varchar(255)                  null,
    tx_hash           varchar(100)                  null,
    completion_time   datetime                      null,
    create_by         varchar(64)                   null,
    create_time       datetime                      null,
    update_by         varchar(64)                   null,
    update_time       datetime                      null,
    create_dept       bigint                        null,
    tenant_id         varchar(20) default '000000'  null
    );

-- 代碼生成業務表
create table if not exists gen_table
(
    table_id          bigint                      not null primary key,
    data_name         varchar(200) default ''     null,
    table_name        varchar(200) default ''     null,
    table_comment     varchar(500) default ''     null,
    sub_table_name    varchar(64)                 null,
    sub_table_fk_name varchar(64)                 null,
    class_name        varchar(100) default ''     null,
    tpl_category      varchar(200) default 'crud' null,
    package_name      varchar(100)                null,
    module_name       varchar(30)                 null,
    business_name     varchar(30)                 null,
    function_name     varchar(50)                 null,
    function_author   varchar(50)                 null,
    gen_type          char         default '0'    null,
    gen_path          varchar(200) default '/'    null,
    options           varchar(1000)               null,
    create_dept       bigint                      null,
    create_by         bigint                      null,
    create_time       datetime                    null,
    update_by         bigint                      null,
    update_time       datetime                    null,
    remark            varchar(500)                null
    );

-- 代碼生成業務表欄位
create table if not exists gen_table_column
(
    column_id      bigint                    not null primary key,
    table_id       bigint                    null,
    column_name    varchar(200)              null,
    column_comment varchar(500)              null,
    column_type    varchar(100)              null,
    java_type      varchar(500)              null,
    java_field     varchar(200)              null,
    is_pk          char                      null,
    is_increment   char                      null,
    is_required    char                      null,
    is_insert      char                      null,
    is_edit        char                      null,
    is_list        char                      null,
    is_query       char                      null,
    query_type     varchar(200) default 'EQ' null,
    html_type      varchar(200)              null,
    dict_type      varchar(200) default ''   null,
    sort           int                       null,
    create_dept    bigint                    null,
    create_by      bigint                    null,
    create_time    datetime                  null,
    update_by      bigint                    null,
    update_time    datetime                  null
    );

-- 系統授權表
create table if not exists sys_client
(
    id             bigint              not null primary key,
    client_id      varchar(64)         null,
    client_key     varchar(32)         null,
    client_secret  varchar(255)        null,
    grant_type     varchar(255)        null,
    device_type    varchar(32)         null,
    active_timeout int  default 1800   null,
    timeout        int  default 604800 null,
    status         char default '0'    null,
    del_flag       char default '0'    null,
    create_dept    bigint              null,
    create_by      bigint              null,
    create_time    datetime            null,
    update_by      bigint              null,
    update_time    datetime            null
    );

-- 參數配置表
create table if not exists sys_config
(
    config_id    bigint                        not null primary key,
    tenant_id    varchar(20)  default '000000' null,
    config_name  varchar(100) default ''       null,
    config_key   varchar(100) default ''       null,
    config_value varchar(500) default ''       null,
    config_type  char         default 'N'      null,
    create_dept  bigint                        null,
    create_by    bigint                        null,
    create_time  datetime                      null,
    update_by    bigint                        null,
    update_time  datetime                      null,
    remark       varchar(500)                  null
    );

-- 部門表
create table if not exists sys_dept
(
    dept_id       bigint                        not null primary key,
    tenant_id     varchar(20)  default '000000' null,
    parent_id     bigint       default 0        null,
    ancestors     varchar(500) default ''       null,
    dept_name     varchar(30)  default ''       null,
    dept_category varchar(100)                  null,
    order_num     int          default 0        null,
    leader        bigint                        null,
    phone         varchar(11)                   null,
    email         varchar(50)                   null,
    status        char         default '0'      null,
    del_flag      char         default '0'      null,
    create_dept   bigint                        null,
    create_by     bigint                        null,
    create_time   datetime                      null,
    update_by     bigint                        null,
    update_time   datetime                      null
    );

-- 字典資料表
create table if not exists sys_dict_data
(
    dict_code   bigint                        not null primary key,
    tenant_id   varchar(20)  default '000000' null,
    dict_sort   int          default 0        null,
    dict_label  varchar(100) default ''       null,
    dict_value  varchar(100) default ''       null,
    dict_type   varchar(100) default ''       null,
    css_class   varchar(100)                  null,
    list_class  varchar(100)                  null,
    is_default  char         default 'N'      null,
    create_dept bigint                        null,
    create_by   bigint                        null,
    create_time datetime                      null,
    update_by   bigint                        null,
    update_time datetime                      null,
    remark      varchar(500)                  null
    );

-- 字典類型表
create table if not exists sys_dict_type
(
    dict_id     bigint                        not null primary key,
    tenant_id   varchar(20)  default '000000' null,
    dict_name   varchar(100) default ''       null,
    dict_type   varchar(100) default ''       null,
    create_dept bigint                        null,
    create_by   bigint                        null,
    create_time datetime                      null,
    update_by   bigint                        null,
    update_time datetime                      null,
    remark      varchar(500)                  null,
    constraint tenant_id_dict_type
    unique (tenant_id, dict_type)
    );

-- 系統訪問記錄
create table if not exists sys_logininfor
(
    info_id        bigint                        not null primary key,
    tenant_id      varchar(20)  default '000000' null,
    user_name      varchar(50)  default ''       null,
    client_key     varchar(32)  default ''       null,
    device_type    varchar(32)  default ''       null,
    ipaddr         varchar(128) default ''       null,
    login_location varchar(255) default ''       null,
    browser        varchar(50)  default ''       null,
    os             varchar(50)  default ''       null,
    status         char         default '0'      null,
    msg            varchar(255) default ''       null,
    login_time     datetime                      null
    );

-- 選單權限表
create table if not exists sys_menu
(
    menu_id     bigint                   not null primary key,
    menu_name   varchar(50)              not null,
    parent_id   bigint       default 0   null,
    order_num   int          default 0   null,
    path        varchar(200) default ''  null,
    component   varchar(255)             null,
    query_param varchar(255)             null,
    is_frame    int          default 1   null,
    is_cache    int          default 0   null,
    menu_type   char         default ''  null,
    visible     char         default '0' null,
    status      char         default '0' null,
    perms       varchar(100)             null,
    icon        varchar(100) default '#' null,
    create_dept bigint                   null,
    create_by   bigint                   null,
    create_time datetime                 null,
    update_by   bigint                   null,
    update_time datetime                 null,
    remark      varchar(500) default ''  null
    );

-- 通知公告表
create table if not exists sys_notice
(
    notice_id      bigint                       not null primary key,
    tenant_id      varchar(20) default '000000' null,
    notice_title   varchar(50)                  not null,
    notice_type    char                         not null,
    notice_content BLOB                         null,
    status         char        default '0'      null,
    create_dept    bigint                       null,
    create_by      bigint                       null,
    create_time    datetime                     null,
    update_by      bigint                       null,
    update_time    datetime                     null,
    remark         varchar(255)                 null
    );

-- 操作日誌記錄
create table if not exists sys_oper_log
(
    oper_id        bigint                         not null primary key,
    tenant_id      varchar(20)   default '000000' null,
    title          varchar(50)   default ''       null,
    business_type  int           default 0        null,
    method         varchar(100)  default ''       null,
    request_method varchar(10)   default ''       null,
    operator_type  int           default 0        null,
    oper_name      varchar(50)   default ''       null,
    dept_name      varchar(50)   default ''       null,
    oper_url       varchar(255)  default ''       null,
    oper_ip        varchar(128)  default ''       null,
    oper_location  varchar(255)  default ''       null,
    oper_param     varchar(4000) default ''       null,
    json_result    varchar(4000) default ''       null,
    status         int           default 0        null,
    error_msg      varchar(4000) default ''       null,
    oper_time      datetime                       null,
    cost_time      bigint        default 0        null
    );

-- OSS對象存儲表
create table if not exists sys_oss
(
    oss_id        bigint                        not null primary key,
    tenant_id     varchar(20)  default '000000' null,
    file_name     varchar(255) default ''       not null,
    original_name varchar(255) default ''       not null,
    file_suffix   varchar(10)  default ''       not null,
    url           varchar(500)                  not null,
    create_dept   bigint                        null,
    create_time   datetime                      null,
    create_by     bigint                        null,
    update_time   datetime                      null,
    update_by     bigint                        null,
    service       varchar(20)  default 'minio'  not null
    );

-- 對象存儲配置表
create table if not exists sys_oss_config
(
    oss_config_id bigint                        not null primary key,
    tenant_id     varchar(20)  default '000000' null,
    config_key    varchar(20)  default ''       not null,
    access_key    varchar(255) default ''       null,
    secret_key    varchar(255) default ''       null,
    bucket_name   varchar(255) default ''       null,
    prefix        varchar(255) default ''       null,
    endpoint      varchar(255) default ''       null,
    domain        varchar(255) default ''       null,
    is_https      char         default 'N'      null,
    region        varchar(255) default ''       null,
    access_policy char         default '1'      not null,
    status        char         default '1'      null,
    ext1          varchar(255) default ''       null,
    create_dept   bigint                        null,
    create_by     bigint                        null,
    create_time   datetime                      null,
    update_by     bigint                        null,
    update_time   datetime                      null,
    remark        varchar(500)                  null
    );

-- 崗位資訊表
create table if not exists sys_post
(
    post_id       bigint                       not null primary key,
    tenant_id     varchar(20) default '000000' null,
    dept_id       bigint                       not null,
    post_code     varchar(64)                  not null,
    post_category varchar(100)                 null,
    post_name     varchar(50)                  not null,
    post_sort     int                          not null,
    status        char                         not null,
    create_dept   bigint                       null,
    create_by     bigint                       null,
    create_time   datetime                     null,
    update_by     bigint                       null,
    update_time   datetime                     null,
    remark        varchar(500)                 null
    );

-- 角色資訊表
create table if not exists sys_role
(
    role_id             bigint                       not null primary key,
    tenant_id           varchar(20) default '000000' null,
    role_name           varchar(30)                  not null,
    role_key            varchar(100)                 not null,
    role_sort           int                          not null,
    data_scope          char        default '1'      null,
    menu_check_strictly tinyint     default 1        null,
    dept_check_strictly tinyint     default 1        null,
    status              char                         not null,
    del_flag            char        default '0'      null,
    create_dept         bigint                       null,
    create_by           bigint                       null,
    create_time         datetime                     null,
    update_by           bigint                       null,
    update_time         datetime                     null,
    remark              varchar(500)                 null
    );

-- 角色和部門關聯表
create table if not exists sys_role_dept
(
    role_id bigint not null,
    dept_id bigint not null,
    primary key (role_id, dept_id)
    );

-- 角色和選單關聯表
create table if not exists sys_role_menu
(
    role_id bigint not null,
    menu_id bigint not null,
    primary key (role_id, menu_id)
    );

-- 社會化關係表
create table if not exists sys_social
(
    id                 bigint                        not null primary key,
    user_id            bigint                        not null,
    tenant_id          varchar(20)  default '000000' null,
    auth_id            varchar(255)                  not null,
    source             varchar(255)                  not null,
    open_id            varchar(255)                  null,
    user_name          varchar(30)                   not null,
    nick_name          varchar(30)  default ''       null,
    email              varchar(255) default ''       null,
    avatar             varchar(500) default ''       null,
    access_token       varchar(255)                  not null,
    expire_in          int                           null,
    refresh_token      varchar(255)                  null,
    access_code        varchar(255)                  null,
    union_id           varchar(255)                  null,
    scope              varchar(255)                  null,
    token_type         varchar(255)                  null,
    id_token           varchar(2000)                 null,
    mac_algorithm      varchar(255)                  null,
    mac_key            varchar(255)                  null,
    code               varchar(255)                  null,
    oauth_token        varchar(255)                  null,
    oauth_token_secret varchar(255)                  null,
    create_dept        bigint                        null,
    create_by          bigint                        null,
    create_time        datetime                      null,
    update_by          bigint                        null,
    update_time        datetime                      null,
    del_flag           char         default '0'      null
    );

-- 租戶表
create table if not exists sys_tenant
(
    id                bigint           not null primary key,
    tenant_id         varchar(20)      not null,
    contact_user_name varchar(20)      null,
    contact_phone     varchar(20)      null,
    company_name      varchar(50)      null,
    license_number    varchar(30)      null,
    address           varchar(200)     null,
    intro             varchar(200)     null,
    domain            varchar(200)     null,
    remark            varchar(200)     null,
    package_id        bigint           null,
    expire_time       datetime         null,
    account_count     int  default -1  null,
    status            char default '0' null,
    del_flag          char default '0' null,
    create_dept       bigint           null,
    create_by         bigint           null,
    create_time       datetime         null,
    update_by         bigint           null,
    update_time       datetime         null
    );

-- 租戶套餐表
create table if not exists sys_tenant_package
(
    package_id          bigint                 not null primary key,
    package_name        varchar(20)            null,
    menu_ids            varchar(3000)          null,
    remark              varchar(200)           null,
    menu_check_strictly tinyint    default 1   null,
    status              char       default '0' null,
    del_flag            char       default '0' null,
    create_dept         bigint                 null,
    create_by           bigint                 null,
    create_time         datetime               null,
    update_by           bigint                 null,
    update_time         datetime               null
    );

-- 使用者資訊表
create table if not exists sys_user
(
    user_id             bigint                            not null primary key,
    tenant_id           varchar(20)    default '000000'   null,
    dept_id             bigint                            null,
    wallet_address_tron varchar(200)                      null,
    user_name           varchar(30)                       not null,
    nick_name           varchar(30)                       not null,
    user_type           varchar(10)    default 'sys_user' null,
    email               varchar(50)    default ''         null,
    phonenumber         varchar(11)    default ''         null,
    sex                 char           default '0'        null,
    avatar              bigint                            null,
    password            varchar(100)   default ''         null,
    status              char           default '0'        null,
    del_flag            char           default '0'        null,
    login_ip            varchar(128)   default ''         null,
    login_date          datetime                          null,
    create_dept         bigint                            null,
    create_by           bigint                            null,
    create_time         datetime                          null,
    update_by           bigint                            null,
    update_time         datetime                          null,
    remark              varchar(500)                      null,
    balance             decimal(14, 2) default 0.00       not null,
    balance_lock        decimal(14, 2) default 0.00       not null,
    invitation_code     varchar(20)                       null,
    inviter_id          bigint                            null,
    pay_password        varchar(100)                      null,
    pay_password_seted  int            default 0          null,
    agent_user_id       bigint                            null
    );

-- 使用者與崗位關聯表
create table if not exists sys_user_post
(
    user_id bigint not null,
    post_id bigint not null,
    primary key (user_id, post_id)
    );

-- 使用者和角色關聯表
create table if not exists sys_user_role
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
    );


alter table biz_user_follows
    add user_name varchar(100) null comment '用户名称' after user_id;

alter table biz_reward_claim
    add user_name varchar(100) null comment '用户名称' after user_id;

alter table biz_transactions
    add user_name varchar(100) null comment '用户名称' after user_id;

alter table biz_user_progress
    add user_name varchar(100) null comment '用户名称' after user_id;


alter table biz_user_wallets
    add user_name varchar(100) null comment '用户名称' after user_id;

alter table biz_withdrawals
    add user_name varchar(100) null comment '用户名称' after user_id;

alter table biz_deposit_wallets
    add user_name varchar(100) null comment '用户名称' after user_id;



alter table biz_matches
    add sporttery_match_id int null comment 'sporttery_match_id';

alter table biz_matches
    add league_name varchar(100) null comment 'LEAGUE_NAME';


-- 文件：h2-schema.sql
-- 說明：此腳本為 H2 測試資料庫定義了與業務實體類匹配的完整表結構。

-- 創建 biz_bet_orders 表 (已根據 BizBetOrders.java 更新)
CREATE TABLE IF NOT EXISTS biz_bet_orders
(
    order_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT,
    user_name        VARCHAR(50),
    odds_desc        VARCHAR(100),
    bet_amount       DECIMAL(10, 2),
    combination_type VARCHAR(50),
    status           VARCHAR(20) DEFAULT 'draft' NOT NULL CHECK (status IN ('draft', 'pending', 'won', 'lost', 'void')),
    payout_amount    DECIMAL(10, 2),
    expiration_time  DATETIME,
    create_time      DATETIME,
    update_time      DATETIME,
    create_dept      BIGINT,
    create_by        BIGINT,
    update_by        BIGINT,
    tenant_id        VARCHAR(20) DEFAULT '000000'
    );


-- 創建 biz_bet_order_details 表 (已根據 BizBetOrderDetails.java 更新)
CREATE TABLE IF NOT EXISTS biz_bet_order_details
(
    detail_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT,
    match_id    BIGINT, -- 已更新為 BIGINT
    pool_code   VARCHAR(10),
    selection   VARCHAR(50),
    odds        DECIMAL(10, 2),
    is_winning  INT, -- 已更新為 INT
    create_time DATETIME,
    update_time DATETIME,
    create_dept BIGINT,
    create_by   BIGINT,
    update_by   BIGINT,
    tenant_id   VARCHAR(20) DEFAULT '000000'
    );
