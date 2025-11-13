ALTER TABLE `user` ADD `password_expired` BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE `user` ADD `last_login_at` TIMESTAMP(6) NULL;