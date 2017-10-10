package com.ank.ssoclient.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthFacade {

    boolean isLogin(HttpServletRequest request);

    void jumpLogin(HttpServletRequest request, HttpServletResponse response) throws IOException;

    boolean isVerifyToken(HttpServletRequest request);

    boolean verifyToken(HttpServletRequest request);

    void setLogin(HttpServletRequest request);

    boolean isLogout(HttpServletRequest request);

    void logout(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
