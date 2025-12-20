//package com.cg.config.QueueConfig.rabbitMQ;
//
//import com.cg.config.QueueConfig.rabbitMQ.Constants;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Author: MIZUGI
// * Date: 2025/11/28
// * Description:
// */
//@Configuration
//public  class QueueClient{
//
//    @Bean
//    public TopicExchange topicExchange(){
//        return new TopicExchange(Constants.EXCHANGE_NAME.getValue(),true,false);
//    }
//    @Bean
//    public Queue deleteQueue(){
//        return new Queue(Constants.DELETE_QUEUE.getValue(),true);
//    }
//    @Bean
//    public Queue insertQueue(){
//        return  new Queue(Constants.INSERT_QUEUE.getValue(),true);
//    }
//    @Bean
//    public Binding deleteBinding(){
//        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(Constants.DELETE_ROUTING_KEY.getValue());
//    }
//    @Bean
//    public Binding insertBinding(){
//        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(Constants.INSERT_ROUTING_KEY.getValue());
//    }
//}
