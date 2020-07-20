package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.CouponHistoryEntity;
import com.avengereug.mall.coupon.service.CouponHistoryService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 优惠券领取历史记录
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/couponhistory")
public class CouponHistoryController {

    @Autowired
    private CouponHistoryService couponHistoryService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:couponhistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:couponhistory:info")
    public R info(@PathVariable("id") Long id){
        CouponHistoryEntity couponHistory = couponHistoryService.getById(id);

        return R.ok().put("couponHistory", couponHistory);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:couponhistory:save")
    public R save(@RequestBody CouponHistoryEntity couponHistory){
        couponHistoryService.save(couponHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:couponhistory:update")
    public R update(@RequestBody CouponHistoryEntity couponHistory){
        couponHistoryService.updateById(couponHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:couponhistory:delete")
    public R delete(@RequestBody Long[] ids){
        couponHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
