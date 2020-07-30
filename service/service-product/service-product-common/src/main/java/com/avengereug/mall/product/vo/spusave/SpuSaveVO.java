/**
  * Copyright 2020 bejson.com 
  */
package com.avengereug.mall.product.vo.spusave;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveVO {

    // ====== begin spu基本信息 mall_pms -> pms_spu_info ========
    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    // ======  end  spu基本信息 mall_pms -> pms_spu_info ========


    // ====== begin spu商品描述信息(一张大图) mall_pms -> pms_spu_info_desc =======
    private List<String> decript;
    // ======  end  spu商品描述信息(一张大图) mall_pms -> pms_spu_info_desc =======


    // ====== begin spu图片集 mall_pms -> pms_sku_images =======
    private List<String> images;
    // ======  end  spu图片集 mall_pms -> pms_sku_images =======


    // ====== begin spu优惠信息(跨库存储: mall_sms库sms_spu_bounds表) =======
    private Bounds bounds;
    // ======  end  spu优惠信息(跨库存储: mall_sms库sms_spu_bounds表) =======


    // ====== begin spu中的基础属性 mall_pms -> pms_product_attr_value =======
    private List<BaseAttrs> baseAttrs;
    // ======  end  spu中的基础属性 mall_pms -> pms_product_attr_value =======


    // ====== begin spu中sku的基本信息 mall_pms -> pms_sku_info =======
    private List<Skus> skus;
    // ======  end  spu中sku的基本信息 mall_pms -> pms_sku_info =======

}