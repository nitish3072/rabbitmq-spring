package com.nitish.rabbitmq;

import com.nitish.rabbitmq.consumer.AbstractQueueConsumer;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
@Data
public class Consumer extends AbstractQueueConsumer {

    private String payload;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void sendMessageForProcessing(String message) {
        payload = message;
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

}
