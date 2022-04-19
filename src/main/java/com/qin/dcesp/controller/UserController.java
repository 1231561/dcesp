package com.qin.dcesp.controller;


import com.google.code.kaptcha.Producer;
import com.qin.dcesp.entity.User;
import com.qin.dcesp.service.UserService;
import com.qin.dcesp.utils.CommunityUtil;
import com.qin.dcesp.utils.HostHolder;
import com.qin.dcesp.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class UserController {

    //默认凭证的过期时长
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //记住我的过期时长
    int REMEBER_SECONDS = 3600 * 12 * 12;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    Producer kaptchaProducer;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @RequestMapping(path = "/register")
    public String getRegisterPage(){
        return "/site/signin";
    }

    @RequestMapping(path = "/login")
    public String getLoginPage() {
        return "/site/login";
    }

    //注册请求
    @PostMapping(path = "/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.registerAUser(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功!");
            model.addAttribute("target","/index");
            return "/site/signinOk";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/signin";
        }
    }

    //获取验证码图片
    @RequestMapping(path = "/kaptcha")
    public void getKaptchaPage(HttpServletResponse response){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath("http://localhost:8080/dcesp/");
        response.addCookie(cookie);
        //验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        //输出图片
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("生成图片失败" + e.getMessage());
        }
    }

    //登录
    @PostMapping(path = "/login")
    public String login(String username, String password, String code,
                        boolean rememberMe, Model model, HttpServletResponse response,
                        @CookieValue("kaptchaOwner")String kaptchaOwner){
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        //检查验证码
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确!");
            return "/site/login";
        }
        //检查账号密码
        int expireSeconds = rememberMe ? REMEBER_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expireSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath("/dcesp");
            cookie.setMaxAge(expireSeconds);
            response.addCookie(cookie);
            model.addAttribute("username",username);
            model.addAttribute("loginUser",map.get("loginUser"));
            return "/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }
    //退出登录
    @RequestMapping(path = "/logout")
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "/site/login";
    }

    //个人中心
    @RequestMapping(path = "/personalCenter/{userId}")
    public String getPersonalCenterPage(@PathVariable("userId")int userId, Model model){
        User user = userService.selectUserById(userId);
        model.addAttribute("username",user.getUsername());
        model.addAttribute("password",user.getPassword());
        return "/site/personal-detail";
    }

}
