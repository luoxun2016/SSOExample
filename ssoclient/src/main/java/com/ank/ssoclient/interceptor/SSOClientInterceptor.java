package com.ank.ssoclient.interceptor;

import com.ank.ssoclient.auth.AuthFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SSOClientInterceptor extends HandlerInterceptorAdapter{
    @Autowired
    private AuthFacade authFacade;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isLogout = authFacade.isLogout(request);
        if(isLogout) {
            authFacade.logout(request, response);
            return false;
        }

        boolean isLogin = authFacade.isLogin(request);
        if(isLogin) {
            return true;
        }

        boolean isVerifyToken = authFacade.isVerifyToken(request);
        if(isVerifyToken){
            boolean isVerify = authFacade.verifyToken(request);
            if(isVerify){
                authFacade.setLogin(request);
                return true;
            }
        }

        authFacade.jumpLogin(request, response);
        return false;
    }

}
