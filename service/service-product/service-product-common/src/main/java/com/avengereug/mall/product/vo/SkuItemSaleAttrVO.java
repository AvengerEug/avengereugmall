package com.avengereug.mall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuItemSaleAttrVO {

    private Long attrId;

    private String attrName;

    private List<String> attrValues;

}
