ALTER TABLE `usage_history`
    MODIFY COLUMN `actual_usage_time` INT NOT NULL COMMENT '실제 사용 시간(분)',
    MODIFY COLUMN `start_at` TIMESTAMP(6) NOT NULL COMMENT '예약 시작 시간',
    MODIFY COLUMN `end_at` TIMESTAMP(6) NOT NULL COMMENT '예약 종료 시간',
    MODIFY COLUMN `usage_time` INT NOT NULL COMMENT '예약 사용 시간(분)',
    MODIFY COLUMN `usage_ratio` DECIMAL(12,3) NOT NULL COMMENT '예약 대비 실제 사용률';

ALTER TABLE `usage_history`
DROP COLUMN `created_by`,
    DROP COLUMN `deleted_at`,
    DROP COLUMN `deleted_by`;

ALTER TABLE settlement
    CHANGE COLUMN `available_hours` `actual_usage_time` INT NOT NULL COMMENT '실제 사용 시간';

ALTER TABLE `settlement`
    MODIFY COLUMN `usage_hours` INT NOT NULL COMMENT '예약 사용 시간',
    MODIFY COLUMN `cost_per_hour_snapshot` DECIMAL(12,3) NOT NULL COMMENT '정산 시점의 자원 단가',
    MODIFY COLUMN `total_usage_cost` DECIMAL(12,3) NOT NULL COMMENT '예약 사용 시간 x 단가',
    MODIFY COLUMN `period_cost_share` DECIMAL(12,3) NOT NULL COMMENT '정산 기간 내 고정비 배분 비율';

ALTER TABLE `settlement`
    ADD COLUMN `actual_usage_cost` DECIMAL(12,3) NOT NULL COMMENT '실제 사용 시간 × 단가' AFTER `total_usage_cost`;

ALTER TABLE `settlement`
DROP COLUMN `created_by`,
    DROP COLUMN `deleted_at`,
    DROP COLUMN `deleted_by`;