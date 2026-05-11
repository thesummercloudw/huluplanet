-- V2__record_tables.sql
-- 喂食记录、养护记录、健康记录

CREATE TABLE IF NOT EXISTS `feeding_record` (
    `record_id`         BIGINT       NOT NULL COMMENT '雪花ID',
    `cat_id`            BIGINT       NOT NULL COMMENT '猫咪ID',
    `family_id`         BIGINT       NOT NULL COMMENT '所属家庭',
    `food_name`         VARCHAR(128) NOT NULL COMMENT '食物名',
    `amount_g`          INT          DEFAULT NULL COMMENT '克数',
    `meal_type`         VARCHAR(16)  NOT NULL DEFAULT 'main' COMMENT 'main/snack/wet/dry',
    `fed_at`            DATETIME     NOT NULL COMMENT '喂食时间',
    `operator_user_id`  BIGINT       NOT NULL COMMENT '操作人',
    `note`              VARCHAR(256) DEFAULT NULL COMMENT '备注',
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`           TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`record_id`),
    INDEX `idx_cat_fed` (`cat_id`, `fed_at`),
    INDEX `idx_family` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='喂食记录';

CREATE TABLE IF NOT EXISTS `care_record` (
    `record_id`         BIGINT       NOT NULL COMMENT '雪花ID',
    `cat_id`            BIGINT       NOT NULL COMMENT '猫咪ID',
    `family_id`         BIGINT       NOT NULL COMMENT '所属家庭',
    `care_type`         VARCHAR(16)  NOT NULL COMMENT 'litter/bath/grooming/nail/play/other',
    `done_at`           DATETIME     NOT NULL COMMENT '完成时间',
    `operator_user_id`  BIGINT       NOT NULL COMMENT '操作人',
    `note`              VARCHAR(256) DEFAULT NULL COMMENT '备注',
    `images`            JSON         DEFAULT NULL COMMENT '图片URL数组',
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`           TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`record_id`),
    INDEX `idx_cat_done` (`cat_id`, `done_at`),
    INDEX `idx_family` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='养护记录';

CREATE TABLE IF NOT EXISTS `health_record` (
    `record_id`         BIGINT       NOT NULL COMMENT '雪花ID',
    `cat_id`            BIGINT       NOT NULL COMMENT '猫咪ID',
    `family_id`         BIGINT       NOT NULL COMMENT '所属家庭',
    `health_type`       VARCHAR(16)  NOT NULL COMMENT 'vaccine/deworm/checkup/medicine/weight',
    `subtype`           VARCHAR(32)  DEFAULT NULL COMMENT '如二联/体内驱虫',
    `record_date`       DATE         NOT NULL COMMENT '实施日',
    `hospital_name`     VARCHAR(128) DEFAULT NULL COMMENT '医院名',
    `cost`              DECIMAL(8,2) DEFAULT NULL COMMENT '费用',
    `next_due_date`     DATE         DEFAULT NULL COMMENT '下次到期',
    `value_numeric`     DECIMAL(8,2) DEFAULT NULL COMMENT '体重等数值',
    `operator_user_id`  BIGINT       NOT NULL COMMENT '操作人',
    `note`              VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `images`            JSON         DEFAULT NULL COMMENT '图片URL数组',
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`           TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`record_id`),
    INDEX `idx_cat_date` (`cat_id`, `record_date`),
    INDEX `idx_family` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录';
