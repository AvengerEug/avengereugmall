package com.avengereug.mall.cart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String CART_KEY = "cart:";

    @Test
    public void testAddItemToHash() {
        // 为用户1的购物车新增商品为1的商品
//        Goods goods = new Goods(2, "方便面", new BigDecimal("4.50"), 1);
        Goods goods = new Goods(3, "百事可乐", new BigDecimal("3.00"), 2);
        // 方式一：
//        redisTemplate.opsForHash().put(CART_KEY + "1", goods.getId(), goods);

        // 方式二：
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(CART_KEY + "1");
        boundHashOperations.put(goods.getId(), goods);
    }


    @Test
    public void testGetCart() {
        // 获取用户1的购物车信息
        BoundHashOperations<String, Integer, Goods> boundHashOperations = redisTemplate.boundHashOps(CART_KEY + "1");

        // 1、获取用户1购物车购买的类别商品 ===> 获取 cart:1 中的元素(map个数)
        Long size = boundHashOperations.size();
        System.out.println("用户1的购物车有【" + size + "】种商品");

        // 2、购物车中的商品总数  ===> 拿出每个商品类别，并最内部的count字段进行相加操作
        List<Goods> values = boundHashOperations.values();
        int count = 0;
        for (Goods value : values) {
            count += value.getCount();
        }
        System.out.println("用户1的购物车一共有【" + count + "】件商品");

        // 3、添加购物车商品 --> 新增一个map元素
        boundHashOperations.put(4, new Goods(4, "薯片", new BigDecimal("2.5"), 1));
        System.out.println("用户1新增一种商品, 目前购物车商品类别有：【" + boundHashOperations.size() + "】种");

        // 4、购物车商品为3的数量 + 1 ---> 修改对应map中的count属性
        Goods goods = boundHashOperations.get(3);
        goods.setCount(goods.getCount() + 1);
        boundHashOperations.put(3, goods);

        // 5、删除购物车商品 --> 删除对应的map元素
        boundHashOperations.delete(4);

        // 6、获取当前商品个数，总价格
        values = boundHashOperations.values();
        count = 0;
        BigDecimal amount = BigDecimal.ZERO;
        for (Goods value : values) {
            count += value.getCount();
            amount = amount.add(value.getPrice().multiply(new BigDecimal(value.getCount())));
        }
        System.out.println("==========================================");
        System.out.println("用户1的购物车一共有【" + boundHashOperations.size() + "】种商品");
        System.out.println("用户1的购物车一共有【" + count + "】件商品");
        System.out.println("用户1的购物车总金额为：【" + amount.toString() + "】");


    }

}
