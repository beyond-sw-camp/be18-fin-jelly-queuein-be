ALTER TABLE `asset`
    DROP COLUMN `parent_id`,
    CHANGE COLUMN `approval_status` `needs_approval` BOOLEAN NOT NULL DEFAULT FALSE,
    ADD CONSTRAINT `uk_asset_name` UNIQUE (`name`);

ALTER TABLE `category`
    ADD CONSTRAINT `uk_category_name` UNIQUE (`name`);

DROP TABLE IF EXISTS `asset_hisory`;
