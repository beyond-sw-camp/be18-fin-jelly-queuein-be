INSERT INTO `role` (`role_name`, `role_description`, `created_at`, `created_by`, `updated_at`, `updated_by`)
VALUES
    ('MASTER',  '최초 생성되는 최고 권한자',       NOW(6), 0, NOW(6), 0),
    ('ADMIN',   '부서/자원 관리자',               NOW(6), 0, NOW(6), 0),
    ('MANAGER', '승인 및 일부 자원 관리 권한',     NOW(6), 0, NOW(6), 0),
    ('GENERAL', '일반 사용자',                    NOW(6), 0, NOW(6), 0)
ON DUPLICATE KEY UPDATE
    `role_name` = `role_name`;
