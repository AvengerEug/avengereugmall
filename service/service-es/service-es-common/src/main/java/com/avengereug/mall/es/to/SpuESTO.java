package com.avengereug.mall.es.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuESTO {

    private Long spuId;
    private Long skuId;
    private String skuTitle;
    // 无法属性对拷
    private BigDecimal skuPrice;
    // 无法属性对拷
    private String skuImg;
    private Long saleCount;

    // 无法属性对拷
    private Boolean hasStock;
    // 无法属性对拷
    private Long hotScope;

    private Long brandId;

    private String brandName;

    //
    private String brandImg;

    private Long catelogId;

    private String catelogName;

    private List<Attrs> attrs;

    @Data
    public static class Attrs {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }


}
