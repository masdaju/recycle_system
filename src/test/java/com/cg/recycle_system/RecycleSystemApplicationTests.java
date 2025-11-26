package com.cg.recycle_system;


import com.cg.config.emil.EmailSendUtil;
import com.cg.config.websocketServer.WebSocketConfig;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Map;

//
//@SpringBootTest()
//@ComponentScan(excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
//        classes = {WebSocketConfig.class})})
//class RecycleSystemApplicationTests {
//    @Autowired
//    EmailSendUtil emailSendUtil;
//    @Test
//    void contextLoads() throws MessagingException {
//        Map<String, Object> variables = Map.of("code", "123456", "expireTime", "5", "username", "张三");
//        emailSendUtil.sendHtmlMail("2951975406@qq.com","测试","email-verification",variables);
//
//    }
//@Test
//void test(){
//    System.out.println(1);
//}


//}
