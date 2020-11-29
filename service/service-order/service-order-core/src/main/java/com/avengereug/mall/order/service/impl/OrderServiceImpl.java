package com.avengereug.mall.order.service.impl;

import com.avengereug.mall.cart.client.CartClient;
import com.avengereug.mall.cart.vo.CartItemVo;
import com.avengereug.mall.common.constants.OrderConstants;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.member.entity.MemberReceiveAddressEntity;
import com.avengereug.mall.member.feign.MemberReceiveAddressClient;
import com.avengereug.mall.member.vo.MemberResponseVo;
import com.avengereug.mall.order.interceptor.LoginInterceptor;
import com.avengereug.mall.order.vo.*;
import com.avengereug.mall.warehouse.feign.WareSkuClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.order.dao.OrderDao;
import com.avengereug.mall.order.entity.OrderEntity;
import com.avengereug.mall.order.service.OrderService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberReceiveAddressClient memberReceiveAddressClient;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private WareSkuClient wareSkuClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        MemberResponseVo currentUser = LoginInterceptor.getCurrentUser();

        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        CompletableFuture receiveAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(servletRequestAttributes);

            // 1、获取收货地址
            List<MemberReceiveAddressEntity> address = memberReceiveAddressClient.getAddress(currentUser.getId());
            List<MemberAddressVo> addressVos = address.stream().map(item -> {
                MemberAddressVo memberAddressVo = new MemberAddressVo();
                BeanUtils.copyProperties(item, memberAddressVo);
                return memberAddressVo;
            }).collect(Collectors.toList());
            orderConfirmVo.setMemberAddressVos(addressVos);
        }, threadPoolExecutor);


        CompletableFuture cartFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(servletRequestAttributes);
            // 2、获取当前用户的购物车信息
            List<CartItemVo> currentCartItems = cartClient.getCurrentCartItems();
            List<OrderItemVo> orderItemVos = currentCartItems.stream().map(item -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                BeanUtils.copyProperties(item, orderItemVo);
                return orderItemVo;
            }).collect(Collectors.toList());
            orderConfirmVo.setItems(orderItemVos);
            return orderItemVos;
        }, threadPoolExecutor).thenAcceptAsync(items -> {
            // 设置每个商品的库存
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            RPCResult<Map<Long, Boolean>> mapRPCResult = wareSkuClient.innerHasStock(collect);
            orderConfirmVo.setStocks(mapRPCResult.getResult());
        });


        // 3、获取用户积分
        orderConfirmVo.setIntegration(currentUser.getIntegration());

        // 4、其他数据在vo中自动计算(订单总额 & 应付金额)

        // 5、防重令牌
        String uuid = UUID.randomUUID().toString();
        // 在redis中添加一个下订单的token
        stringRedisTemplate.opsForValue().set(OrderConstants.ORDER_TOKEN_PREFIX + currentUser.getId(), uuid);
        orderConfirmVo.setOrderToken(uuid);


        log.info("等待异步编排中。。。。");
        CompletableFuture.allOf(receiveAddressFuture, cartFuture).get();

        return orderConfirmVo;
    }

    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        /**
         * TODO
         * 1、验证幂等性
         * 2、锁定库存
         * 3、验证价格 --> 防止价格被商家修改时有提示
         * ...
         */



        return null;
    }
}