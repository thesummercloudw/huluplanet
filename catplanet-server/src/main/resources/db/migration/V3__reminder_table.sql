-- 提醒表
CREATE TABLE reminder (
    reminder_id   BIGINT PRIMARY KEY COMMENT '雪花ID',
    family_id     BIGINT NOT NULL COMMENT '所属家庭',
    cat_id        BIGINT NOT NULL COMMENT '关联猫咪',
    source_record_id BIGINT NULL COMMENT '关联源记录（如疫苗记录ID）',
    type          VARCHAR(32) NOT NULL COMMENT '类型: vaccine/deworm/checkup/feeding/care/custom',
    title         VARCHAR(128) NOT NULL COMMENT '提醒标题',
    trigger_at    DATETIME NOT NULL COMMENT '触发时间',
    repeat_rule   JSON NULL COMMENT '重复规则，如 {"every":30,"unit":"day"}',
    subscribed_user_ids JSON NULL COMMENT '已授权订阅消息的用户ID数组',
    status        VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT 'pending/sent/done/cancelled',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT DEFAULT 0,
    INDEX idx_family_status (family_id, status),
    INDEX idx_trigger_status (trigger_at, status),
    INDEX idx_cat (cat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提醒';
