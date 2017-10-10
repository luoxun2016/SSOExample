package com.ank.ssoserver.auth;

import com.ank.ssoserver.entity.UserInfo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

public interface AuthFacade {

    boolean isLogin(HttpServletRequest request);

    UserInfo getUserInfo(String username, String password);

    String createToken(HttpServletResponse response, UserInfo userInfo);

    UserInfo getUserInfo(String token);

    String getToken(HttpServletRequest request);

    void clearToken(HttpServletResponse response, String token);

    void registryToken(String token, String returnUrl);

    void unRegistryToken(String token);
}
