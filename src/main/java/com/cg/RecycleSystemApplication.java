package com.cg;

import com.cg.docService.canal.CanalClientService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.cg.mapper")
@EnableScheduling
@Slf4j
public class RecycleSystemApplication implements CommandLineRunner {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(RecycleSystemApplication.class, args);
//        Map<String, SshInfo> sshInfoMap = context.getBeansOfType(SshInfo.class);
//        sshInfoMap.forEach((k, v) -> System.out.println(v));
//        printResourceFile();
    }


//    private static void printResourceFile() {
//        try {
//            ClassPathResource resource = new ClassPathResource("sysBanner.txt");
//            String content = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
//            log.info("文件 [{}] 的内容如下：\n{}", "sysBanner.txt", content);
//        } catch (IOException e) {
//            log.error("读取文件时出现异常", e);
//        }
//    }
    @Autowired
    CanalClientService canalClientService;
    @Override
    public void run(String... args) {
        canalClientService.start();
    }
}
