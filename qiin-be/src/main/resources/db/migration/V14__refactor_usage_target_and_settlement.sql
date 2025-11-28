DROP TABLE IF EXISTS `saving_rate`;
DROP TABLE IF EXISTS `settlement`;

CREATE TABLE IF NOT EXISTS `usage_target`
(
    `usage_target_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '목표 사용률 PK',
    `year`            INT NOT NULL COMMENT '해당 년도',
    `target_rate`     DECIMAL(12,3) NOT NULL COMMENT '해당 년도 목표 사용률(%)',
    `created_at`      TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
    `created_by`      BIGINT(20) NOT NULL COMMENT '생성자',
    PRIMARY KEY (`usage_target_id`),
    UNIQUE KEY `uq_usage_target_year` (`year`)
    );

CREATE TABLE IF NOT EXISTS `settlement`
(
    `settlement_id`        BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '정산PK',
    `usage_history_id`     BIGINT(20) NOT NULL COMMENT '자원사용기록 FK',
    `asset_id`             BIGINT(20) NOT NULL COMMENT '자원 FK',
    `usage_target_id`      BIGINT(20) NOT NULL COMMENT '목표 사용률 PK',
    `cost_per_hour_snapshot` DECIMAL(12,3) NOT NULL COMMENT '정산 시점의 자원 단가',
    `total_usage_cost`     DECIMAL(12,3) NOT NULL COMMENT '예약 사용 시간 × 단가',
    `actual_usage_cost`    DECIMAL(12,3) NOT NULL COMMENT '실제 사용 시간 × 단가',
    `usage_gap_cost`       DECIMAL(12,3) NOT NULL COMMENT '손익 금액',
    `created_at`           TIMESTAMP(6) NOT NULL COMMENT '생성시간',

    PRIMARY KEY (`settlement_id`)
    );

CREATE INDEX `idx_settlement_usage_history_id` ON `settlement` (`usage_history_id`);
CREATE INDEX `idx_settlement_asset_id` ON `settlement` (`asset_id`);
CREATE INDEX `idx_settlement_usage_target_id` ON `settlement` (`usage_target_id`);