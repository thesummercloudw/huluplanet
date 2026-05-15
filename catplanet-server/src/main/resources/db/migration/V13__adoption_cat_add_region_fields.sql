-- 领养猫咪表增加省份和区县字段（支持省市区三级地区选择）
ALTER TABLE adoption_cat
    ADD COLUMN province VARCHAR(32) NULL COMMENT '省份' AFTER personality,
    ADD COLUMN district VARCHAR(32) NULL COMMENT '区县' AFTER city;
