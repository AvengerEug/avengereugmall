package com.avengereug.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.order.entity.OrderReturnApplyEntity;
import com.avengereug.mall.order.service.OrderReturnApplyService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 订单退货申请
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:09:44
 */
@RestController
@RequestMapping("order/orderreturnapply")
public class OrderReturnApplyController {

    @Autowired
    private OrderReturnApplyService orderReturnApplyService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("order:orderreturnapply:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderReturnApplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("order:orderreturnapply:info")
    public R info(@PathVariable("id") Long id){
        OrderReturnApplyEntity orderReturnApply = orderReturnApplyService.getById(id);

        return R.ok().put("orderReturnApply", orderReturnApply);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("order:orderreturnapply:save")
    public R save(@RequestBody OrderReturnApplyEntity orderReturnApply){
        orderReturnApplyService.save(orderReturnApply);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("order:orderreturnapply:update")
    public R update(@RequestBody OrderReturnApplyEntity orderReturnApply){
        orderReturnApplyService.updateById(orderReturnApply);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("order:orderreturnapply:delete")
    public R delete(@RequestBody Long[] ids){
        orderReturnApplyService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
