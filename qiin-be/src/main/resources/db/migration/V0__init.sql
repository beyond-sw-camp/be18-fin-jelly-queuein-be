CREATE TABLE `user`
(
    `user_id`     BIGINT(20)	NOT NULL	COMMENT '사용자 PK',
    `dpt_id`      BIGINT(20)	NOT NULL	COMMENT '부서 FK',
    `user_no`     VARCHAR(50)  NOT NULL COMMENT '사번(로그인 ID) UNIQUE',
    `user_name`   VARCHAR(100) NOT NULL COMMENT '사원명',
    `email`       VARCHAR(100) NOT NULL COMMENT '사내이메일 UNIQUE',
    `password`    VARCHAR(255) NOT NULL COMMENT '비밀번호',
    `phone`       VARCHAR(20) NULL	COMMENT '연락처',
    `birth`       VARCHAR(10) NULL	COMMENT '생년월일',
    `hire_date`   TIMESTAMP(6) NOT NULL COMMENT '입사일자',
    `retire_date` TIMESTAMP(6) NULL	COMMENT '퇴사일자',
    `created_at`  TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
    `created_by`  BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`  TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by`  BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`  TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by`  BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (user_id),
    UNIQUE KEY `uk_user_name` (`user_name`),
    UNIQUE KEY `uk_email` (`email`)
);

CREATE TABLE `user_rev`
(
    `user_rev_id`    BIGINT(20)	NOT NULL	COMMENT '참여자 PK',
    `reservation_id` BIGINT(20)	NOT NULL	COMMENT '예약 FK',
    `user_id`        BIGINT(20)	NOT NULL	COMMENT '사용자 FK',
    PRIMARY KEY (user_rev_id)
);

CREATE TABLE `user_role`
(
    `user_role_id` BIGINT(20)	NOT NULL	COMMENT '사용자-역할 매핑 PK',
    `user_id`      BIGINT(20)	NOT NULL	COMMENT '사용자 FK',
    `role_id`      BIGINT(20)	NOT NULL	COMMENT '역할 FK',
    PRIMARY KEY (user_role_id)
);

CREATE TABLE `user_history`
(
    `user_history_id`  BIGINT(20)	NOT NULL	COMMENT '실제 참여자PK',
    `user_id`          BIGINT(20)	NOT NULL	COMMENT '사용자FK',
    `usage_history_id` BIGINT(20)	NOT NULL	COMMENT '자원사용기록 PK',
    PRIMARY KEY (user_history_id)
);

CREATE TABLE `permission`
(
    `permission_id`          BIGINT(20)	NOT NULL	COMMENT '권한 PK',
    `permission_name`        VARCHAR(100) NOT NULL COMMENT '권한명 (예: 자원 예약, 예약 승인, 자원 관리) UNIQUE',
    `permission_description` VARCHAR(255) NULL	COMMENT '권한 설명',
    `created_at`             TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
    `created_by`             BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`             TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by`             BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`             TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by`             BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (permission_id)
);

CREATE TABLE `department`
(
    `dpt_id`     BIGINT(20)	NOT NULL	COMMENT '부서 PK',
    `dpt_name`   VARCHAR(50)  NOT NULL COMMENT '부서명',
    `created_at` TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
    `created_by` BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at` TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by` BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at` TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by` BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (dpt_id)
);

CREATE TABLE `department_closure`
(
    `ancestor_id`   BIGINT(20)	NOT NULL	COMMENT '상위 부서 ID',
    `descendant_id` BIGINT(20)	NOT NULL	COMMENT '하위 부서 ID',
    `depth`         INT NOT NULL COMMENT '관계 깊이 (0은 자기 자신)'
);

CREATE TABLE `role`
(
    `role_id`          BIGINT(20)	NOT NULL	COMMENT '역할 PK',
    `role_name`        VARCHAR(50)  NOT NULL COMMENT '역할명',
    `role_description` VARCHAR(255) NOT NULL COMMENT '역할 설명',
    `created_at`       TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
    `created_by`       BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`       TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by`       BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`       TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by`       BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (role_id),
    UNIQUE KEY `uk_role_name` (`role_name`)
);

