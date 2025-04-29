package com.cg.config;

import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UndertowConfig {

    @Value("${undertow.websocket.buffer-size}")
    private int bufferSize;

    @Value("${undertow.websocket.region-buffers}")
    private int regionBuffers;

    @Value("${undertow.websocket.max-idle-buffers}")
    private int maxIdleBuffers;

    @Value("${undertow.websocket.max-direct-memory}")
    private int maxDirectMemory;
    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer() {
        return factory -> {
            factory.addDeploymentInfoCustomizers(deploymentInfo -> {
                DefaultByteBufferPool bufferPool = new DefaultByteBufferPool(
                        false,
                        bufferSize,
                        regionBuffers,
                        maxIdleBuffers,
                        maxDirectMemory
                );

                WebSocketDeploymentInfo webSocketDeploymentInfo = (WebSocketDeploymentInfo) deploymentInfo.getServletContextAttributes()
                        .computeIfAbsent(WebSocketDeploymentInfo.ATTRIBUTE_NAME, k -> new WebSocketDeploymentInfo());

                webSocketDeploymentInfo.setBuffers(bufferPool);
            });

            factory.addBuilderCustomizers(builder -> {
                builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
            });
        };
    }
}