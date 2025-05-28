package com.cg.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.Arrays;


/**
 * 请求日志处理
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    // 切入点
    @Pointcut("execution(* com.cg.controller..*.*(..))")
    public void pc() {}

    // 前置通知
    @Before("pc()")
    public void doBefore(JoinPoint joinPoint) {
        // 记录请求开始时间
        startTime.set(System.currentTimeMillis());

        // 接收请求, 记录请求内容
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 记录请求内容
        log.info("URL(请求路径): " + request.getRequestURL().toString());
        log.info("HTTP_METHOD(请求): " + request.getMethod());
        log.info("IP(请求IP): " + request.getRemoteAddr());
        log.info("CLASS_METHOD(类方法): " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("Args(请求参数): " + Arrays.toString(joinPoint.getArgs()));
    }

    // 返回通知
    @AfterReturning(returning = "result", pointcut = "pc()")
    public void doAfterReturning(Object result) {
        // 请求返回内容
        log.info("RESPONSE(响应内容): " + result);
        log.info("SPEND TIME(响应时间): " + (System.currentTimeMillis() - startTime.get()+"ms"));

        // 用完之后移除, 避免内存泄漏
        startTime.remove();
    }

    // 异常通知
    @AfterThrowing(value = "pc()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        // 获取类名加方法名
        String name = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        // 记录异常信息
        log.info("Exception_Class_Method(异常类方法): {}, Exception_Message: {}", name, e.getMessage());
    }
}
