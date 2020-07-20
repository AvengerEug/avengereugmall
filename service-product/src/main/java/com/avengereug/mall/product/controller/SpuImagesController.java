package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.SpuImagesEntity;
import com.avengereug.mall.product.service.SpuImagesService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * spu图片
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/spuimages")
public class SpuImagesController {

    @Autowired
    private SpuImagesService spuImagesService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("product:spuimages:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuImagesService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("product:spuimages:info")
    public R info(@PathVariable("id") Long id){
        SpuImagesEntity spuImages = spuImagesService.getById(id);

        return R.ok().put("spuImages", spuImages);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:spuimages:save")
    public R save(@RequestBody SpuImagesEntity spuImages){
        spuImagesService.save(spuImages);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:spuimages:update")
    public R update(@RequestBody SpuImagesEntity spuImages){
        spuImagesService.updateById(spuImages);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:spuimages:delete")
    public R delete(@RequestBody Long[] ids){
        spuImagesService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
