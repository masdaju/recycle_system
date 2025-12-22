package com.cg.config.emil;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.Map;

/**
 * 通用邮件发送工具类（支持文本邮件、HTML邮件、模板邮件）
 */
@Component
@Slf4j

public class EmailSendUtil {
    @Resource
    private  JavaMailSender javaMailSender;
    @Autowired
    private  TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;  // 发件人邮箱

    public void sendHtmlMail(String toEmail, String subject, String templateName, Map<String, Object> variables)
            throws MessagingException {

        // 创建 Thymeleaf 上下文
        Context context = new Context();
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        }

        // 渲染模板
        String htmlContent = templateEngine.process(templateName, context);

        // 创建邮件对象
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setSubject(subject);
        helper.setSentDate(new Date());
        helper.setTo(toEmail);
        helper.setFrom(fromEmail);
        helper.setText(htmlContent, true);

        // 发送邮件
        javaMailSender.send(message);
    }
}