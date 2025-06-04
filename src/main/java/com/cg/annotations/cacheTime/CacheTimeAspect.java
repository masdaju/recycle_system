package com.cg.annotations.cacheTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class CacheTimeAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.cg.annotations.cacheTime.CacheTime) || @within(com.cg.annotations.cacheTime.CacheTime)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的@CacheTime注解
        CacheTime cacheTime = method.getAnnotation(CacheTime.class);
        if (cacheTime == null) {
            // 获取类上的@CacheTime注解
            cacheTime = method.getDeclaringClass().getAnnotation(CacheTime.class);
        }

        Object result = joinPoint.proceed();

        // 如果方法有@Cacheable注解，且返回值不为空，则设置过期时间
        if (method.isAnnotationPresent(Cacheable.class) && result != null) {
            Cacheable cacheable = method.getAnnotation(Cacheable.class);
            String[] cacheNames = cacheable.value();
            String key = generateKey(joinPoint);

            for (String cacheName : cacheNames) {
                String cacheKey = cacheName + "::" + key;
                redisTemplate.expire(cacheKey, cacheTime.value(), cacheTime.unit());
            }
        }

        return result;
    }
    //
    private String generateKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return methodName;
        }

        return methodName + "(" + String.join(",", Arrays.toString(args)) + ")";
    }
}