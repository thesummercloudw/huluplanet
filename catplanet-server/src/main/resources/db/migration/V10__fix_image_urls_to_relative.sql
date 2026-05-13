-- 将所有图片URL从绝对路径(http://localhost:8080/uploads/...)改为相对路径(/uploads/...)
-- 这样前端可以根据当前环境动态拼接正确的baseUrl

UPDATE cat_food SET image = REPLACE(image, 'http://localhost:8080', '') WHERE image LIKE 'http://localhost:8080%';
UPDATE pgc_review SET cover = REPLACE(cover, 'http://localhost:8080', '') WHERE cover LIKE 'http://localhost:8080%';
UPDATE cat SET avatar = REPLACE(avatar, 'http://localhost:8080', '') WHERE avatar LIKE 'http://localhost:8080%';
UPDATE cat_sighting SET image = REPLACE(image, 'http://localhost:8080', '') WHERE image LIKE 'http://localhost:8080%';
