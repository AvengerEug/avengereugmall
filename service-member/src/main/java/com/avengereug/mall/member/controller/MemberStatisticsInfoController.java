package com.avengereug.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.member.entity.MemberStatisticsInfoEntity;
import com.avengereug.mall.member.service.MemberStatisticsInfoService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 会员统计信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:13:49
 */
@RestController
@RequestMapping("member/memberstatisticsinfo")
public class MemberStatisticsInfoController {

    @Autowired
    private MemberStatisticsInfoService memberStatisticsInfoService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("member:memberstatisticsinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberStatisticsInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("member:memberstatisticsinfo:info")
    public R info(@PathVariable("id") Long id){
        MemberStatisticsInfoEntity memberStatisticsInfo = memberStatisticsInfoService.getById(id);

        return R.ok().put("memberStatisticsInfo", memberStatisticsInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("member:memberstatisticsinfo:save")
    public R save(@RequestBody MemberStatisticsInfoEntity memberStatisticsInfo){
        memberStatisticsInfoService.save(memberStatisticsInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("member:memberstatisticsinfo:update")
    public R update(@RequestBody MemberStatisticsInfoEntity memberStatisticsInfo){
        memberStatisticsInfoService.updateById(memberStatisticsInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("member:memberstatisticsinfo:delete")
    public R delete(@RequestBody Long[] ids){
        memberStatisticsInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
