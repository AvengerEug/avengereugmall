package com.avengereug.mall.order.vo;

import com.avengereug.mall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {

    /**
     * 订单实体类 --> 订单相关信息
     */
    private OrderEntity order;

    /**
     * 错误状态码：
     * 0: 成功
     * 1: 令牌订单信息过期，请刷新再次提交
     * 2: 订单商品价格发生变化，请确认后再次提交
     * 3: 库存锁定失败，商品库存不足
     */
    private Integer code;

}
