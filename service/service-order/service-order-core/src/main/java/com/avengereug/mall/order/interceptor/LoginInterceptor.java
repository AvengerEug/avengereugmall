package com.avengereug.mall.order.interceptor;

import com.avengereug.mall.member.vo.MemberResponseVo;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.avengereug.mall.common.constants.AuthServerConstant.LOGIN_USER;

public class LoginInterceptor implements HandlerInterceptor {

    // 将用户信息保存到ThreadLocal中，保证同一个请求中的数据能在整个线程调用链中获取到参数信息
    public static ThreadLocal<MemberResponseVo> threadLocal = new ThreadLocal<>();


    public static MemberResponseVo getCurrentUser() {
        return threadLocal.get();
    }

    /***
     * 目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        //获得当前登录用户的信息
        Object loginUser = session.getAttribute(LOGIN_USER);

        if (loginUser != null) {
            threadLocal.set((MemberResponseVo) loginUser);
            //用户登录了
            return true;
        } else {
            request.getSession().setAttribute("msg", "请先进行登录");
            response.sendRedirect("http://auth.avengereugmall.com/login.html");
            return false;
        }
    }

}
