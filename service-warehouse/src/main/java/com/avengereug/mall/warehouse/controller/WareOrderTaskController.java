package com.avengereug.mall.warehouse.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.warehouse.entity.WareOrderTaskEntity;
import com.avengereug.mall.warehouse.service.WareOrderTaskService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 库存工作单
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 10:59:40
 */
@RestController
@RequestMapping("warehouse/wareordertask")
public class WareOrderTaskController {

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("warehouse:wareordertask:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareOrderTaskService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("warehouse:wareordertask:info")
    public R info(@PathVariable("id") Long id){
        WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getById(id);

        return R.ok().put("wareOrderTask", wareOrderTask);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("warehouse:wareordertask:save")
    public R save(@RequestBody WareOrderTaskEntity wareOrderTask){
        wareOrderTaskService.save(wareOrderTask);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("warehouse:wareordertask:update")
    public R update(@RequestBody WareOrderTaskEntity wareOrderTask){
        wareOrderTaskService.updateById(wareOrderTask);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("warehouse:wareordertask:delete")
    public R delete(@RequestBody Long[] ids){
        wareOrderTaskService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
