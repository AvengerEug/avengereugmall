package com.avengereug.mall.coupon.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuFullReductionTO {


    private Long skuId;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
}
