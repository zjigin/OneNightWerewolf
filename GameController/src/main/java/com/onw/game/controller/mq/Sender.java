package com.onw.game.controller.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${mq.exchange_name}")
    private String topicExchangeName;

    @Autowired
    MyMessagePostProcessor myMessagePostProcessor;

    public void send(String key, String message) {
        rabbitTemplate.convertAndSend(topicExchangeName, key, message, myMessagePostProcessor);
    }
}
