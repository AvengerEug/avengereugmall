package com.avengereug.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.avengereug.mall.auth.common.vo.UserLoginVo;
import com.avengereug.mall.auth.common.vo.UserRegisterVo;
import com.avengereug.mall.common.Enum.BusinessCodeEnum;
import com.avengereug.mall.common.controller.BaseController;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.member.entity.MemberEntity;
import com.avengereug.mall.member.exception.PhoneException;
import com.avengereug.mall.member.exception.UsernameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.member.service.MemberService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 会员
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:13:49
 */
@RestController
@RequestMapping("member/member")
public class MemberController extends BaseController {

    @Autowired
    private MemberService memberService;


    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping(value = "/register")
    public R register(@RequestBody UserRegisterVo vo) {
        try {
            memberService.register(vo);
        } catch (PhoneException e) {
            return R.error(BusinessCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BusinessCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameException e) {
            return R.error(BusinessCodeEnum.USER_EXIST_EXCEPTION.getCode(), BusinessCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }

        return R.ok();
    }

    @PostMapping(value = "/login")
    public R login(@RequestBody UserLoginVo vo) {

        MemberEntity memberEntity = memberService.login(vo);

        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BusinessCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BusinessCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
        }
    }


}
