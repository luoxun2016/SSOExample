package com.ank.ssoserver.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieHelper {

    public static Cookie getCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;

        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                return cookie;
            }
        }

        return null;
    }

}
