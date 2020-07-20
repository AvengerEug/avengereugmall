package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.SeckillSessionEntity;
import com.avengereug.mall.coupon.service.SeckillSessionService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 秒杀活动场次
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/seckillsession")
public class SeckillSessionController {

    @Autowired
    private SeckillSessionService seckillSessionService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:seckillsession:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = seckillSessionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:seckillsession:info")
    public R info(@PathVariable("id") Long id){
        SeckillSessionEntity seckillSession = seckillSessionService.getById(id);

        return R.ok().put("seckillSession", seckillSession);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:seckillsession:save")
    public R save(@RequestBody SeckillSessionEntity seckillSession){
        seckillSessionService.save(seckillSession);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:seckillsession:update")
    public R update(@RequestBody SeckillSessionEntity seckillSession){
        seckillSessionService.updateById(seckillSession);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:seckillsession:delete")
    public R delete(@RequestBody Long[] ids){
        seckillSessionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
