package com.avengereug.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.order.entity.OrderSettingEntity;
import com.avengereug.mall.order.service.OrderSettingService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 订单配置信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:09:44
 */
@RestController
@RequestMapping("order/ordersetting")
public class OrderSettingController {

    @Autowired
    private OrderSettingService orderSettingService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("order:ordersetting:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderSettingService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("order:ordersetting:info")
    public R info(@PathVariable("id") Long id){
        OrderSettingEntity orderSetting = orderSettingService.getById(id);

        return R.ok().put("orderSetting", orderSetting);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("order:ordersetting:save")
    public R save(@RequestBody OrderSettingEntity orderSetting){
        orderSettingService.save(orderSetting);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("order:ordersetting:update")
    public R update(@RequestBody OrderSettingEntity orderSetting){
        orderSettingService.updateById(orderSetting);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("order:ordersetting:delete")
    public R delete(@RequestBody Long[] ids){
        orderSettingService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
