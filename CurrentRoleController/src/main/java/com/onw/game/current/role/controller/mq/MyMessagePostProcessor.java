package com.onw.game.current.role.controller.mq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyMessagePostProcessor implements MessagePostProcessor {

    @Value("${mq.msg.ttl}")
    private Integer ttl;


    @Override
    public Message postProcessMessage(final Message message) throws AmqpException {
        message.getMessageProperties().setExpiration(ttl.toString());
        return message;
    }
}