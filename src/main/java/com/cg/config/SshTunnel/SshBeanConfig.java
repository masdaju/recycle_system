package com.cg.config.SshTunnel; // 请确保包名与你的其他类一致

import com.cg.config.SshTunnel.core.SshInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * 循环注册SshInfo Bean（修复AbstractApplicationContext找不到的问题）
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SshBeanConfig {

    // 注入BeanMap（读取yml配置）和ApplicationContext（用于注册Bean）
    private final BeanMap beanMap;
    private final ApplicationContext applicationContext;

    /**
     * 核心方法：循环注册每个SshInfo为独立的Spring Bean
     */
    @PostConstruct
    public void registerSshInfoBeans() {
        // 1. 校验配置是否为空
        if (beanMap == null || beanMap.getTunnels() == null || beanMap.getTunnels().isEmpty()) {
            log.warn("yml中未配置ssh.tunnels，无需注册SshInfo Bean");
            return;
        }

        // 2. 获取Spring Bean工厂（简化方式，无需AbstractApplicationContext）
        ConfigurableListableBeanFactory beanFactory = null;
        if (applicationContext instanceof ConfigurableListableBeanFactory) {
            beanFactory = (ConfigurableListableBeanFactory) applicationContext;
        } else if (applicationContext.getAutowireCapableBeanFactory() instanceof ConfigurableListableBeanFactory) {
            beanFactory = (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        }

        if (beanFactory == null) {
            log.error("无法获取Spring Bean工厂，注册SshInfo Bean失败");
            return;
        }

        // 3. 遍历配置，注册每个SshInfo Bean
        Map<String, SshInfo> tunnels = beanMap.getTunnels();
        for (Map.Entry<String, SshInfo> entry : tunnels.entrySet()) {
            String beanName = entry.getKey();
            SshInfo sshInfo = entry.getValue();

            // 避免重复注册
            if (beanFactory.containsBean(beanName)) {
                log.warn("SshInfo Bean[{}]已存在，跳过注册", beanName);
                continue;
            }

            // 注册单例Bean（核心逻辑）
            beanFactory.registerSingleton(beanName, sshInfo);
            log.info("成功注册SshInfo Bean：[{}]，SSH地址：{}:{}",
                    beanName, sshInfo.getSshHost(), sshInfo.getSshPort());
        }
    }
}