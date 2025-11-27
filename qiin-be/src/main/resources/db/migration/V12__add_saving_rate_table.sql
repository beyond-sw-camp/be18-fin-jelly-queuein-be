CREATE TABLE saving_rate
(
    saving_rate_id BIGINT         NOT NULL AUTO_INCREMENT COMMENT '절감률 PK',
    year           INT            NOT NULL COMMENT '해당 년도',
    target_rate    DECIMAL(12, 3) NOT NULL COMMENT '해당 년도 목표 절감률',
    editable       BOOLEAN        NOT NULL COMMENT '수정 가능 여부(true=가능, false=불가)',
    created_at     TIMESTAMP(6)   NOT NULL COMMENT '생성 시각',
    created_by     BIGINT         NOT NULL COMMENT '생성자',
    updated_at     TIMESTAMP(6) NULL COMMENT '수정 시각',
    updated_by     BIGINT NULL COMMENT '수정자',
    deleted_at     TIMESTAMP(6) NULL COMMENT '삭제 시각',
    deleted_by     BIGINT NULL COMMENT '삭제자',

    PRIMARY KEY (saving_rate_id),
    UNIQUE KEY uq_saving_rate_year (year)
)
