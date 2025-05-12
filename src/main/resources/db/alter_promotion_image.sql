-- 修改营销活动图片表的image_url字段类型为text
ALTER TABLE promotion_image MODIFY COLUMN image_url TEXT NOT NULL COMMENT '图片URL'; 