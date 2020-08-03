-- pms_attr表添加value_type字段，用来标识该字段是否可以多选
ALTER TABLE pms_attr ADD COLUMN value_type INT NOT NULL DEFAULT 0;

-- pms_spu_info表修改catalog_id为catelog_id
ALTER TABLE pms_spu_info CHANGE catalog_id catelog_id BIGINT(20) COMMENT "所属分类id";

-- pms_sku_info表修改catalog_id为catelog_id
ALTER TABLE pms_sku_info CHANGE catalog_id catelog_id BIGINT(20) COMMENT "所属分类id";

-- 修改pms_spu_info表中publishStatus字段的注释，
ALTER TABLE pms_spu_info CHANGE publish_status publish_status TINYINT(4) COMMENT "上架状态[0 - 新建，1 - 上架, 2 - 下架]";

-----------------------------------------------------------------------------

-- wms_purchase_detail新增采购失败原因字段和实际采购数量字段
ALTER TABLE wms_purchase_detail ADD COLUMN comment VARCHAR(255) COMMENT "采购备注";
ALTER TABLE wms_purchase_detail ADD COLUMN actual_sku_num INT COMMENT "实际采购数量";