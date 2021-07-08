USE `lix_database_demo`;

CREATE TABLE `users` (
    `id`        BIGINT(20)      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `username`  VARCHAR(20)     NOT NULL COMMENT '用户名',
    `password`  VARCHAR(50)     NOT NULL COMMENT '密码'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `user_task`(
    `id`            BIGINT(20)      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`       BIGINT(20)      NOT NULL COMMENT '用户ID',
    `task_id`       VARCHAR(50)     NOT NULL COMMENT '任务ID',
    `task_type`     VARCHAR(50)     NOT NULL COMMENT '任务类型',
    KEY `idx_user_id`(`user_id`),
    KEY `idx_task`(`task_type`, `task_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;