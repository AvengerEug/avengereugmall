package com.avengereug.mall.order.service.impl;

import com.avengereug.mall.cart.client.CartClient;
import com.avengereug.mall.cart.vo.CartItemVo;
import com.avengereug.mall.member.entity.MemberReceiveAddressEntity;
import com.avengereug.mall.member.feign.MemberReceiveAddressClient;
import com.avengereug.mall.member.vo.MemberResponseVo;
import com.avengereug.mall.order.interceptor.LoginInterceptor;
import com.avengereug.mall.order.vo.MemberAddressVo;
import com.avengereug.mall.order.vo.OrderConfirmVo;
import com.avengereug.mall.order.vo.OrderItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
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


        CompletableFuture cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(servletRequestAttributes);
            // 2、获取当前用户的购物车信息
            List<CartItemVo> currentCartItems = cartClient.getCurrentCartItems();
            List<OrderItemVo> orderItemVos = currentCartItems.stream().map(item -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                BeanUtils.copyProperties(item, orderItemVo);
                return orderItemVo;
            }).collect(Collectors.toList());
            orderConfirmVo.setItems(orderItemVos);
        }, threadPoolExecutor);


        // 3、获取用户积分
        orderConfirmVo.setIntegration(currentUser.getIntegration());

        // 4、其他数据在vo中自动计算(订单总额 & 应付金额)

        // TODO 5、防重令牌

        try {
            log.info("等待异步编排中。。。。");
            CompletableFuture.allOf(receiveAddressFuture, cartFuture).get();
        } catch (Exception e) {
            log.info("异步编排发生异常，错误信息: {}", e.getMessage());
            e.printStackTrace();
        }

        return orderConfirmVo;
    }
}