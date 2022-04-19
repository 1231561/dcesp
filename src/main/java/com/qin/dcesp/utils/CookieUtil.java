package com.qin.dcesp.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {

    //从Cookie中取指定的键值对
    public static String getValueFromCookie(HttpServletRequest request,String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空!");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
