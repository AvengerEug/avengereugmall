package com.avengereug.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.order.entity.OrderItemEntity;
import com.avengereug.mall.order.service.OrderItemService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 订单项信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:09:44
 */
@RestController
@RequestMapping("order/orderitem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("order:orderitem:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderItemService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("order:orderitem:info")
    public R info(@PathVariable("id") Long id){
        OrderItemEntity orderItem = orderItemService.getById(id);

        return R.ok().put("orderItem", orderItem);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("order:orderitem:save")
    public R save(@RequestBody OrderItemEntity orderItem){
        orderItemService.save(orderItem);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("order:orderitem:update")
    public R update(@RequestBody OrderItemEntity orderItem){
        orderItemService.updateById(orderItem);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("order:orderitem:delete")
    public R delete(@RequestBody Long[] ids){
        orderItemService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
