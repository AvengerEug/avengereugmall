package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.SkuImagesEntity;
import com.avengereug.mall.product.service.SkuImagesService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * sku图片
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/skuimages")
public class SkuImagesController {

    @Autowired
    private SkuImagesService skuImagesService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("product:skuimages:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuImagesService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("product:skuimages:info")
    public R info(@PathVariable("id") Long id){
        SkuImagesEntity skuImages = skuImagesService.getById(id);

        return R.ok().put("skuImages", skuImages);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:skuimages:save")
    public R save(@RequestBody SkuImagesEntity skuImages){
        skuImagesService.save(skuImages);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:skuimages:update")
    public R update(@RequestBody SkuImagesEntity skuImages){
        skuImagesService.updateById(skuImages);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:skuimages:delete")
    public R delete(@RequestBody Long[] ids){
        skuImagesService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
