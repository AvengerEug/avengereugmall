package com.avengereug.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.member.entity.MemberLoginLogEntity;
import com.avengereug.mall.member.service.MemberLoginLogService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 会员登录记录
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:13:49
 */
@RestController
@RequestMapping("member/memberloginlog")
public class MemberLoginLogController {

    @Autowired
    private MemberLoginLogService memberLoginLogService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("member:memberloginlog:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberLoginLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("member:memberloginlog:info")
    public R info(@PathVariable("id") Long id){
        MemberLoginLogEntity memberLoginLog = memberLoginLogService.getById(id);

        return R.ok().put("memberLoginLog", memberLoginLog);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("member:memberloginlog:save")
    public R save(@RequestBody MemberLoginLogEntity memberLoginLog){
        memberLoginLogService.save(memberLoginLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("member:memberloginlog:update")
    public R update(@RequestBody MemberLoginLogEntity memberLoginLog){
        memberLoginLogService.updateById(memberLoginLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("member:memberloginlog:delete")
    public R delete(@RequestBody Long[] ids){
        memberLoginLogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
