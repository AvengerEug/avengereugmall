package com.avengereug.mall.product.web.controller;

import com.avengereug.mall.product.entity.CategoryEntity;
import com.avengereug.mall.product.service.CategoryService;
import com.avengereug.mall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    //index/json/catalog.json
    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

        return catalogJson;
    }


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


}
