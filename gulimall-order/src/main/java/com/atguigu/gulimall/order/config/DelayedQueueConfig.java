package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayedQueueConfig {

    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingKey";

    @Bean("delayedQueue")
    public Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME);
    }

    /**
     * 自定义交换机 定义一个延迟交换机
     *  不需要死信交换机和死信队列，支持消息延迟投递，消息投递之后没有到达投递时间，是不会投递给队列
     *  而是存储在一个分布式表，当投递时间到达，才会投递到目标队列
     * @return
     */
    @Bean("delayedExchange")
    public CustomExchange delayedExchange(){
        Map<String, Object> args = new HashMap<>(1);
        // 自定义交换机的类型
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding bindingDelayedQueue(@Qualifier("delayedQueue") Queue delayedQueue,
                                       @Qualifier("delayedExchange") CustomExchange delayedExchange){
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }
}


