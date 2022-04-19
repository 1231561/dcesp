package com.qin.dcesp.controller.interceptor;

import com.qin.dcesp.entity.LoginTicket;
import com.qin.dcesp.entity.User;
import com.qin.dcesp.service.UserService;
import com.qin.dcesp.utils.CookieUtil;
import com.qin.dcesp.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求到达控制器前
        //请求前进行授权
        String ticket = CookieUtil.getValueFromCookie(request,"ticket");
        if(ticket != null){
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                User user = userService.selectUserById(loginTicket.getUserId());
                //防止多线程的问题,防止user这个数据冲突,可以使用ThreadLocal
                hostHolder.setUser(user);
                //现在要构建用户认证的结果,并存入SecurityContext,以便于Security进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,user.getPassword(),userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;//要true,不然后面不会执行了
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //响应返回客户端前
        //这里使用上面存的user
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //请求和响应完成后
        //请求完成后清理数据
        hostHolder.clear();
        //这里也要对授权进行清理
       // SecurityContextHolder.clearContext();
    }
}