CREATE TABLE `role_permission`
(
    `role_permission_id` BIGINT(20)	NOT NULL	COMMENT '역할-권한 매핑 PK',
    `role_id`            BIGINT(20)	NOT NULL	COMMENT '역할 FK',
    `permission_id`      BIGINT(20)	NOT NULL	COMMENT '권한 FK'
);

CREATE TABLE `asset`
(
    `asset_id`        BIGINT(20)	NOT NULL	COMMENT '자원 PK',
    `parent_asset_id` BIGINT(20)	NULL	COMMENT '부모 자원 FK',
    `category_id`     BIGINT(20)	NOT NULL	COMMENT '카테고리 FK',
    `name`            VARCHAR(100)   NOT NULL COMMENT '자원 이름 (예: 고성능 현미경 #1)',
    `description`     VARCHAR(500) NULL	COMMENT '자원 설명',
    `image`           VARCHAR(255) NULL	COMMENT '자원 이미지',
    `status`          INT            NOT NULL DEFAULT '0' COMMENT '자원 상태',
    `type`            INT            NOT NULL COMMENT '자원 유형(동적, 정적)',
    `access_level`    INT            NOT NULL COMMENT '자원에 대한 인가 수준',
    `approval_status` BOOLEAN        NOT NULL DEFAULT FALSE COMMENT '자원 예약시 승인이 필요한지',
    `cost_per_hour`   DECIMAL(12, 3) NOT NULL DEFAULT 0.000 COMMENT '시간당 가상 청구 비용',
    `period_cost`     DECIMAL(12, 3) NOT NULL DEFAULT 0.000 COMMENT '단위 기간(월, 주 등) 고정비',
    `version`         BIGINT(20)	NOT NULL	COMMENT '낙관적 락 버전',
    `created_at`      TIMESTAMP(6)   NOT NULL COMMENT '생성 시각',
    `created_by`      BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`      TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by`      BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`      TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by`      BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (asset_id)
);

CREATE TABLE `asset_history`
(
    `asset_history_id` BIGINT(20)	NOT NULL	COMMENT '자원 수정 이력 PK',
    `asset_id`         BIGINT(20)	NOT NULL	COMMENT '자원 FK',
    `parent_id`        BIGINT(20)	NULL	COMMENT '부모 자원 FK',
    `category_id`      BIGINT(20)	NOT NULL	COMMENT '카테고리 PK',
    `name`             VARCHAR(100)   NOT NULL COMMENT '자원 이름 (예: 고성능 현미경 #1)',
    `description`      VARCHAR(500) NULL	COMMENT '자원 설명',
    `image`            VARCHAR(255) NULL	COMMENT '자원 이미지',
    `status`           INT NULL	DEFAULT '0'	COMMENT '자원 상태',
    `type`             INT            NOT NULL COMMENT '자원 유형(동적, 정적)',
    `access_level`     INT NULL	COMMENT '자원에 대한 인가 수준',
    `approval_status`  BOOLEAN NULL	DEFAULT FALSE	COMMENT '자원 예약시 승인이 필요한지',
    `cost_per_hour`    DECIMAL(12, 3) NOT NULL DEFAULT 0.000 COMMENT '시간당 가상 청구 비용',
    `period_cost`      DECIMAL(12, 3) NOT NULL DEFAULT 0.000 COMMENT '단위 기간(월, 주 등) 고정비',
    `version`          BIGINT(20)	NOT NULL	DEFAULT 0	COMMENT '낙관적 락 버전',
    `created_at`       TIMESTAMP(6)   NOT NULL COMMENT '생성 시각',
    `created_by`       BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`       TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by`       BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`       TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by`       BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (asset_history_id)
);

CREATE TABLE `asset_closure`
(
    `ancestor_id`   BIGINT(20)	NOT NULL	COMMENT '상위(조상) 자원 ID',
    `descendant_id` BIGINT(20)	NOT NULL	COMMENT '하위(자손) 자원 ID',
    `depth`         INT NOT NULL COMMENT '계층 깊이 (0=본인)'
);

