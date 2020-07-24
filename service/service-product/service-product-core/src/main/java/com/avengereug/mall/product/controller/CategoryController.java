package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.CategoryEntity;
import com.avengereug.mall.product.service.CategoryService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 商品三级分类
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类及子分类，以树形结构组装起来
     */
    @GetMapping("/list/tree")
    public R list() {
        List<CategoryEntity> lists = categoryService.listWithTree();

        return R.ok().put("data", lists);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
        /**
         * TODO
         * 1. 校验分类名是否相同
         *
         */
        categoryService.save(category);

        return R.ok().put("category", category);
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
        categoryService.updateById(category);

        return R.ok();
    }

    @PutMapping("/update/sort")
    //@RequiresPermissions("product:category:update")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 删除
     *
     * 请求体中不需要指定任何key,
     * 直接写 [100] 即可
     *
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        categoryService.removeCategoryByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
