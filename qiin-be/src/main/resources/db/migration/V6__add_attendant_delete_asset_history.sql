DROP TABLE IF EXISTS `asset_history`;

DROP TABLE IF EXISTS `user_rev`;
CREATE TABLE `attendant`
(
    `attendant_id`    BIGINT(20)	NOT NULL	COMMENT '참여자 PK',
    `reservation_id` BIGINT(20)	NOT NULL	COMMENT '예약 FK',
    `user_id`        BIGINT(20)	NOT NULL	COMMENT '사용자 FK',
    `created_at`  TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
    `created_by`  BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`  TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by`  BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`  TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by`  BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (attendant_id)
);

ALTER TABLE `attendant` MODIFY `attendant_id` BIGINT(20) NOT NULL AUTO_INCREMENT;

CREATE INDEX idx_attendant_reservation_id ON attendant (reservation_id);
CREATE INDEX idx_attendant_user_id ON attendant (user_id);