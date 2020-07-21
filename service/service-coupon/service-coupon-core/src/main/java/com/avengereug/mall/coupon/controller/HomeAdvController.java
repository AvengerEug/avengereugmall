package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.HomeAdvEntity;
import com.avengereug.mall.coupon.service.HomeAdvService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 首页轮播广告
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/homeadv")
public class HomeAdvController {

    @Autowired
    private HomeAdvService homeAdvService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:homeadv:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = homeAdvService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:homeadv:info")
    public R info(@PathVariable("id") Long id){
        HomeAdvEntity homeAdv = homeAdvService.getById(id);

        return R.ok().put("homeAdv", homeAdv);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:homeadv:save")
    public R save(@RequestBody HomeAdvEntity homeAdv){
        homeAdvService.save(homeAdv);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:homeadv:update")
    public R update(@RequestBody HomeAdvEntity homeAdv){
        homeAdvService.updateById(homeAdv);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:homeadv:delete")
    public R delete(@RequestBody Long[] ids){
        homeAdvService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
