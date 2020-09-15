package com.avengereug.mall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpuItemAttrGroupVO {
    private String groupName;
    private List<SpuBaseAttrVO> attrs;
}