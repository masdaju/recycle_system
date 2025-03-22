package com.cg.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Value("${file.upload-path}")
    private String uploadPath;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
//        List<String> excludePathPatterns = List.of("/sys-user/create","/sys-user/login");
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/preview/**")
                .excludePathPatterns("/sys-user/login")
                .excludePathPatterns("/sys-user/create");

    }
    //配置静态资源映射
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("file:"+uploadPath);
        registry.addResourceHandler("/preview/**")
                .addResourceLocations("file:"+uploadPath);
    }
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }
}