CREATE TABLE `category`
(
    `category_id` BIGINT(20)	NOT NULL	COMMENT '카테고리 PK',
    `name`        VARCHAR(50)  NOT NULL COMMENT 'ex)회의실, 실험실, 장비',
    `description` VARCHAR(500) NULL	COMMENT '회의용 공간, 실험용 공간, 현미경, 장비 등등',
    `created_at`  TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
    `created_by`  BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`  TIMESTAMP(6) NULL	COMMENT '수정 시각',
    `updated_by`  BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`  TIMESTAMP(6) NULL	COMMENT '삭제 시각',
    `deleted_by`  BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (category_id)
);

CREATE TABLE `reservation`
(
    `reservation_id`  BIGINT(20)	NOT NULL	COMMENT '예약PK',
    `applicant_id`    BIGINT(20)	NOT NULL	COMMENT '예약을 요청한 사용자FK',
    `respondent_id`   BIGINT(20)	NULL	COMMENT '예약 승인한 사용자FK',
    `asset_id`        BIGINT(20)	NOT NULL	COMMENT '자원 FK',
    `start_at`        TIMESTAMP(6) NOT NULL COMMENT '예약 시작 시간',
    `end_at`          TIMESTAMP(6) NOT NULL COMMENT '예약 종료 시간',
    `actual_start_at` TIMESTAMP(6) NULL	COMMENT '예약 실제 시작 시간',
    `actual_end_at`   TIMESTAMP(6) NULL	COMMENT '예약 실제 종료 시간',
    `status`          INT          NOT NULL DEFAULT 0 COMMENT '예약 상태',
    `description`     VARCHAR(500) NULL	COMMENT '예약에 대한 설명',
    `version`         BIGINT(20)	NOT NULL	DEFAULT 0	COMMENT '낙관적 락 버전',
    `is_approved`     BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '승인 여부',
    `reason`          VARCHAR(255) NULL	COMMENT '승인/반려 사유',
    `created_at`      TIMESTAMP(6) NOT NULL COMMENT '생성시간',
    `created_by`      BIGINT(20)	NOT NULL	COMMENT '생성자',
    `updated_at`      TIMESTAMP(6) NULL	COMMENT '수정시간',
    `updated_by`      BIGINT(20)	NULL	COMMENT '수정자',
    `deleted_at`      TIMESTAMP(6) NULL	COMMENT '삭제시간',
    `deleted_by`      BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (reservation_id)
);

CREATE TABLE `settlement`
(
    `settlement_id`          BIGINT(20)	NOT NULL	COMMENT '정산PK',
    `usage_history_id`       BIGINT(20)	NOT NULL	COMMENT '자원사용기록 PK',
    `asset_id`               BIGINT(20)	NOT NULL	COMMENT '자원 FK',
    `usage_hours`            DECIMAL(12, 3) NULL	COMMENT '사용 시간 (분 / 60) 분자',
    `available_hours`        DECIMAL(12, 3) NULL	COMMENT '가용 시간 (분 / 60) 분모',
    `cost_per_hour_snapshot` DECIMAL(12, 3) NULL	COMMENT '정산 시점의 자원 단가',
    `total_usage_cost`       DECIMAL(12, 3) NULL	COMMENT '사용 시간 × 단가',
    `period_cost_share`      DECIMAL(12, 3) NULL	COMMENT '정산 기간에서 이 자원이 차지하는 고정비 비율',
    `created_at`             TIMESTAMP(6) NOT NULL COMMENT '생성시간',
    `created_by`             BIGINT(20)	NOT NULL	COMMENT '생성자',
    `deleted_at`             TIMESTAMP(6) NULL	COMMENT '삭제시간',
    `deleted_by`             BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (settlement_id)
);

CREATE TABLE `notification`
(
    `event_outbox_id` BIGINT(20)	NOT NULL	COMMENT '이벤트PK',
    `event_type`      VARCHAR(100) NOT NULL COMMENT '이벤트 타입 (예: USAGE_COMPLETED)',
    `payload`         JSON         NOT NULL COMMENT '이벤트 데이터(JSON)',
    `is_published`    BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '발행 여부',
    PRIMARY KEY (event_outbox_id)
);

