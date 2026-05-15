-- 领养猫咪表增加经纬度字段（支持按距离排序展示最近的领养信息）
ALTER TABLE adoption_cat
    ADD COLUMN lat DOUBLE NULL COMMENT '纬度' AFTER district,
    ADD COLUMN lng DOUBLE NULL COMMENT '经度' AFTER lat;
