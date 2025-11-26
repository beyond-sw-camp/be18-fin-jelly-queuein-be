ALTER TABLE `user`
    DROP INDEX `uk_user_no`,
    DROP INDEX `uk_email`;

ALTER TABLE `user`
    ADD COLUMN `not_archived` TINYINT(1)
        GENERATED ALWAYS AS (IF(`deleted_at` IS NULL, 1, NULL)) VIRTUAL;

ALTER TABLE `user`
    ADD UNIQUE INDEX `uk_user_no_active` (`user_no`, `not_archived`);

ALTER TABLE `user`
    ADD UNIQUE INDEX `uk_user_email_active` (`email`, `not_archived`);






ALTER TABLE `role`
    DROP INDEX `uk_role_name`;

ALTER TABLE `role`
    ADD COLUMN `not_archived` TINYINT(1)
        GENERATED ALWAYS AS (IF(`deleted_at` IS NULL, 1, NULL)) VIRTUAL;

ALTER TABLE `role`
    ADD UNIQUE INDEX `uk_role_name_active` (`role_name`, `not_archived`);





ALTER TABLE `permission`
    ADD COLUMN `not_archived` TINYINT(1)
        GENERATED ALWAYS AS (IF(`deleted_at` IS NULL, 1, NULL)) VIRTUAL;

ALTER TABLE `permission`
    ADD UNIQUE INDEX `uk_permission_name_active` (`permission_name`, `not_archived`);
