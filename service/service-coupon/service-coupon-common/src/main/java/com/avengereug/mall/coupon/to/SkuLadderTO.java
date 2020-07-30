package com.avengereug.mall.coupon.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuLadderTO {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    /**
     * 折后价
     */
    private BigDecimal price;
}
