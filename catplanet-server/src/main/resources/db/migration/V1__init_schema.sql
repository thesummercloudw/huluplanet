-- V1__init_schema.sql
CREATE TABLE IF NOT EXISTS `user` (
    `user_id`    BIGINT       NOT NULL COMMENT '雪花ID',
    `openid`     VARCHAR(64)  NOT NULL COMMENT '微信openid',
    `unionid`    VARCHAR(64)  DEFAULT NULL COMMENT '微信unionid',
    `nickname`   VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`     VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `phone`      VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `family` (
    `family_id`   BIGINT      NOT NULL COMMENT '雪花ID',
    `name`        VARCHAR(64) NOT NULL COMMENT '家庭名',
    `cover_emoji` VARCHAR(8)  DEFAULT '🏠' COMMENT '封面emoji',
    `creator_id`  BIGINT      NOT NULL COMMENT '创建者ID',
    `invite_code` VARCHAR(16) NOT NULL COMMENT '邀请码',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`family_id`),
    UNIQUE KEY `uk_invite_code` (`invite_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭表';

CREATE TABLE IF NOT EXISTS `family_member` (
    `id`                  BIGINT      NOT NULL AUTO_INCREMENT,
    `family_id`           BIGINT      NOT NULL,
    `user_id`             BIGINT      NOT NULL,
    `role`                VARCHAR(16) NOT NULL DEFAULT 'member' COMMENT 'owner/admin/member',
    `nickname_in_family`  VARCHAR(32) DEFAULT NULL COMMENT '家庭内昵称',
    `joined_at`           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_family_user` (`family_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭成员关系表';

CREATE TABLE IF NOT EXISTS `cat` (
    `cat_id`          BIGINT       NOT NULL COMMENT '雪花ID',
    `family_id`       BIGINT       NOT NULL COMMENT '所属家庭',
    `name`            VARCHAR(32)  NOT NULL,
    `avatar`          VARCHAR(512) DEFAULT NULL,
    `breed`           VARCHAR(32)  DEFAULT NULL COMMENT '品种',
    `gender`          VARCHAR(8)   DEFAULT 'unknown' COMMENT 'male/female/unknown',
    `birthday`        DATE         DEFAULT NULL,
    `is_neutered`     TINYINT      DEFAULT 0 COMMENT '0/1',
    `weight_kg`       DECIMAL(4,2) DEFAULT NULL COMMENT '最新体重',
    `personality_tags` JSON        DEFAULT NULL COMMENT '性格标签数组',
    `adoption_date`   DATE         DEFAULT NULL COMMENT '到家日',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`cat_id`),
    KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪表';
