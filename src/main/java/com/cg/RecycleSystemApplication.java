package com.cg;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@MapperScan("com.cg.mapper")
@EnableScheduling
@Slf4j
public class RecycleSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecycleSystemApplication.class, args);
        printResourceFile();
    }
    private static void printResourceFile() {
        try {
            ClassPathResource resource = new ClassPathResource("sysBanner.txt");
            String content = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
            log.info("文件 [{}] 的内容如下：\n{}", "sysBanner.txt", content);
        } catch (IOException e) {
            log.error("读取文件时出现异常", e);
        }
    }
}
