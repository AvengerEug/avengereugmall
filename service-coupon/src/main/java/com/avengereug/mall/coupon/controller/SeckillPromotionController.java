package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.SeckillPromotionEntity;
import com.avengereug.mall.coupon.service.SeckillPromotionService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 秒杀活动
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/seckillpromotion")
public class SeckillPromotionController {

    @Autowired
    private SeckillPromotionService seckillPromotionService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:seckillpromotion:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = seckillPromotionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:seckillpromotion:info")
    public R info(@PathVariable("id") Long id){
        SeckillPromotionEntity seckillPromotion = seckillPromotionService.getById(id);

        return R.ok().put("seckillPromotion", seckillPromotion);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:seckillpromotion:save")
    public R save(@RequestBody SeckillPromotionEntity seckillPromotion){
        seckillPromotionService.save(seckillPromotion);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:seckillpromotion:update")
    public R update(@RequestBody SeckillPromotionEntity seckillPromotion){
        seckillPromotionService.updateById(seckillPromotion);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:seckillpromotion:delete")
    public R delete(@RequestBody Long[] ids){
        seckillPromotionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
