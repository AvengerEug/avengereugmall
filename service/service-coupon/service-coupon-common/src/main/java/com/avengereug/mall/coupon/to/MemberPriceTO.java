package com.avengereug.mall.coupon.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPriceTO {

    private Long skuId;
    /**
     * memberLevelId
     */
    private Long id;
    /**
     * memberLevelName
     */
    private String name;
    /**
     * memberLevelPrice
     */
    private BigDecimal price;
}
