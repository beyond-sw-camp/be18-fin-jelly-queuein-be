ALTER TABLE `user` DROP INDEX `uk_user_name`;

ALTER TABLE `user` ADD CONSTRAINT `uk_user_no` UNIQUE (`user_no`);
