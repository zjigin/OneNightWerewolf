package com.onw.game.current.role.controller.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mq.exchange_name}")
    private String topicExchangeName;

    private final MyMessagePostProcessor myMessagePostProcessor;

    @Autowired
    public Sender(RabbitTemplate rabbitTemplate, MyMessagePostProcessor myMessagePostProcessor) {
        this.rabbitTemplate = rabbitTemplate;
        this.myMessagePostProcessor = myMessagePostProcessor;
    }

    public void send(String key, String message) {
        rabbitTemplate.convertAndSend(topicExchangeName, key, message, myMessagePostProcessor);
    }

}
