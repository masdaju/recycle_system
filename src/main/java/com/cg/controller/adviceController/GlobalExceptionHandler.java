package com.cg.controller.adviceController;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.http.HttpException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
//登录异常
    @ExceptionHandler(NotLoginException.class)
    public SaResult handleNotLoginException(NotLoginException e, HttpServletResponse response) {
        e.printStackTrace();
        //把没有登录异常的请求的设置响应状态码为401，前端根据这个状态码判断是否需要跳转到登录页面
        response.setStatus(401);
        return SaResult.error("请先登录"+e.getMessage());
    }
    //Http异常
    @ExceptionHandler(HttpException.class)
    public SaResult handleGeneralException(HttpException e) {
        e.printStackTrace();
        return SaResult.error("发生未知异常: " + e.getMessage());
    }
}