package com.avengereug.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.order.entity.OrderReturnReasonEntity;
import com.avengereug.mall.order.service.OrderReturnReasonService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 退货原因
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:09:44
 */
@RestController
@RequestMapping("order/orderreturnreason")
public class OrderReturnReasonController {

    @Autowired
    private OrderReturnReasonService orderReturnReasonService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("order:orderreturnreason:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderReturnReasonService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("order:orderreturnreason:info")
    public R info(@PathVariable("id") Long id){
        OrderReturnReasonEntity orderReturnReason = orderReturnReasonService.getById(id);

        return R.ok().put("orderReturnReason", orderReturnReason);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("order:orderreturnreason:save")
    public R save(@RequestBody OrderReturnReasonEntity orderReturnReason){
        orderReturnReasonService.save(orderReturnReason);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("order:orderreturnreason:update")
    public R update(@RequestBody OrderReturnReasonEntity orderReturnReason){
        orderReturnReasonService.updateById(orderReturnReason);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("order:orderreturnreason:delete")
    public R delete(@RequestBody Long[] ids){
        orderReturnReasonService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
