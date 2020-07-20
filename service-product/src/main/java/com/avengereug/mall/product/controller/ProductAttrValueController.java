package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.ProductAttrValueEntity;
import com.avengereug.mall.product.service.ProductAttrValueService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * spu属性值
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/productattrvalue")
public class ProductAttrValueController {

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("product:productattrvalue:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = productAttrValueService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("product:productattrvalue:info")
    public R info(@PathVariable("id") Long id){
        ProductAttrValueEntity productAttrValue = productAttrValueService.getById(id);

        return R.ok().put("productAttrValue", productAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:productattrvalue:save")
    public R save(@RequestBody ProductAttrValueEntity productAttrValue){
        productAttrValueService.save(productAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:productattrvalue:update")
    public R update(@RequestBody ProductAttrValueEntity productAttrValue){
        productAttrValueService.updateById(productAttrValue);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:productattrvalue:delete")
    public R delete(@RequestBody Long[] ids){
        productAttrValueService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
