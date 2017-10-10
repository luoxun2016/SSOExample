package com.ank.ssoserver.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ank.ssoserver.common.CookieHelper;
import com.ank.ssoserver.common.UrlHelper;
import com.ank.ssoserver.entity.UserInfo;
import org.apache.catalina.User;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthFacadeImpl implements AuthFacade{
    private static final String SSO_TOKEN = "sso.token";
    private static final Map<String, UserInfo> TOKEN_MAP = new ConcurrentHashMap<String, UserInfo>();
    private static final Map<String, Set<String>> TOKEN_URL_MAP = new ConcurrentHashMap<String, Set<String>>();

    public boolean isLogin(HttpServletRequest request) {
        Cookie cookie = CookieHelper.getCookie(request, SSO_TOKEN);
        if(cookie != null){
            return TOKEN_MAP.containsKey(cookie.getValue());
        }else{
            return false;
        }
    }

    public UserInfo getUserInfo(String username, String password) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassworld(password);
        return userInfo;
    }

    public String createToken(HttpServletResponse response, UserInfo userInfo) {
        String token = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(SSO_TOKEN, token);
        response.addCookie(cookie);
        TOKEN_MAP.put(token, userInfo);
        return token;
    }

    public UserInfo getUserInfo(String token) {
        return TOKEN_MAP.get(token);
    }

    public boolean verifyToken(String token) {
        if(token == null) return false;
        return TOKEN_MAP.containsKey(token);
    }

    public String getToken(HttpServletRequest request) {
        Cookie cookie = CookieHelper.getCookie(request, SSO_TOKEN);
        if(cookie != null){
            return cookie.getValue();
        }else{
            return null;
        }
    }

    public void clearToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(SSO_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        TOKEN_MAP.remove(token);
    }

    public void registryToken(String token, String returnUrl) {
        Set<String> urlset = TOKEN_URL_MAP.get(token);
        if(urlset == null){
            urlset = new TreeSet<String>();
            TOKEN_URL_MAP.put(token, urlset);
        }
        String rootUrl = UrlHelper.getRootUrl(returnUrl);
        if(rootUrl != null){
            urlset.add(rootUrl);
        }
    }

    public void unRegistryToken(String token) {
        Set<String> urlset = TOKEN_URL_MAP.get(token);
        if(urlset == null) return;

        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            for(String url : urlset){
                url = url + "?logout&token=" + token;
                System.out.println("logout:"+url);
                try {
                    HttpGet httpGet = new HttpGet(url);
                    httpClient.execute(httpGet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
