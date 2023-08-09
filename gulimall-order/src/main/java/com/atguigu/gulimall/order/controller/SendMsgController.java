package com.atguigu.gulimall.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingKey";

    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public String sendMsg(@PathVariable String message, @PathVariable Integer delayTime){
        rabbitTemplate.convertAndSend(DELAYED_EXCHANGE_NAME, DELAYED_ROUTING_KEY, message, messagePostProcessor ->{
            messagePostProcessor.getMessageProperties().setDelay(delayTime);
            return messagePostProcessor;
        });
        log.info("当前时间：{}，发送一条延迟{}毫秒的信息给队列delay.queue:{}", new Date(), delayTime, message);
        return "发送成功";
    }
}


