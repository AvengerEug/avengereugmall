package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.CouponSpuCategoryRelationEntity;
import com.avengereug.mall.coupon.service.CouponSpuCategoryRelationService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 优惠券分类关联
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/couponspucategoryrelation")
public class CouponSpuCategoryRelationController {

    @Autowired
    private CouponSpuCategoryRelationService couponSpuCategoryRelationService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:couponspucategoryrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponSpuCategoryRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:couponspucategoryrelation:info")
    public R info(@PathVariable("id") Long id){
        CouponSpuCategoryRelationEntity couponSpuCategoryRelation = couponSpuCategoryRelationService.getById(id);

        return R.ok().put("couponSpuCategoryRelation", couponSpuCategoryRelation);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:couponspucategoryrelation:save")
    public R save(@RequestBody CouponSpuCategoryRelationEntity couponSpuCategoryRelation){
        couponSpuCategoryRelationService.save(couponSpuCategoryRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:couponspucategoryrelation:update")
    public R update(@RequestBody CouponSpuCategoryRelationEntity couponSpuCategoryRelation){
        couponSpuCategoryRelationService.updateById(couponSpuCategoryRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:couponspucategoryrelation:delete")
    public R delete(@RequestBody Long[] ids){
        couponSpuCategoryRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
