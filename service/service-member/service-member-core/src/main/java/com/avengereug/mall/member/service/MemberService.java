package com.avengereug.mall.member.service;

import com.avengereug.mall.auth.common.vo.UserLoginVo;
import com.avengereug.mall.auth.common.vo.UserRegisterVo;
import com.avengereug.mall.member.entity.MemberEntity;
import com.avengereug.mall.member.exception.PhoneException;
import com.avengereug.mall.member.exception.UsernameException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;

import java.util.Map;

/**
 * 会员
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:13:49
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVo vo);

    /**
     * 判断邮箱是否重复
     * @param phone
     * @return
     */
    void checkPhoneUnique(String phone) throws PhoneException;

    /**
     * 判断用户名是否重复
     * @param userName
     * @return
     */
    void checkUserNameUnique(String userName) throws UsernameException;

    MemberEntity login(UserLoginVo vo);
}

