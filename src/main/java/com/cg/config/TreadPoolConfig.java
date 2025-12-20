package com.cg.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
@Slf4j
@Configuration
public class TreadPoolConfig {
    // 线程池配置
@Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        // 设置最大线程数
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);
        // 设置队列容量
        executor.setQueueCapacity(100000);
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            // 当线程池中的线程数量大于corePoolSize时，如果还有任务进来，它就直接丢给拒绝策略
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                executor.execute(r);
                log.info("rejectedExecution");
            }
        });

        executor.initialize();
        return executor;
    }

}