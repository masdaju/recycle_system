package com.cg.docService.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Canal客户端配置
 */
@Configuration
public class CanalClientConfig {

    @Value("${canal.server}")
    private String canalServer;

    @Value("${canal.destination}")
    private String destination;

    @Value("${canal.username}")
    private String username;

    @Value("${canal.password}")
    private String password;

    /**
     * 创建Canal连接器
     *
     * @return CanalConnector实例
     */
    @Bean
    public CanalConnector canalConnector() {
        String[] serverAddress = canalServer.split(":");
        String host = serverAddress[0];
        int port = Integer.parseInt(serverAddress[1]);
        return CanalConnectors.newSingleConnector(
                new InetSocketAddress(host, port),
                destination,
                username,
                password
        );
    }
}