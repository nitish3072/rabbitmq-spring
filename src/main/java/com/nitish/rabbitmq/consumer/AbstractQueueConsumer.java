package com.nitish.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;

public abstract class AbstractQueueConsumer {

    protected abstract void sendMessageForProcessing(String message);

    @RabbitListener(queues = {"queue-1"})
    public void receive(@Payload String message) {
        System.out.println("Message: " + message);
        sendMessageForProcessing(message);
    }

}