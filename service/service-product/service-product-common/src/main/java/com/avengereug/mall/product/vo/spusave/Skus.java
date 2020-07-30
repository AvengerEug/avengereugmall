/**
  * Copyright 2020 bejson.com 
  */
package com.avengereug.mall.product.vo.spusave;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Skus {

    // ====== begin sku中的销售信息 mall_pms -> pms_sku_sale_attr_value =======
    private List<Attr> attr;
    // ======  end  sku中的销售信息 mall_pms -> pms_sku_sale_attr_value =======


    // ====== begin sku中的基本信息 mall_pms -> pms_sku_info  =======
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    // ======  end  sku中的基本信息 mall_pms -> pms_sku_info =======

    // ====== begin sku存储的图片 mall_pms -> pms_sku_images =======
    private List<Images> images;
    private List<String> descar;
    // ======  end  sku存储的图片 mall_pms -> pms_sku_images =======

    // ====== begin 满多少件，打多少折，并且是否叠加 mall_sms -> sms_sku_ladder ======
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    // ======  end  满多少件，打多少折，并且是否叠加 mall_sms -> sms_sku_ladder ======


    // ====== begin 满多少钱，减多少钱，并且是否叠加 mall_sms -> sms_sku_full_reduction ======
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    // ======  end  满多少钱，减多少钱，并且是否叠加 mall_sms -> sms_sku_full_reduction ======

    // ====== begin 会员价格信息 mall_sms -> sms_member_price =======
    private List<MemberPrice> memberPrice;
    // ======  end  会员价格信息 mall_sms -> sms_member_price =======

}