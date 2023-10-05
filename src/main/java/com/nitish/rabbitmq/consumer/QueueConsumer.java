package com.nitish.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;

public abstract class QueueConsumer {

    protected abstract void sendMessageForProcessing(String fileBody);

    @RabbitListener(queues = {"${queue.name}"})
    public void receive(@Payload String fileBody) {
        System.out.println("Message: " + fileBody);
        sendMessageForProcessing(fileBody);
    }

}