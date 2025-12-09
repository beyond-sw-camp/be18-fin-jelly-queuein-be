ALTER TABLE reservation
    ADD COLUMN is_applied TINYINT(1) NOT NULL DEFAULT 0 COMMENT '승인 여부(0=대기,1=승인)';
