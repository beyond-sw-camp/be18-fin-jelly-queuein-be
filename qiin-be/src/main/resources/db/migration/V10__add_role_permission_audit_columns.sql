ALTER TABLE `role_permission`
    ADD COLUMN `created_by` BIGINT(20) NOT NULL DEFAULT 0 AFTER `permission_id`,
    ADD COLUMN `created_at` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER `created_by`,
    ADD COLUMN `updated_by` BIGINT(20) NOT NULL DEFAULT 0 AFTER `created_at`,
    ADD COLUMN `updated_at` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER `updated_by`,
    ADD COLUMN `deleted_by` BIGINT(20) NULL AFTER `updated_at`,
    ADD COLUMN `deleted_at` TIMESTAMP(6) NULL AFTER `deleted_by`;
