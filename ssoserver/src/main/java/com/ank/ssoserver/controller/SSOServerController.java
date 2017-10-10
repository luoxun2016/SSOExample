package com.ank.ssoserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.ank.ssoserver.auth.AuthFacade;
import com.ank.ssoserver.common.UrlHelper;
import com.ank.ssoserver.entity.UserInfo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/sso")
public class SSOServerController {
    /** token标识 */
    private static final String TOKEN = "token";
    /** returnUrl标识 */
    private static final String RETURN_URL = "returnUrl";

    @Autowired
    private AuthFacade authFacade;

    @RequestMapping("/login")
    public Object login(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) throws Exception{
        String returnUrl = request.getParameter(RETURN_URL);
        if(returnUrl == null){
            throw new IllegalAccessException("return url is null");
        }

        boolean isLogin = authFacade.isLogin(request);
        if(isLogin){
            //已登录携带token重定向
            String token = authFacade.getToken(request);
            returnUrl = UrlHelper.join(returnUrl, TOKEN + "=" + token);
            return "redirect:" + returnUrl;
        }else{
            //未登录跳转到登录页
            modelAndView.addObject(RETURN_URL, returnUrl);
            modelAndView.setViewName("login");
            return modelAndView;
        }
    }

    @RequestMapping("/dologin")
    public Object doLogin(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) throws Exception{
        String returnUrl = request.getParameter(RETURN_URL);
        if(returnUrl == null){
            throw new IllegalAccessException("return url is null");
        }

        boolean isLogin = authFacade.isLogin(request);
        if(isLogin) {
            //已登录携带token重定向
            String token = authFacade.getToken(request);
            returnUrl = UrlHelper.join(returnUrl, TOKEN + "=" + token);
            return "redirect:" + returnUrl;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserInfo userInfo = authFacade.getUserInfo(username, password);
        if(userInfo != null){
            //用户信息验证通过创建token，并携带token重定向
            String token = authFacade.createToken(response, userInfo);
            returnUrl = UrlHelper.join(returnUrl, TOKEN + "=" + token);
            return "redirect:" + returnUrl;
        }else{
            modelAndView.addObject(RETURN_URL, returnUrl);
            modelAndView.setViewName("login");
            return modelAndView;
        }
    }

    @RequestMapping("/verifyToken")
    @ResponseBody
    public String verifyToken(HttpServletRequest request){
        //验证token有效性
        JSONObject jsonObject = new JSONObject();
        String token = request.getParameter(TOKEN);
        String returnUrl = request.getParameter(RETURN_URL);
        UserInfo userInfo = authFacade.getUserInfo(token);
        if(userInfo != null){
            authFacade.registryToken(token, returnUrl);
            jsonObject.put("code", 0);
            jsonObject.put("data", userInfo);
            return jsonObject.toJSONString();
        }else{
            jsonObject.put("code", 1);
            return jsonObject.toJSONString();
        }
    }

    @RequestMapping("/logout")
    public void logout(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = request.getParameter(TOKEN);
        String returnUrl = request.getParameter(RETURN_URL);
        if(token == null || returnUrl == null){
            throw new IllegalAccessException("return url is null");
        }
        authFacade.clearToken(response, token);
        authFacade.unRegistryToken(token);
        response.sendRedirect("login?" + RETURN_URL + "=" + returnUrl);
    }
}
