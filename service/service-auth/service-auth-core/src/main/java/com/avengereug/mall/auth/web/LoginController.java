package com.avengereug.mall.auth.web;

import com.avengereug.mall.auth.feign.MemberClient;
import com.avengereug.mall.auth.feign.SMSClient;
import com.avengereug.mall.auth.common.vo.UserRegisterVo;
import com.avengereug.mall.common.Enum.BusinessCodeEnum;
import com.avengereug.mall.common.constants.AuthServerConstant;
import com.avengereug.mall.common.controller.BaseController;
import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.common.utils.RPCResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private SMSClient smsClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberClient memberClient;

    /**
     *
     * 1、验证码5分钟内有效。
     * 2、并且同一个用户60s内不允许重复调用发送验证码功能
     *
     * 实现上述两个功能的核心：
     * 1、把验证码存入redis中，其中用户的手机号作为key，而一般在使用redis时，由于它是中间件，因此一般是有一些前缀标识的
     *    在此处的前缀为：  "auth:sms:code:"   标识为auth服务的sms模块的验证码功能。
     *    eg：完整的key为：  "auth:sms:code:15512345678"
     *    同时需要设置它的过期时间来完成它的有效期功能。所以我们在存储时可以指定它过期的时间
     * 2、同时，我们还需要对value做文章，在存储value时，把当前操作redis的时间也存进去，这样就能标识这个redis是时候存入的。
     *    eg：我们可以存储的value为： yzm_60   它代表的含义为验证码的值为yzm, 60s后过期
     *   所以当一个用户一直调用发送验证码功能时，我们可以先从redis获取，再使用_分割，拿到它的过期时间expiredTime，
     *   然后再将当前请求发送验证码的时间与expiredTime做减法运算。
     *   如果小于60，则认为是在60s内一直调用发送验证码的功能，此时直接返回错误信息。
     *   如果大于60，则认为是正常的情况：用户在60s后重新调用了发送验证码的功能，此时应该要发验证码，并且覆盖上一次获取的验证码，由
     *   新验证码覆盖redis的旧验证码
     *
     * @param phone
     * @return
     */
    @GetMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        String codeKey = AuthServerConstant.AUTH_SMS_CODE_CACHE_PREFIX + phone;

        String codeFromRedis = stringRedisTemplate.opsForValue().get(AuthServerConstant.AUTH_SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(codeFromRedis)) {
            // 判断用户是否一直调用此api，防抖操作
            long l = Long.parseLong(codeFromRedis.split("_")[1]);
            // 时间间隔小于5分钟(300s == 300000ms)
            if (System.currentTimeMillis() - l < 300000) {
                return R.error(BusinessCodeEnum.REPEAT_CALL_SEND_SMS.getCode(), BusinessCodeEnum.REPEAT_CALL_SEND_SMS.getMsg());
            }
        }

        String code = UUID.randomUUID().toString().substring(0, 6);
        String codeSavedRedis = code + "_" + System.currentTimeMillis();

        // 以uuid的前5位和当前时间戳作为value，存入redis中  key_phone, code_currentTime
        stringRedisTemplate.opsForValue().set(codeKey, codeSavedRedis, 5, TimeUnit.MINUTES);

        smsClient.send(phone, code);

        return R.ok();
    }


    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo vos, BindingResult result, RedirectAttributes attributes) {
        //如果有错误回到注册页面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors",errors);

            //效验出错回到注册页面
            return "redirect:auth.avengereugmall.com/reg.html";
        }

        //1、效验验证码
        String code = vos.getCode();

        //获取存入Redis里的验证码
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.AUTH_SMS_CODE_CACHE_PREFIX + vos.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            //截取字符串
            if (code.equals(redisCode.split("_")[0])) {
                //删除验证码;令牌机制
                stringRedisTemplate.delete(AuthServerConstant.AUTH_SMS_CODE_CACHE_PREFIX+vos.getPhone());
                R register = memberClient.register(vos);
                if (register.getCode() == 0) {
                    // 在com.avengereug.mall.auth.web.config.WebConfig中配置了，直接写配置的key即可
                    return "redirect:auth.avengereugmall.com/login.html";
                } else {
                    //失败
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", register.getMsg());
                    attributes.addFlashAttribute("errors",errors);
                    return "redirect:auth.avengereugmall.com/reg.html";
                }

            } else {
                //效验出错回到注册页面
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:auth.avengereugmall.com/reg.html";
            }
        } else {
            //效验出错回到注册页面
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:auth.avengereugmall.com/reg.html";
        }
    }

}
