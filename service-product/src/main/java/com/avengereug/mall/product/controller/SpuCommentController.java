package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.SpuCommentEntity;
import com.avengereug.mall.product.service.SpuCommentService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 商品评价
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/spucomment")
public class SpuCommentController {

    @Autowired
    private SpuCommentService spuCommentService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("product:spucomment:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuCommentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("product:spucomment:info")
    public R info(@PathVariable("id") Long id){
        SpuCommentEntity spuComment = spuCommentService.getById(id);

        return R.ok().put("spuComment", spuComment);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:spucomment:save")
    public R save(@RequestBody SpuCommentEntity spuComment){
        spuCommentService.save(spuComment);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:spucomment:update")
    public R update(@RequestBody SpuCommentEntity spuComment){
        spuCommentService.updateById(spuComment);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:spucomment:delete")
    public R delete(@RequestBody Long[] ids){
        spuCommentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
