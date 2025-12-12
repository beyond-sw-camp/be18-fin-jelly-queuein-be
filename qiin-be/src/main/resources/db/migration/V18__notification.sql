DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification` (
                                `notification_id` BIGINT NOT NULL AUTO_INCREMENT,

                                `receiver_id` BIGINT NOT NULL COMMENT '알림 수신자 ID',
                                `aggregate_id` BIGINT NOT NULL COMMENT '예약 등 연관 리소스 ID',

                                `message` VARCHAR(2000) NOT NULL COMMENT '알림 메시지',

                                `status` INT NOT NULL DEFAULT 0 COMMENT '알림 상태(PENDING=0, SENT=1, FAILED=2)',
                                `type` INT NOT NULL COMMENT '알림 타입 코드(NotificationType)',

                                `payload` JSON NOT NULL COMMENT '알림 메타데이터(JSON)',

                                `is_read` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '읽음 여부',

                                `delivered_at` TIMESTAMP(6) NULL COMMENT '전송 완료 시각',
                                `read_at` TIMESTAMP(6) NULL COMMENT '읽은 시각',

                                `created_at` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시각',
                                `deleted_at` TIMESTAMP(6) NULL COMMENT 'Soft delete 시각',

                                PRIMARY KEY (`notification_id`),


                                INDEX `idx_notification_receiver` (`receiver_id`),
                                INDEX `idx_notification_created_at` (`created_at`),
                                INDEX `idx_notification_status` (`status`)
);
