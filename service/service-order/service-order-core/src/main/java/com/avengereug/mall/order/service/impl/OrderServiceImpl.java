package com.avengereug.mall.order.service.impl;

import com.avengereug.mall.cart.client.CartClient;
import com.avengereug.mall.cart.vo.CartItemVo;
import com.avengereug.mall.common.constants.OrderConstants;
import com.avengereug.mall.common.exception.NoStockException;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.member.entity.MemberReceiveAddressEntity;
import com.avengereug.mall.member.feign.MemberReceiveAddressClient;
import com.avengereug.mall.member.vo.MemberResponseVo;
import com.avengereug.mall.order.Enum.OrderStatusEnum;
import com.avengereug.mall.order.entity.OrderItemEntity;
import com.avengereug.mall.order.interceptor.LoginInterceptor;
import com.avengereug.mall.order.service.OrderItemService;
import com.avengereug.mall.order.to.OrderCreateTo;
import com.avengereug.mall.order.vo.*;
import com.avengereug.mall.product.entity.SpuInfoEntity;
import com.avengereug.mall.product.feign.SpuInfoClient;
import com.avengereug.mall.warehouse.feign.WareSkuClient;
import com.avengereug.mall.warehouse.vo.MemberAddressVo;
import com.avengereug.mall.order.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.avengereug.mall.cart.constants.CartConstant.CART_PREFIX;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberReceiveAddressClient memberReceiveAddressClient;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private SpuInfoClient spuInfoClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private WareSkuClient wareSkuClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemService orderItemService;

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

    /**
     * 下单流程：
     * 1、提交订单 --> 发送submitOrder请求
     * 2、解析请求体中的数据：OrderSubmitVo
     * 3、验重令牌，防止用户重复提交
     * 4、
     *
     * @param vo
     * @return
     */
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        /**
         * 1、验证幂等性
         * 2、锁定库存
         * 3、验证价格 --> 防止价格被商家修改时有提示
         * ...
         */
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        // 1、验证幂等性
        MemberResponseVo currentUser = LoginInterceptor.getCurrentUser();

        /**
         * 使用lua脚本将令牌删除(查找和删除为原子性) => 此脚本表示，调用get命令，key为KEYS[1]的值与传入的参数ARGV[1]相等，则执行
         * redis.call('del', KEYS[1])命令，此命令就是把key删掉
         *
         * 当返回值为1时，表示脚本运行成功
         * 当返回值为0时，表示脚本运行失败
         *
         */
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        /**
         * 第一个参数为lua脚本，其中构造方法的第二个参数表示了脚本执行的返回值
         * 第二个参数为一个list，里面存的数据就是上述脚本中KEYS, 上述脚本的KEYS[1] 表示取的是第一个元素
         * 第三个参数是一个可变参数，它对应的是脚本中的ARGV, ==> ARGV[1]就是那的第三个参数的第一个元素
         */
        Long result = (Long) redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstants.ORDER_TOKEN_PREFIX + currentUser.getId()),
                orderToken);
        // 令牌不正确
        if (result.equals(0L)) {
            //令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            // 令牌验证成功
            // 1、创建订单 --> 从购物车中获取订单项
            OrderCreateTo order = createOrder(vo);

            // 2、验证价格 --> 验证前端传入的价格和我们动态生成的价格是否相同
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();

            if (payAmount.compareTo(payPrice) != 0) {
                // 价格验证不通过，价格被商家修改了
                responseVo.setCode(2);
                return responseVo;
            }

            // 3、保存订单
            saveOrder(order);
            //订单号、所有订单项信息(skuId,skuNum,skuName)
            WareSkuLockVo lockVo = new WareSkuLockVo();
            lockVo.setOrderSn(order.getOrder().getOrderSn());

            // 4、锁定库存 --> 跨服务调用，分布式事务
            //获取出要锁定的商品数据信息
            List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setSkuId(item.getSkuId());
                orderItemVo.setCount(item.getSkuQuantity());
                orderItemVo.setTitle(item.getSkuName());
                return orderItemVo;
            }).collect(Collectors.toList());
            lockVo.setLocks(orderItemVos);

            // 锁库存
            Boolean wareLockResult = wareSkuClient.orderLockStock(lockVo);
            if (wareLockResult) {
                //锁定成功
                responseVo.setOrder(order.getOrder());

                //TODO 订单创建成功，发送消息给MQ

                //删除购物车里的数据 TODO 这里应该有问题，应该删除已经购买的商品
                redisTemplate.delete(CART_PREFIX + currentUser.getId());
                return responseVo;
            } else {
                //锁定失败
                throw new NoStockException("库存锁定失败");
                // responseVo.setCode(3);
                // return responseVo;
            }

        }

    }

    private void saveOrder(OrderCreateTo orderCreateTo) {
        //获取订单信息
        OrderEntity order = orderCreateTo.getOrder();
        order.setModifyTime(new Date());
        order.setCreateTime(new Date());
        //保存订单
        this.baseMapper.insert(order);

        //获取订单项信息
        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
        //批量保存订单项数据
        orderItemService.saveBatch(orderItems);
    }

    private OrderCreateTo createOrder(OrderSubmitVo vo) {
        OrderCreateTo createTo = new OrderCreateTo();

        //1、生成订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = builderOrder(orderSn, vo);

        //2、获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);

        //3、验价(计算价格、积分等信息)
        computePrice(orderEntity,orderItemEntities);

        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);

        return createTo;

    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = new BigDecimal("0.0");
        //优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        //积分、成长值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        //订单总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItem : orderItemEntities) {
            //优惠价格信息
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());

            //总价
            total = total.add(orderItem.getRealAmount());

            //积分信息和成长值信息
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();

        }
        //1、订单价格相关的
        orderEntity.setTotalAmount(total);
        //设置应付总额(总额+运费)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        //设置积分成长值信息
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);

        //设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);
    }

    private List<OrderItemEntity> builderOrderItems(String orderSn) {
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();

        //最后确定每个购物项的价格
        List<CartItemVo> currentCartItems = cartClient.getCurrentCartItems();
        if (currentCartItems != null && currentCartItems.size() > 0) {
            orderItemEntityList = currentCartItems.stream().map((items) -> {
                //构建订单项数据
                OrderItemEntity orderItemEntity = builderOrderItem(items);
                orderItemEntity.setOrderSn(orderSn);

                return orderItemEntity;
            }).collect(Collectors.toList());
        }

        return orderItemEntityList;
    }

    private OrderItemEntity builderOrderItem(CartItemVo items) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();

        //1、商品的spu信息
        Long skuId = items.getSkuId();
        //获取spu的信息
        SpuInfoEntity spuInfo = spuInfoClient.queryBySkuId(skuId);
        orderItemEntity.setSpuId(spuInfo.getId());
        orderItemEntity.setSpuName(spuInfo.getSpuName());
        orderItemEntity.setSpuBrand(spuInfo.getBrandName());
        orderItemEntity.setCategoryId(spuInfo.getCatelogId());

        //2、商品的sku信息
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(items.getTitle());
        orderItemEntity.setSkuPic(items.getImage());
        orderItemEntity.setSkuPrice(items.getPrice());
        orderItemEntity.setSkuQuantity(items.getCount());

        //使用StringUtils.collectionToDelimitedString将list集合转换为String
        String skuAttrValues = StringUtils.collectionToDelimitedString(items.getSkuAttrValues(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);

        //3、商品的优惠信息

        //4、商品的积分信息
        orderItemEntity.setGiftGrowth(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        orderItemEntity.setGiftIntegration(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());

        //5、订单项的价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);

        return orderItemEntity;
    }

    private OrderEntity builderOrder(String orderSn, OrderSubmitVo orderSubmitVo) {
        //获取当前用户登录信息
        MemberResponseVo currentUser = LoginInterceptor.getCurrentUser();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(currentUser.getId());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(currentUser.getUsername());

        //远程获取收货地址和运费信息 --> 模拟
        orderEntity.setFreightAmount(new BigDecimal("1.21"));

        //获取到收货地址信息
        MemberReceiveAddressEntity address = memberReceiveAddressClient.getById(orderSubmitVo.getAddrId());
        //设置收货人信息
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());

        //设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }
}