CREATE TABLE `usage_history`
(
    `usage_history_id`  BIGINT(20)	NOT NULL	COMMENT '자원사용기록 PK',
    `asset_id`          BIGINT(20)	NOT NULL	COMMENT '자원 FK',
    `reservation_id`    BIGINT(20)	NOT NULL	COMMENT '예약FK',
    `actual_start_at`   TIMESTAMP(6) NOT NULL COMMENT '실제 사용 시작',
    `actual_end_at`     TIMESTAMP(6) NOT NULL COMMENT '실제 사용 종료',
    `actual_usage_time` TIMESTAMP(6) NULL	COMMENT '사용 시간(분) - 계산 컬럼',
    `start_at`          TIMESTAMP(6) NULL	COMMENT '예약시작시간',
    `end_at`            TIMESTAMP(6) NULL	COMMENT '예약종료시간',
    `usage_time`        TIMESTAMP(6) NULL	COMMENT '예약사용시간',
    `usage_ratio`       DECIMAL(12, 3) NULL	COMMENT '예약 대비 실제 사용률(actual_usage_time/ usage_time)',
    `created_at`        TIMESTAMP(6) NOT NULL COMMENT '생성시간',
    `created_by`        BIGINT(20)	NOT NULL	COMMENT '생성자',
    `deleted_at`        TIMESTAMP(6) NULL	COMMENT '삭제시간',
    `deleted_by`        BIGINT(20)	NULL	COMMENT '삭제자',
    PRIMARY KEY (usage_history_id)
);

-- 인덱스
-- 사용자
CREATE INDEX idx_user_dpt_id ON user (dpt_id);

-- 부서 closure
CREATE UNIQUE INDEX idx_department_closure_ancestor_descendant_id ON department_closure (ancestor_id, descendant_id);
CREATE INDEX idx_department_closure_ancestor_id ON department_closure (ancestor_id);
CREATE INDEX idx_department_closure_descendant_id ON department_closure (descendant_id);

-- 사용자 역할
CREATE INDEX idx_user_role_user_id ON user_role (user_id);
CREATE INDEX idx_user_role_role_id ON user_role (role_id);

-- 역할 권한
CREATE INDEX idx_role_permission_role_id ON role_permission (role_id);
CREATE INDEX idx_role_permission_permission_id ON role_permission (permission_id);

-- 자원
CREATE INDEX idx_asset_parent_asset_id ON asset (parent_asset_id);
CREATE INDEX idx_asset_category_id ON asset (category_id);

-- 자원 수정 이력
CREATE INDEX idx_asset_history_asset_id ON asset_history (asset_id);
CREATE INDEX idx_asset_history_parent_id ON asset_history (parent_id); -- 필요 시
CREATE INDEX idx_asset_history_category_id ON asset_history (category_id);

-- 자원 closure
CREATE UNIQUE INDEX idx_asset_closure_ancestor_descendant_id ON asset_closure (ancestor_id);
CREATE INDEX idx_asset_closure_ancestor_id ON asset_closure (ancestor_id);
CREATE INDEX idx_asset_closure_descendant_id ON asset_closure (descendant_id);

-- 자원 사용 기록
CREATE INDEX idx_usage_history_asset_id ON usage_history (asset_id);
CREATE INDEX idx_usage_history_reservation_id ON usage_history (reservation_id);

-- 정산
CREATE INDEX idx_settlement_usage_history_id ON settlement (usage_history_id);
CREATE INDEX idx_settlement_asset_id ON settlement (asset_id);

-- 실제 참여자
CREATE INDEX idx_user_history_user_id ON user_history (user_id);
CREATE INDEX idx_user_history_usage_history_id ON user_history (usage_history_id);

-- 예약
CREATE INDEX idx_reservation_applicant_id ON reservation (applicant_id);
CREATE INDEX idx_reservation_respondent_id ON reservation (respondent_id);
CREATE INDEX idx_reservation_asset_id ON reservation (asset_id);

-- user rev
CREATE INDEX idx_user_rev_reservation_id ON user_rev (reservation_id);
CREATE INDEX idx_user_rev_user_id ON user_rev (user_id);

