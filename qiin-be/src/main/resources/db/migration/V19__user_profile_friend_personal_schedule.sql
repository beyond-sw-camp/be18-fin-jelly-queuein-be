/*
------------------------------------------------------------------------------
user_profile
------------------------------------------------------------------------------
사용자의 프로필 표현 정보를 관리하는 테이블이다.

- user 엔티티의 핵심 식별/인증 정보와 분리된 표현(UI) 전용 데이터
- 프로필 이미지(S3 key, 접근 URL)만을 책임진다
- user_id를 PK로 사용하여 user와 1:1 관계를 유지한다
- 이미지 변경이 잦아도 user 테이블에 영향을 주지 않도록 분리됨
- FK 제약은 사용하지 않고, 논리적 FK + 인덱스로 관리한다
------------------------------------------------------------------------------
*/
CREATE TABLE `user_profile` (
                                `user_id`    BIGINT       NOT NULL COMMENT 'user PK (논리적 FK)',

                                `image_key`  VARCHAR(255) NOT NULL COMMENT 'S3 object key',
                                `image_url`  VARCHAR(500) NOT NULL COMMENT '프로필 이미지 접근 URL',

                                `created_at` TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
                                `created_by` BIGINT       NOT NULL COMMENT '생성자',
                                `updated_at` TIMESTAMP(6) NOT NULL COMMENT '수정 시각',
                                `updated_by` BIGINT       NOT NULL COMMENT '수정자',
                                `deleted_at` TIMESTAMP(6) NULL COMMENT '삭제 시각',
                                `deleted_by` BIGINT       NULL COMMENT '삭제자',

                                PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE INDEX `idx_user_profile_user_id`
    ON `user_profile` (`user_id`);



/*
------------------------------------------------------------------------------
user_friend
------------------------------------------------------------------------------
사용자 간의 친구 관계를 관리하는 테이블이다.

- 친구 요청, 수락, 거절, 차단 상태를 관리한다
- requester_id -> receiver_id 방향의 관계를 명시적으로 표현한다
- 복합 PK(requester_id, receiver_id)를 사용하여 중복 관계를 방지한다
- 상태 기반 모델로 Notification, SSE와 쉽게 연동 가능하다
- FK 제약 없이 인덱스를 통해 조회 성능을 보장한다
------------------------------------------------------------------------------
*/
CREATE TABLE `user_friend` (
                               `requester_id` BIGINT NOT NULL COMMENT '친구 요청자 user_id',
                               `receiver_id`  BIGINT NOT NULL COMMENT '친구 요청 대상 user_id',

                               `status`       INT NOT NULL COMMENT '0=PENDING,1=ACCEPTED,2=REJECTED,3=BLOCKED',

                               `created_at`   TIMESTAMP(6) NOT NULL COMMENT '요청 시각',
                               `created_by`   BIGINT       NOT NULL COMMENT '요청자',
                               `updated_at`   TIMESTAMP(6) NOT NULL COMMENT '상태 변경 시각',
                               `updated_by`   BIGINT       NOT NULL COMMENT '상태 변경자',
                               `deleted_at`   TIMESTAMP(6) NULL COMMENT '삭제 시각',
                               `deleted_by`   BIGINT       NULL COMMENT '삭제자',

                               PRIMARY KEY (`requester_id`, `receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE INDEX `idx_user_friend_requester`
    ON `user_friend` (`requester_id`);

CREATE INDEX `idx_user_friend_receiver_status`
    ON `user_friend` (`receiver_id`, `status`);



/*
------------------------------------------------------------------------------
user_schedule
------------------------------------------------------------------------------
사용자의 개인 일정을 관리하는 테이블이다.

- 자원 예약(reservation)과 무관한 개인 캘린더 도메인
- 일정의 소유자는 user_id로 명확히 구분된다
- 월/주/일 단위 조회를 고려한 시간 범위(start_at, end_at) 구조
- 개인 메모 및 친구 초대 기능의 기준 엔티티 역할을 한다
------------------------------------------------------------------------------
*/
CREATE TABLE `user_schedule` (
                                 `schedule_id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `user_id`     BIGINT NOT NULL COMMENT '일정 소유자 user_id',

                                 `title`       VARCHAR(200) NOT NULL COMMENT '일정 제목',
                                 `start_at`    TIMESTAMP(6) NOT NULL COMMENT '시작 시각',
                                 `end_at`      TIMESTAMP(6) NOT NULL COMMENT '종료 시각',

                                 `created_at`  TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
                                 `created_by`  BIGINT       NOT NULL COMMENT '생성자',
                                 `updated_at`  TIMESTAMP(6) NOT NULL COMMENT '수정 시각',
                                 `updated_by`  BIGINT       NOT NULL COMMENT '수정자',
                                 `deleted_at`  TIMESTAMP(6) NULL COMMENT '삭제 시각',
                                 `deleted_by`  BIGINT       NULL COMMENT '삭제자',

                                 PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE INDEX `idx_user_schedule_user_id`
    ON `user_schedule` (`user_id`);

CREATE INDEX `idx_user_schedule_user_period`
    ON `user_schedule` (`user_id`, `start_at`, `end_at`);



/*
------------------------------------------------------------------------------
user_schedule_memo
------------------------------------------------------------------------------
개인 일정(user_schedule)에 속한 메모를 관리하는 테이블이다.

- 일정 하나에 여러 개의 메모를 작성할 수 있다
- 메모의 작성자(author_id)를 명시적으로 관리한다
- 기본 정책은 작성자만 수정/삭제 가능
- 일정 상세 화면의 보조 정보 역할을 수행한다
------------------------------------------------------------------------------
*/
CREATE TABLE `user_schedule_memo` (
                                      `memo_id`     BIGINT NOT NULL AUTO_INCREMENT,
                                      `schedule_id` BIGINT NOT NULL COMMENT 'user_schedule PK',
                                      `author_id`   BIGINT NOT NULL COMMENT '메모 작성자 user_id',

                                      `content`     VARCHAR(2000) NOT NULL COMMENT '메모 내용',

                                      `created_at`  TIMESTAMP(6) NOT NULL COMMENT '생성 시각',
                                      `created_by`  BIGINT       NOT NULL COMMENT '생성자',
                                      `updated_at`  TIMESTAMP(6) NOT NULL COMMENT '수정 시각',
                                      `updated_by`  BIGINT       NOT NULL COMMENT '수정자',
                                      `deleted_at`  TIMESTAMP(6) NULL COMMENT '삭제 시각',
                                      `deleted_by`  BIGINT       NULL COMMENT '삭제자',

                                      PRIMARY KEY (`memo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE INDEX `idx_user_schedule_memo_schedule_id`
    ON `user_schedule_memo` (`schedule_id`);

CREATE INDEX `idx_user_schedule_memo_author_id`
    ON `user_schedule_memo` (`author_id`);



/*
------------------------------------------------------------------------------
user_schedule_participant
------------------------------------------------------------------------------
개인 일정(user_schedule)에 참여하는 사용자를 관리하는 매핑 테이블이다.

- 일정 소유자가 자신의 친구를 일정에 초대할 수 있다
- 친구 관계(user_friend)가 성립된 사용자만 추가하는 것을 전제로 한다
- 단순한 관계 테이블로 별도 상태 컬럼은 두지 않는다
- 복합 PK(schedule_id, user_id)로 중복 참여를 방지한다
------------------------------------------------------------------------------
*/
CREATE TABLE `user_schedule_participant` (
                                             `schedule_id` BIGINT NOT NULL COMMENT 'user_schedule PK',
                                             `user_id`     BIGINT NOT NULL COMMENT '참여 user_id',

                                             PRIMARY KEY (`schedule_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE INDEX `idx_user_schedule_participant_schedule_id`
    ON `user_schedule_participant` (`schedule_id`);

CREATE INDEX `idx_user_schedule_participant_user_id`
    ON `user_schedule_participant` (`user_id`);
