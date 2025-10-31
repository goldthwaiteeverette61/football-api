-- 为确保测试环境的表结构正确，直接重建 biz_match_results 表
DROP TABLE IF EXISTS biz_match_results;
CREATE TABLE biz_match_results (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   match_id INT NOT NULL,
                                   pool_id BIGINT NULL,
                                   pool_code VARCHAR(10) NULL,
                                   combination VARCHAR(20) NULL,
                                   combination_desc VARCHAR(50) NULL,
                                   goal_line VARCHAR(10) NULL,
                                   odds DECIMAL(10, 2) NULL,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                   create_dept BIGINT NULL,
                                   create_by BIGINT NULL,
                                   create_time DATETIME NULL,
                                   update_by BIGINT NULL,
                                   update_time DATETIME NULL,
                                   tenant_id VARCHAR(20) DEFAULT '000000' NULL,
                                   CONSTRAINT uk_match_pool UNIQUE (match_id, pool_code)
);
CREATE INDEX idx_match_id ON biz_match_results(match_id);


-- 清理其他与比赛数据采集相关的表，确保环境干净
DELETE FROM biz_odds;
DELETE FROM biz_matches;
DELETE FROM biz_teams;
DELETE FROM biz_leagues;

