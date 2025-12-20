package com.cg.config.SshTunnel.core;

import com.jcraft.jsch.JSchException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("sshBeanConfig")
public class SshTunnelInitializer {
    private final ApplicationContext applicationContext;
    private final SshPortForwardManager sshPortForwardManager;

    /**
     * Spring上下文初始化完成后，自动执行：初始化所有SSH连接
     */
    @PostConstruct
    public void initAllSshConnections() {
        Map<String, SshInfo> sshInfoMap = applicationContext.getBeansOfType(SshInfo.class);
        if (sshInfoMap.isEmpty()) {
            log.warn("未找到任何SshInfo类型的Bean，跳过SSH连接初始化");
            return;
        }

        // 2. 遍历所有SshInfo Bean，初始化连接
        for (Map.Entry<String, SshInfo> entry : sshInfoMap.entrySet()) {
            String beanName = entry.getKey();
            SshInfo sshInfo = entry.getValue();
            try {
                log.info("开始初始化SSH Bean[{}]：{}:{}", beanName, sshInfo.getSshHost(), sshInfo.getSshPort());
                // 核心：调用连接方法
                sshPortForwardManager.initSshConnection(sshInfo);
            } catch (JSchException e) {
                log.error("初始化SSH Bean[{}]失败", beanName, e);
            }
        }
    }

    /**
     * 应用关闭时，自动关闭所有SSH连接
     */
    @PreDestroy
    public void closeAllSshConnections() {
        Map<String, SshInfo> sshInfoMap = applicationContext.getBeansOfType(SshInfo.class);
        for (SshInfo sshInfo : sshInfoMap.values()) {
            sshPortForwardManager.closeSshConnection(sshInfo);
        }
    }
}