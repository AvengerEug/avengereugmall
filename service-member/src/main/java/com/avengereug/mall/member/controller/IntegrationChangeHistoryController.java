package com.avengereug.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.member.entity.IntegrationChangeHistoryEntity;
import com.avengereug.mall.member.service.IntegrationChangeHistoryService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 积分变化历史记录
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:13:49
 */
@RestController
@RequestMapping("member/integrationchangehistory")
public class IntegrationChangeHistoryController {

    @Autowired
    private IntegrationChangeHistoryService integrationChangeHistoryService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("member:integrationchangehistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = integrationChangeHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("member:integrationchangehistory:info")
    public R info(@PathVariable("id") Long id){
        IntegrationChangeHistoryEntity integrationChangeHistory = integrationChangeHistoryService.getById(id);

        return R.ok().put("integrationChangeHistory", integrationChangeHistory);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("member:integrationchangehistory:save")
    public R save(@RequestBody IntegrationChangeHistoryEntity integrationChangeHistory){
        integrationChangeHistoryService.save(integrationChangeHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("member:integrationchangehistory:update")
    public R update(@RequestBody IntegrationChangeHistoryEntity integrationChangeHistory){
        integrationChangeHistoryService.updateById(integrationChangeHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("member:integrationchangehistory:delete")
    public R delete(@RequestBody Long[] ids){
        integrationChangeHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
