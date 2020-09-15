package com.avengereug.mall.product.vo;

import com.avengereug.mall.product.entity.SkuImagesEntity;
import com.avengereug.mall.product.entity.SkuInfoEntity;
import com.avengereug.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVO  {

    // 1、sku基本信息  --> pms_sku_info
    SkuInfoEntity info;

    // 2、sku图片信息 --> pms_sku_images
    List<SkuImagesEntity> images;

    // 3、sku对应的spu销售属性
    List<SkuItemSaleAttrVO> saleAttr;

    // 4、spu商品介绍
    SpuInfoDescEntity desp;

    // 5、spu的规格参数信息
    List<SpuItemAttrGroupVO> groupAttrs;

}
