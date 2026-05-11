-- 猫粮 SKU 表
CREATE TABLE cat_food (
    food_id       BIGINT PRIMARY KEY COMMENT '雪花ID',
    brand         VARCHAR(64) NOT NULL COMMENT '品牌',
    name          VARCHAR(128) NOT NULL COMMENT '商品名',
    image         VARCHAR(512) NULL COMMENT '封面图',
    age_stage     VARCHAR(16) NOT NULL DEFAULT 'all' COMMENT 'kitten/adult/senior/all',
    food_type     VARCHAR(32) NOT NULL DEFAULT 'main' COMMENT 'main/wet/snack/freeze_dried',
    price_range   VARCHAR(32) NULL COMMENT '如 30-50元/kg',
    protein_pct   DECIMAL(4,1) NULL COMMENT '粗蛋白%',
    fat_pct       DECIMAL(4,1) NULL COMMENT '粗脂肪%',
    ingredients_summary TEXT NULL COMMENT '配料表摘要',
    tags          JSON NULL COMMENT '标签数组',
    avg_score     DECIMAL(2,1) DEFAULT 0 COMMENT 'UGC平均评分',
    review_count  INT DEFAULT 0 COMMENT '短评数量',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT DEFAULT 0,
    INDEX idx_brand (brand),
    INDEX idx_food_type (food_type),
    INDEX idx_age_stage (age_stage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫粮SKU';

-- PGC 官方测评表
CREATE TABLE pgc_review (
    review_id     BIGINT PRIMARY KEY COMMENT '雪花ID',
    food_id       BIGINT NOT NULL COMMENT '关联猫粮',
    author_id     BIGINT NOT NULL COMMENT '运营作者',
    title         VARCHAR(128) NOT NULL COMMENT '测评标题',
    cover         VARCHAR(512) NULL COMMENT '封面图',
    content_md    LONGTEXT NULL COMMENT 'Markdown正文',
    score_ingredient  TINYINT DEFAULT 0 COMMENT '原料 1-10',
    score_nutrition   TINYINT DEFAULT 0 COMMENT '营养 1-10',
    score_value       TINYINT DEFAULT 0 COMMENT '性价比 1-10',
    score_palatability TINYINT DEFAULT 0 COMMENT '适口性 1-10',
    score_safety      TINYINT DEFAULT 0 COMMENT '安全性 1-10',
    published_at  DATETIME NULL COMMENT '发布时间',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT DEFAULT 0,
    INDEX idx_food (food_id),
    INDEX idx_published (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PGC官方测评';

-- UGC 用户短评表
CREATE TABLE ugc_short_review (
    review_id     BIGINT PRIMARY KEY COMMENT '雪花ID',
    food_id       BIGINT NOT NULL COMMENT '关联猫粮',
    user_id       BIGINT NOT NULL COMMENT '评价用户',
    cat_id        BIGINT NULL COMMENT '哪只猫吃过',
    score         TINYINT NOT NULL COMMENT '1-5星',
    content       VARCHAR(200) NOT NULL COMMENT '短评内容 ≤200字',
    images        JSON NULL COMMENT '1-3张图片URL',
    audit_status  VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected',
    audit_note    VARCHAR(256) NULL COMMENT '拒绝原因',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT DEFAULT 0,
    INDEX idx_food_status (food_id, audit_status),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='UGC用户短评';
