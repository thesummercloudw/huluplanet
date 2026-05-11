-- 宠物医院表
CREATE TABLE hospital (
    hospital_id    BIGINT PRIMARY KEY COMMENT '雪花ID',
    poi_source     VARCHAR(16) NOT NULL DEFAULT 'manual' COMMENT 'tencent/amap/manual',
    poi_source_id  VARCHAR(64) NULL COMMENT '三方POI ID',
    name           VARCHAR(128) NOT NULL COMMENT '医院名称',
    address        VARCHAR(256) NULL COMMENT '地址',
    lat            DECIMAL(10,6) NULL COMMENT '纬度',
    lng            DECIMAL(10,6) NULL COMMENT '经度',
    phone          VARCHAR(32) NULL COMMENT '电话',
    business_hours VARCHAR(128) NULL COMMENT '营业时间',
    tags           JSON NULL COMMENT '标签: 24h/夜诊/连锁 等',
    avg_score      DECIMAL(2,1) DEFAULT 0 COMMENT '平均评分',
    review_count   INT DEFAULT 0 COMMENT '评价数',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted        TINYINT DEFAULT 0,
    INDEX idx_location (lat, lng),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物医院';

-- 医院评价表
CREATE TABLE hospital_review (
    review_id      BIGINT PRIMARY KEY COMMENT '雪花ID',
    hospital_id    BIGINT NOT NULL COMMENT '关联医院',
    user_id        BIGINT NOT NULL COMMENT '评价用户',
    score          TINYINT NOT NULL COMMENT '1-5',
    content        VARCHAR(500) NULL COMMENT '评价内容',
    service_tags   JSON NULL COMMENT '服务标签: 态度好/技术强/价格合理 等',
    images         JSON NULL COMMENT '图片',
    audit_status   VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted        TINYINT DEFAULT 0,
    INDEX idx_hospital_status (hospital_id, audit_status),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医院评价';

-- 待领养猫咪表
CREATE TABLE adoption_cat (
    adopt_id              BIGINT PRIMARY KEY COMMENT '雪花ID',
    name                  VARCHAR(32) NOT NULL COMMENT '名字',
    cover                 VARCHAR(512) NULL COMMENT '封面图',
    images                JSON NULL COMMENT '图片数组',
    gender                VARCHAR(8) NOT NULL DEFAULT 'unknown' COMMENT 'male/female/unknown',
    age_estimate          VARCHAR(32) NULL COMMENT '如 约6月龄',
    breed_estimate        VARCHAR(32) NULL COMMENT '品种估计',
    health_status         JSON NULL COMMENT '疫苗/驱虫/绝育 状态',
    personality           VARCHAR(256) NULL COMMENT '性格描述',
    city                  VARCHAR(32) NULL COMMENT '所在城市',
    reason_for_adoption   TEXT NULL COMMENT '送养原因',
    contact_method        VARCHAR(64) NULL COMMENT '联系方式(仅运营可见)',
    status                VARCHAR(16) NOT NULL DEFAULT 'available' COMMENT 'available/pending/adopted',
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted               TINYINT DEFAULT 0,
    INDEX idx_city_status (city, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待领养猫咪';

-- 领养申请表
CREATE TABLE adoption_application (
    apply_id              BIGINT PRIMARY KEY COMMENT '雪花ID',
    adopt_id              BIGINT NOT NULL COMMENT '关联待领养猫咪',
    applicant_user_id     BIGINT NOT NULL COMMENT '申请人',
    self_intro            TEXT NULL COMMENT '自我介绍',
    experience            TEXT NULL COMMENT '养猫经验',
    family_env            TEXT NULL COMMENT '家庭环境',
    commitment_signed     TINYINT NOT NULL DEFAULT 0 COMMENT '养宠承诺已签 0/1',
    status                VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected/cancelled',
    operator_note         TEXT NULL COMMENT '运营备注',
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted               TINYINT DEFAULT 0,
    INDEX idx_adopt_id (adopt_id),
    INDEX idx_user (applicant_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请';
