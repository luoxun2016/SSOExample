package com.ank.ssoclient.controller;

import com.ank.ssoclient.auth.AuthFacade;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {

    @Autowired
    private AuthFacade authFacade;

    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
