-- ================================
-- 1) SETTLEMENT 테이블 수정
-- ================================

ALTER TABLE settlement
    MODIFY COLUMN usage_hours INT NULL COMMENT '사용 시간 (분 단위)',
    MODIFY COLUMN available_hours INT NULL COMMENT '가용 시간 (분 단위)';


-- ================================
-- 2) USAGE_HISTORY 테이블 수정
-- ================================

ALTER TABLE usage_history
    MODIFY COLUMN actual_usage_time INT NULL COMMENT '실제 사용시간(분)',
    MODIFY COLUMN usage_time INT NULL COMMENT '예약 사용시간(분)';


-- 기존 TIMESTAMP 타입이 잘못 들어간 상태라면 아래 주석을 참고해 처리
-- 만약 기존 컬럼 이름이 TIMESTAMP 로 잘못 만들어졌다면 drop + add 로 강제 수정 가능
-- 예시:
-- ALTER TABLE usage_history DROP COLUMN actual_usage_time;
-- ALTER TABLE usage_history ADD COLUMN actual_usage_time INT NULL COMMENT '실제 사용시간(분)';

