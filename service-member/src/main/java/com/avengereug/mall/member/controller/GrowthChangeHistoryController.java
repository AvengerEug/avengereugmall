package com.avengereug.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.member.entity.GrowthChangeHistoryEntity;
import com.avengereug.mall.member.service.GrowthChangeHistoryService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 成长值变化历史记录
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:13:49
 */
@RestController
@RequestMapping("member/growthchangehistory")
public class GrowthChangeHistoryController {

    @Autowired
    private GrowthChangeHistoryService growthChangeHistoryService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("member:growthchangehistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = growthChangeHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("member:growthchangehistory:info")
    public R info(@PathVariable("id") Long id){
        GrowthChangeHistoryEntity growthChangeHistory = growthChangeHistoryService.getById(id);

        return R.ok().put("growthChangeHistory", growthChangeHistory);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("member:growthchangehistory:save")
    public R save(@RequestBody GrowthChangeHistoryEntity growthChangeHistory){
        growthChangeHistoryService.save(growthChangeHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("member:growthchangehistory:update")
    public R update(@RequestBody GrowthChangeHistoryEntity growthChangeHistory){
        growthChangeHistoryService.updateById(growthChangeHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("member:growthchangehistory:delete")
    public R delete(@RequestBody Long[] ids){
        growthChangeHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
