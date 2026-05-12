-- hospital 表增加 type 字段，区分宠物医院和宠物店
ALTER TABLE hospital ADD COLUMN `type` VARCHAR(16) NOT NULL DEFAULT 'hospital' COMMENT 'hospital=宠物医院, petstore=宠物店' AFTER hospital_id;

-- 为 type 增加索引方便筛选
ALTER TABLE hospital ADD INDEX idx_type (`type`);

-- 插入一些宠物店示例数据
INSERT INTO hospital (hospital_id, `type`, poi_source, name, address, lat, lng, phone, business_hours, tags) VALUES
(1001, 'petstore', 'manual', '萌宠乐园宠物店', '北京市朝阳区望京SOHO T1', 39.9920, 116.4740, '010-12345678', '09:00-21:00', '["洗护","寄养","活体"]'),
(1002, 'petstore', 'manual', '爱宠到家宠物生活馆', '北京市海淀区中关村大街1号', 39.9840, 116.3160, '010-87654321', '10:00-22:00', '["洗护","美容","用品"]'),
(1003, 'petstore', 'manual', '猫咪天堂宠物用品店', '北京市西城区金融街购物中心B1', 39.9140, 116.3580, '010-55667788', '10:00-21:30', '["猫粮","猫砂","玩具"]'),
(1004, 'hospital', 'manual', '北京宠爱佳动物医院', '北京市朝阳区酒仙桥路2号', 39.9700, 116.4900, '010-11223344', '08:00-22:00', '["24h","外科","疫苗"]'),
(1005, 'hospital', 'manual', '美联众合动物医院(望京店)', '北京市朝阳区望京南湖西园', 39.9880, 116.4680, '010-99887766', '09:00-21:00', '["内科","口腔","体检"]');
