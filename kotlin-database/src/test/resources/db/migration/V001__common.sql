USE `lix_database_common`;

CREATE TABLE `users` (
    `id`        BIGINT(20)      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `username`  VARCHAR(20)     NOT NULL COMMENT '用户名',
    `password`  VARCHAR(50)     NOT NULL COMMENT '密码'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;