package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.HomeSubjectSpuEntity;
import com.avengereug.mall.coupon.service.HomeSubjectSpuService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 专题商品
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/homesubjectspu")
public class HomeSubjectSpuController {

    @Autowired
    private HomeSubjectSpuService homeSubjectSpuService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:homesubjectspu:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = homeSubjectSpuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:homesubjectspu:info")
    public R info(@PathVariable("id") Long id){
        HomeSubjectSpuEntity homeSubjectSpu = homeSubjectSpuService.getById(id);

        return R.ok().put("homeSubjectSpu", homeSubjectSpu);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:homesubjectspu:save")
    public R save(@RequestBody HomeSubjectSpuEntity homeSubjectSpu){
        homeSubjectSpuService.save(homeSubjectSpu);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:homesubjectspu:update")
    public R update(@RequestBody HomeSubjectSpuEntity homeSubjectSpu){
        homeSubjectSpuService.updateById(homeSubjectSpu);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:homesubjectspu:delete")
    public R delete(@RequestBody Long[] ids){
        homeSubjectSpuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
