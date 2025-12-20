package com.cg.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class JsonTypeConfig {


//    @Bean
////    默认BeanName是mappingJackson2HttpMessageConverter
//    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//        return new MappingJackson2HttpMessageConverter();
//    }
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
