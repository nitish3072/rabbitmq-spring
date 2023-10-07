package com.nitish.rabbitmq.producer;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueueSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String directRoutingKey = "key-1";

    private String exchangeName = "exchange-1";

    public void send(String message) {
        Message msg = new Message(message.getBytes());
        rabbitTemplate.send(exchangeName, this.directRoutingKey, msg);
    }

}
