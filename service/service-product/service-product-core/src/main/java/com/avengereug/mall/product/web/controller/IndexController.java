package com.avengereug.mall.product.web.controller;

import com.avengereug.mall.product.entity.CategoryEntity;
import com.avengereug.mall.product.service.CategoryService;
import com.avengereug.mall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String READ_WRITE_KEY = "rw-lock";

    @GetMapping(value = {"/", "index.html"})
    private String indexPage(Model model) {

        //1、查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categories", categoryEntities);

        return "index";
    }

    //product.index/json/catalog.json
    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

        return catalogJson;
    }


    /**
     * 测试读写锁========================================
     *
     * 读写锁特征：
     *   1、只要存在写操作，那么后面的读操作就会阻塞。
     *   2、读读操作相当于无锁
     * @return
     */
    @GetMapping("/write/value")
    @ResponseBody
    public String writeValue() {
        RReadWriteLock readWriteLock= redissonClient.getReadWriteLock(READ_WRITE_KEY);

        RLock lock = readWriteLock.writeLock();
        String uuid = UUID.randomUUID().toString();

        lock.lock();
        try {
            System.out.println("写数据");
            stringRedisTemplate.opsForValue().set("rw-key", uuid);

            Thread.sleep(30000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return uuid;
    }

    @GetMapping("/read/value")
    @ResponseBody
    public String readValue() {
        RReadWriteLock readWriteLock= redissonClient.getReadWriteLock(READ_WRITE_KEY);

        RLock lock = readWriteLock.readLock();
        String value = null;

        lock.lock();
        try {
            System.out.println("写数据");
            value = stringRedisTemplate.opsForValue().get("rw-key");

        } finally {
            lock.unlock();
        }

        return value;
    }

    /**
     * 测试分布式情况下的countdownLatch
     */
    @GetMapping("/distributed/countdown-latch/before")
    @ResponseBody
    public String distributedCountdownLatchBefore() throws InterruptedException {
        RCountDownLatch countdownLatchLock = redissonClient.getCountDownLatch("countdownLatchLock");
        countdownLatchLock.trySetCount(5);
        // 当执行5次countDown方法，此时就会解阻塞，继续执行
        countdownLatchLock.await();
        return "ok";
    }

    @GetMapping("/distributed/countdown-latch/after")
    @PostMapping
    public String distributedCountdownLatchAfter() {
        RCountDownLatch countdownLatchLock = redissonClient.getCountDownLatch("countdownLatchLock");
        countdownLatchLock.countDown();
        return "value: " + countdownLatchLock.getCount();
    }


    /**
     * 测试分布式情况下的semaphore
     *
     * 模拟停车位
     */
    @GetMapping("/distributed/semaphore/before")
    @ResponseBody
    public String distributedSemaphoreBefore() throws InterruptedException {
        RSemaphore semaphoreLock = redissonClient.getSemaphore("semaphoreLock");
        semaphoreLock.trySetPermits(3);
        // 申请获取一个车位，如果车位不够，就阻塞到这里，所以第三次请求以后，都会阻塞
        semaphoreLock.acquire();
        return "ok";
    }

    @GetMapping("/distributed/semaphore/after")
    @ResponseBody
    public String distributedSemaphoreAfter() {
        RSemaphore semaphoreLock = redissonClient.getSemaphore("semaphoreLock");
        // 释放一个停车位
        semaphoreLock.release();
        return "ok";
    }

}
