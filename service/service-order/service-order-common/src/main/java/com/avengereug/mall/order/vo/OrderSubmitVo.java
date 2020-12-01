package com.avengereug.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {

    /** 收获地址的id **/
    private Long addrId;

    /** 支付方式 **/
    private Integer payType;

    // 无需提交要购买的商品，去购物车再获取一遍 ===> 防止用户在下单页面中又在购物车中勾选了一个商品，然后去结算
    //优惠、发票

    /** 防重令牌 **/
    private String orderToken;

    /** 应付价格， 可以用来校验哪些商品减价了，哪些商品增加了**/
    private BigDecimal payPrice;

    /** 订单备注 **/
    private String remarks;

    //用户相关的信息，直接去session中取出即可
}
