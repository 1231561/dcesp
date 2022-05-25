package com.qin.dcesp.config;

import com.qin.dcesp.utils.CommunityConstant;
import com.qin.dcesp.utils.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略静态资源的请求
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests().antMatchers(
                "/personalCenter/**",
                "/working/**",
                "/workingSpace"
        ).hasAnyAuthority("USER","ADMIN")//表示这些路径,现在有以下权限的话就可以访问
                .antMatchers(
                        "/data/**").hasAnyAuthority("ADMIN")
                .anyRequest().permitAll()//其他路径通通允许.
                .and().csrf().disable();//偷懒,取消csrf攻击防护.
        //权限不足时的处理:
        http.exceptionHandling()
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
                    String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                    if("XMLHttpRequest".equals(xRequestedWith)){
                        //如果是一个异步请求
                        httpServletResponse.setContentType("application/plain;charset=utf-8");
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write(CommunityUtil.getJSONString(403,"未登录!"));
                    }else{
                        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
                    }
                })////权限不足,进入这个主键中.
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> {
                    String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                    if("XMLHttpRequest".equals(xRequestedWith)){
                        httpServletResponse.setContentType("application/plain;charset=utf-8");
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write(CommunityUtil.getJSONString(403,"你没有访问该页面的权限!"));
                    }else{
                        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/index");
                    }
                });
        //还有问题,就是Security这个玩意默认会自动拦截名为logout的请求,我们现在需要走自己的logout,而不是它的.
        //我们覆盖它的逻辑即可.
        http.logout().logoutUrl("/securitylogout");//修改它处理的那个登出的路径,搞一个不存在的请求,让它去拦截,这样我们的logout就不会被它栏了.
    }
}
