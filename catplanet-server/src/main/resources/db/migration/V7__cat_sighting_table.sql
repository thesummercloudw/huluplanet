-- 猫咪出没（猫咪目击）表
CREATE TABLE cat_sighting (
    sighting_id    BIGINT PRIMARY KEY COMMENT '雪花ID',
    user_id        BIGINT NOT NULL COMMENT '发布用户',
    image          VARCHAR(512) NOT NULL COMMENT '猫咪图片URL',
    content        VARCHAR(256) NULL COMMENT '描述文字',
    lat            DECIMAL(10,6) NOT NULL COMMENT '纬度',
    lng            DECIMAL(10,6) NOT NULL COMMENT '经度',
    address        VARCHAR(256) NULL COMMENT '地点描述',
    like_count     INT DEFAULT 0 COMMENT '点赞数',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted        TINYINT DEFAULT 0,
    INDEX idx_user (user_id),
    INDEX idx_location (lat, lng),
    INDEX idx_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪出没';
