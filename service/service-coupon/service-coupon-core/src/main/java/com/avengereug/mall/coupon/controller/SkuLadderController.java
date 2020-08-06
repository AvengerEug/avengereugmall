package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.avengereug.mall.coupon.to.SkuLadderTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.SkuLadderEntity;
import com.avengereug.mall.coupon.service.SkuLadderService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 商品阶梯价格
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/skuladder")
public class SkuLadderController {

    @Autowired
    private SkuLadderService skuLadderService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:skuladder:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuLadderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:skuladder:info")
    public R info(@PathVariable("id") Long id){
        SkuLadderEntity skuLadder = skuLadderService.getById(id);

        return R.ok().put("skuLadder", skuLadder);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:skuladder:save")
    public R save(@RequestBody SkuLadderEntity skuLadder){
        skuLadderService.save(skuLadder);

        return R.ok();
    }

    /**
     * 微服务内部调用存储
     */
    @PostMapping("/inner/save")
    public R innerSave(@RequestBody SkuLadderTO skuLadderTo){
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        BeanUtils.copyProperties(skuLadderTo, skuLadder);
        skuLadder.setAddOther(skuLadderTo.getCountStatus());

        skuLadderService.save(skuLadder);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:skuladder:update")
    public R update(@RequestBody SkuLadderEntity skuLadder){
        skuLadderService.updateById(skuLadder);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:skuladder:delete")
    public R delete(@RequestBody Long[] ids){
        skuLadderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
