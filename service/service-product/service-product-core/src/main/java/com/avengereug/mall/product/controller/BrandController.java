package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.avengereug.mall.common.controller.BaseController;
import com.avengereug.mall.common.utils.jsr303.valid.group.SaveGroup;
import com.avengereug.mall.common.utils.jsr303.valid.group.UpdateGroup;
import com.avengereug.mall.common.utils.jsr303.valid.group.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.BrandEntity;
import com.avengereug.mall.product.service.BrandService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;


/**
 * 品牌
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/brand")
public class BrandController extends BaseController {

    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(SaveGroup.class) @RequestBody BrandEntity brand){
        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
        brandService.updateCascade(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @PutMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand) {

        // 新创建一个对象，只获取到brandId和showStatus即可，防止更新到其他字段
        BrandEntity brandInner = new BrandEntity();
        brandInner.setBrandId(brand.getBrandId());
        brandInner.setShowStatus(brand.getShowStatus());

        brandService.updateById(brandInner);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
