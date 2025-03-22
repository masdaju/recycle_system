package com.cg;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@MapperScan("com.cg.mapper")
@Slf4j
public class RecycleSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecycleSystemApplication.class, args);
        printResourceFile();
    }
    private static void printResourceFile() {
        try {
            // 从 classpath 中加载文件
            ClassPathResource resource = new ClassPathResource("sysbanner.txt");
            // 读取文件内容
            String content = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
            // 打印日志
            log.info("文件 [{}] 的内容如下：\n{}", "sysbanner.txt", content);
        } catch (IOException e) {
            log.error("读取文件时出现异常", e);
        }
    }
}
