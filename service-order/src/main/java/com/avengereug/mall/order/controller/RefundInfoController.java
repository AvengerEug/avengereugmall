package com.avengereug.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.order.entity.RefundInfoEntity;
import com.avengereug.mall.order.service.RefundInfoService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 退款信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:09:44
 */
@RestController
@RequestMapping("order/refundinfo")
public class RefundInfoController {

    @Autowired
    private RefundInfoService refundInfoService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("order:refundinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = refundInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("order:refundinfo:info")
    public R info(@PathVariable("id") Long id){
        RefundInfoEntity refundInfo = refundInfoService.getById(id);

        return R.ok().put("refundInfo", refundInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("order:refundinfo:save")
    public R save(@RequestBody RefundInfoEntity refundInfo){
        refundInfoService.save(refundInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("order:refundinfo:update")
    public R update(@RequestBody RefundInfoEntity refundInfo){
        refundInfoService.updateById(refundInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("order:refundinfo:delete")
    public R delete(@RequestBody Long[] ids){
        refundInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
