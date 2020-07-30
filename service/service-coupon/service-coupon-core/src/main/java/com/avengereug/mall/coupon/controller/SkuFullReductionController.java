package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.avengereug.mall.coupon.to.SkuFullReductionTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.SkuFullReductionEntity;
import com.avengereug.mall.coupon.service.SkuFullReductionService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 商品满减信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/skufullreduction")
public class SkuFullReductionController {

    @Autowired
    private SkuFullReductionService skuFullReductionService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:skufullreduction:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuFullReductionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:skufullreduction:info")
    public R info(@PathVariable("id") Long id){
        SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

        return R.ok().put("skuFullReduction", skuFullReduction);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:skufullreduction:save")
    public R save(@RequestBody SkuFullReductionEntity skuFullReduction){
        skuFullReductionService.save(skuFullReduction);

        return R.ok();
    }

    /**
     * 保存
     */
    @PostMapping("/save/inner")
    //@RequiresPermissions("coupon:skufullreduction:save")
    public R saveInner(@RequestBody SkuFullReductionTO skuFullReductionTo){
        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuFullReductionTo, skuFullReduction);
        skuFullReduction.setAddOther(skuFullReductionTo.getPriceStatus());

        skuFullReductionService.save(skuFullReduction);
        return R.ok();
    }


    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:skufullreduction:update")
    public R update(@RequestBody SkuFullReductionEntity skuFullReduction){
        skuFullReductionService.updateById(skuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:skufullreduction:delete")
    public R delete(@RequestBody Long[] ids){
        skuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
