package com.ank.ssoclient.config;

import com.ank.ssoclient.interceptor.SSOClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class SSOClientMVCConfig {
    @Autowired
    private SSOClientInterceptor ssoClientInterceptor;

    @Bean
    public WebMvcConfigurerAdapter defaultWebMvcConfig(){
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(ssoClientInterceptor);
            }
        };
    }

}
