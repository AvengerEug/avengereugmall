package com.avengereug.mall.warehouse.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.warehouse.entity.WareOrderTaskDetailEntity;
import com.avengereug.mall.warehouse.service.WareOrderTaskDetailService;
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
@RequestMapping("warehouse/wareordertaskdetail")
public class WareOrderTaskDetailController {

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("warehouse:wareordertaskdetail:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareOrderTaskDetailService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("warehouse:wareordertaskdetail:info")
    public R info(@PathVariable("id") Long id){
        WareOrderTaskDetailEntity wareOrderTaskDetail = wareOrderTaskDetailService.getById(id);

        return R.ok().put("wareOrderTaskDetail", wareOrderTaskDetail);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("warehouse:wareordertaskdetail:save")
    public R save(@RequestBody WareOrderTaskDetailEntity wareOrderTaskDetail){
        wareOrderTaskDetailService.save(wareOrderTaskDetail);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("warehouse:wareordertaskdetail:update")
    public R update(@RequestBody WareOrderTaskDetailEntity wareOrderTaskDetail){
        wareOrderTaskDetailService.updateById(wareOrderTaskDetail);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("warehouse:wareordertaskdetail:delete")
    public R delete(@RequestBody Long[] ids){
        wareOrderTaskDetailService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
