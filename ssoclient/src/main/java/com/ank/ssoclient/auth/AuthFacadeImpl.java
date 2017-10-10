package com.ank.ssoclient.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ank.ssoclient.common.UrlHelper;
import com.ank.ssoclient.entity.UserInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthFacadeImpl implements AuthFacade{
    /** 登录标识 */
    private static final String IS_LOGIN = "islogin";
    /** SSO服务器地址 */
    private static final String SSO_SERVER_URL = "http://sso.test.com:8080/sso/";
    /** token标识 */
    private static final String TOKEN = "token";
    /** returnUrl标识 */
    private static final String RETURN_URL = "returnUrl";
    /** token和session的映射关系 */
    private static final Map<String, HttpSession> TOKEN_SESSION_MAP = new ConcurrentHashMap<String, HttpSession>();

    public boolean isLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object isLogin = session.getAttribute(IS_LOGIN);
        if(isLogin != null){
            return true;
        }
        return false;
    }

    public void jumpLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String returnUrl = request.getRequestURL().toString();
        String url = UrlHelper.parseUrl(SSO_SERVER_URL, "login?" + RETURN_URL + "=" + returnUrl);
        response.sendRedirect(url);
    }

    public boolean isVerifyToken(HttpServletRequest request) {
        String token = request.getParameter(TOKEN);
        if(token != null){
            return true;
        }else{
            return false;
        }
    }

    public boolean verifyToken(HttpServletRequest request) {
        String token = request.getParameter(TOKEN);
        String url = UrlHelper.parseUrl(SSO_SERVER_URL, "verifyToken?" + TOKEN + "=" + token + "&" + RETURN_URL + "=" + request.getRequestURL());
        try {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            try {
                HttpResponse response = httpClient.execute(httpGet);
                String content = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(content);
                int code = jsonObject.getIntValue("code");
                if (code == 0) {
                    UserInfo userInfo = jsonObject.getObject("data", UserInfo.class);
                    request.getSession().setAttribute("userinfo", userInfo);
                    return true;
                }
            }finally {
                if(httpClient != null) httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setLogin(HttpServletRequest request) {
        String token = request.getParameter(TOKEN);
        HttpSession session = request.getSession();
        session.setAttribute(IS_LOGIN, true);
        session.setAttribute(TOKEN, token);

        HttpSession oldSession = TOKEN_SESSION_MAP.put(token, session);
        if(oldSession != null){
            oldSession.invalidate();
        }
    }

    public boolean isLogout(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if(queryString != null && queryString.indexOf("logout") != -1){
            return true;
        }else{
            return false;
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter(TOKEN);
        if(token == null){
            String returnUrl = request.getRequestURL().toString();
            returnUrl = returnUrl.replaceAll("\\?logout", "");
            HttpSession session = request.getSession();
            token = (String) session.getAttribute(TOKEN);
            String url = UrlHelper.parseUrl(SSO_SERVER_URL, "logout?" + TOKEN + "=" + token + "&" + RETURN_URL + "=" + returnUrl);
            response.sendRedirect(url);
        }else{
            HttpSession session = TOKEN_SESSION_MAP.get(token);
            if(session != null){
                session.invalidate();
                TOKEN_SESSION_MAP.remove(token);
            }
        }
    }
}
