-- 猫咪出没评论表
CREATE TABLE sighting_comment (
    comment_id     BIGINT PRIMARY KEY COMMENT '雪花ID',
    sighting_id    BIGINT NOT NULL COMMENT '关联出没记录',
    user_id        BIGINT NOT NULL COMMENT '评论用户',
    content        VARCHAR(500) NOT NULL COMMENT '评论内容（≤500字）',
    images         JSON NULL COMMENT '评论图片URL数组（最多3张）',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted        TINYINT DEFAULT 0,
    INDEX idx_sighting (sighting_id, created_at DESC),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪出没评论';

-- 给 cat_sighting 表增加评论数字段
ALTER TABLE cat_sighting ADD COLUMN comment_count INT DEFAULT 0 COMMENT '评论数';
