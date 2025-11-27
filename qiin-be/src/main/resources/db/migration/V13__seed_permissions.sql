INSERT INTO `permission`
(
    `permission_name`,
    `permission_description`,
    `created_at`,
    `created_by`,
    `updated_at`,
    `updated_by`
)
VALUES
    -- ======================================
    -- IAM - USER
    -- ======================================
    ('IAM_USER_READ',               '사용자 목록 및 상세 조회',                             NOW(6), 0, NOW(6), 0),
    ('IAM_USER_CREATE',             '사용자 생성',                                         NOW(6), 0, NOW(6), 0),
    ('IAM_USER_UPDATE',             '사용자 정보 수정',                                   NOW(6), 0, NOW(6), 0),
    ('IAM_USER_DELETE',             '사용자 삭제(소프트 딜리트)',                          NOW(6), 0, NOW(6), 0),
    ('IAM_USER_PASSWORD_CHANGE',    '사용자 비밀번호 변경(관리자/본인 변경 포함)',         NOW(6), 0, NOW(6), 0),
    ('IAM_USER_TEMP_PASSWORD_ISSUE','임시 비밀번호 발급',                                 NOW(6), 0, NOW(6), 0),
    ('IAM_USER_ME_READ',            '내 정보 조회',                                       NOW(6), 0, NOW(6), 0),
    ('IAM_USER_ME_PASSWORD_CHANGE', '내 비밀번호 변경',                                   NOW(6), 0, NOW(6), 0),

    -- ======================================
    -- IAM - ROLE
    -- ======================================
    ('IAM_ROLE_READ',               '역할 조회',                                          NOW(6), 0, NOW(6), 0),
    ('IAM_ROLE_CREATE',             '역할 생성',                                          NOW(6), 0, NOW(6), 0),
    ('IAM_ROLE_UPDATE',             '역할 수정',                                          NOW(6), 0, NOW(6), 0),
    ('IAM_ROLE_DELETE',             '역할 삭제(소프트 딜리트)',                           NOW(6), 0, NOW(6), 0),

    -- ======================================
    -- IAM - PERMISSION 관리
    -- ======================================
    ('IAM_PERMISSION_READ',         '권한(Permission) 조회',                              NOW(6), 0, NOW(6), 0),
    ('IAM_PERMISSION_CREATE',       '권한 생성',                                          NOW(6), 0, NOW(6), 0),
    ('IAM_PERMISSION_UPDATE',       '권한 수정',                                          NOW(6), 0, NOW(6), 0),
    ('IAM_PERMISSION_DELETE',       '권한 삭제(소프트 딜리트)',                           NOW(6), 0, NOW(6), 0),

    -- ======================================
    -- IAM - ROLE_PERMISSION 매핑 관리
    -- ======================================
    ('IAM_ROLE_PERMISSION_READ',    '역할-권한 매핑 조회',                                NOW(6), 0, NOW(6), 0),
    ('IAM_ROLE_PERMISSION_ADD',     '역할에 권한 추가',                                   NOW(6), 0, NOW(6), 0),
    ('IAM_ROLE_PERMISSION_REPLACE', '역할 권한 일괄 수정',                                NOW(6), 0, NOW(6), 0),
    ('IAM_ROLE_PERMISSION_REMOVE',  '역할에서 권한 제거',                                 NOW(6), 0, NOW(6), 0),

    -- ======================================
    -- INVENTORY - CATEGORY
    -- ======================================
    ('INVENTORY_CATEGORY_READ',     '카테고리 조회',                                      NOW(6), 0, NOW(6), 0),
    ('INVENTORY_CATEGORY_CREATE',   '카테고리 생성',                                      NOW(6), 0, NOW(6), 0),
    ('INVENTORY_CATEGORY_UPDATE',   '카테고리 수정',                                      NOW(6), 0, NOW(6), 0),
    ('INVENTORY_CATEGORY_DELETE',   '카테고리 삭제(소프트 딜리트)',                       NOW(6), 0, NOW(6), 0),

    -- ======================================
    -- INVENTORY - ASSET
    -- ======================================
    ('INVENTORY_ASSET_READ',                '자산 조회',                                  NOW(6), 0, NOW(6), 0),
    ('INVENTORY_ASSET_CREATE',              '자산 생성',                                  NOW(6), 0, NOW(6), 0),
    ('INVENTORY_ASSET_UPDATE',              '자산 수정',                                  NOW(6), 0, NOW(6), 0),
    ('INVENTORY_ASSET_DELETE',              '자산 삭제(소프트 딜리트)',                   NOW(6), 0, NOW(6), 0),
    ('INVENTORY_ASSET_STATUS_UPDATE',       '자산 상태 변경',                             NOW(6), 0, NOW(6), 0),
    ('INVENTORY_ASSET_ACCESS_LEVEL_UPDATE', '자산 접근 레벨 변경',                        NOW(6), 0, NOW(6), 0),

    -- ======================================
    -- INVENTORY - ASSET TREE
    -- ======================================
    ('INVENTORY_ASSET_TREE_READ',   '자산 트리 구조 조회',                               NOW(6), 0, NOW(6), 0),
    ('INVENTORY_ASSET_TREE_MANAGE', '자산 트리 구조 수정',                               NOW(6), 0, NOW(6), 0),

    -- ======================================
    -- BOOKING - RESERVATION
    -- ======================================
    ('BOOKING_RESERVATION_READ',             '예약 목록/상세 조회',                      NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_CREATE',           '예약 생성',                                NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_UPDATE',           '예약 정보 수정',                           NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_DELETE',           '예약 삭제(소프트 딜리트)',                 NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_APPROVE',          '예약 승인',                                NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_REJECT',           '예약 거절',                                NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_CANCEL',           '예약 취소',                                NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_USE_START',        '예약 자원 사용 시작',                      NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_USE_END',          '예약 자원 사용 종료',                      NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_SCHEDULE_UPDATE',  '예약 시간 변경',                           NOW(6), 0, NOW(6), 0),
    ('BOOKING_RESERVATION_ATTENDANT_UPDATE', '참여자 목록 변경',                         NOW(6), 0, NOW(6), 0);
