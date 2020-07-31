-- pms_attr表添加value_type字段，用来标识该字段是否可以多选
ALTER TABLE pms_attr ADD COLUMN value_type INT NOT NULL DEFAULT 0;

-- pms_spu_info表修改catalog_id为catelog_id
ALTER TABLE pms_spu_info CHANGE catalog_id catelog_id BIGINT(20) COMMENT "所属分类id";

-- pms_sku_info表修改catalog_id为catelog_id
ALTER TABLE pms_sku_info CHANGE catalog_id catelog_id BIGINT(20) COMMENT "所属分类id";

-- 修改pms_spu_info表中publishStatus字段的注释，
ALTER TABLE pms_spu_info CHANGE publish_status publish_status TINYINT(4) COMMENT "上架状态[0 - 新建，1 - 上架, 2 - 下架]"

-----------------------------------------------------------------------------