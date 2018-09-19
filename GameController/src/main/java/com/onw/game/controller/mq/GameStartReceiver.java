package com.onw.game.controller.mq;

import com.onw.game.controller.core.GameController;
import com.onw.game.controller.core.GameSetting;
import com.onw.game.controller.shared.GameControllerUtil;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class GameStartReceiver {

    @Autowired
    GameController gameController;

    @SuppressWarnings("unused")
    void receiveMessage(byte[] bytes) {
        try {
            receiveMessage(new String(bytes, StandardCharsets.UTF_8));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("WeakerAccess")
    void receiveMessage(String message) {
        System.out.println("Receive " + message);
        GameSetting gameSetting = GameControllerUtil.getGson().fromJson(message, GameSetting.class);
        gameController.startGame(gameSetting);
    }

    @Value("${mq.exchange_name}")
    private String topicExchangeName;

    @Value("${mq.start_queue_name}")
    private String queueName;

    @Value("${mq.start_routing_key}")
    private String routingKey;

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(GameStartReceiver gameStartReceiver) {
        return new MessageListenerAdapter(gameStartReceiver, "receiveMessage");
    }

}
