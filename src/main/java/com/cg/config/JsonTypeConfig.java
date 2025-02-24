package com.cg.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JsonTypeConfig {

    // 定义一个Bean，用于自定义Jackson2ObjectMapperBuilder
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {

        // 返回一个自定义Jackson2ObjectMapperBuilder的函数
        return jacksonObjectMapperBuilder -> {
            // 为Long类型的序列化器设置为ToStringSerializer
            jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            // 为Long类的序列化器设置为ToStringSerializer
            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
        };
    }

}
