package com.avengereug.mall.product.vo;

import com.avengereug.mall.product.entity.SpuInfoEntity;
import lombok.Data;

@Data
public class SpuInfoSearchVO extends SpuInfoEntity {

    private String brandName;
    private String catelogName;
}
