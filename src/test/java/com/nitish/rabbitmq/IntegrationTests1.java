package com.nitish.rabbitmq;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration()
@Testcontainers
public class IntegrationTests1 {

    public static final int DEFAULT_AMQPS_PORT = 5671;

    public static final int DEFAULT_AMQP_PORT = 5672;

    public static final int DEFAULT_HTTPS_PORT = 15671;

    public static final int DEFAULT_HTTP_PORT = 15672;

    @Test
    public void shouldCreateRabbitMQContainer() {
        try (RabbitMQContainer container = new RabbitMQContainer(RabbitMQTestImages.RABBITMQ_IMAGE)) {
            assertThat(container.getAdminPassword()).isEqualTo("guest");
            assertThat(container.getAdminUsername()).isEqualTo("guest");

            container.start();

            assertThat(container.getAmqpsUrl())
                    .isEqualTo(
                            String.format("amqps://%s:%d", container.getHost(), container.getMappedPort(DEFAULT_AMQPS_PORT))
                    );
            assertThat(container.getAmqpUrl())
                    .isEqualTo(
                            String.format("amqp://%s:%d", container.getHost(), container.getMappedPort(DEFAULT_AMQP_PORT))
                    );
            assertThat(container.getHttpsUrl())
                    .isEqualTo(
                            String.format("https://%s:%d", container.getHost(), container.getMappedPort(DEFAULT_HTTPS_PORT))
                    );
            assertThat(container.getHttpUrl())
                    .isEqualTo(
                            String.format("http://%s:%d", container.getHost(), container.getMappedPort(DEFAULT_HTTP_PORT))
                    );

            assertThat(container.getHttpsPort()).isEqualTo(container.getMappedPort(DEFAULT_HTTPS_PORT));
            assertThat(container.getHttpPort()).isEqualTo(container.getMappedPort(DEFAULT_HTTP_PORT));
            assertThat(container.getAmqpsPort()).isEqualTo(container.getMappedPort(DEFAULT_AMQPS_PORT));
            assertThat(container.getAmqpPort()).isEqualTo(container.getMappedPort(DEFAULT_AMQP_PORT));

            assertThat(container.getLivenessCheckPortNumbers())
                    .containsExactlyInAnyOrder(
                            container.getMappedPort(DEFAULT_AMQP_PORT),
                            container.getMappedPort(DEFAULT_AMQPS_PORT),
                            container.getMappedPort(DEFAULT_HTTP_PORT),
                            container.getMappedPort(DEFAULT_HTTPS_PORT)
                    );
        }
    }

    @Test
    public void shouldCreateRabbitMQContainerWithExchange() throws IOException, InterruptedException {
        try (RabbitMQContainer container = new RabbitMQContainer(RabbitMQTestImages.RABBITMQ_IMAGE)) {
            container.withExchange("test-exchange", "direct");

            container.start();

            assertThat(container.execInContainer("rabbitmqctl", "list_exchanges").getStdout())
                    .containsPattern("test-exchange\\s+direct");
        }
    }

    @Test
    public void shouldCreateRabbitMQContainerWithExchangeInVhost() throws IOException, InterruptedException {
        try (RabbitMQContainer container = new RabbitMQContainer(RabbitMQTestImages.RABBITMQ_IMAGE)) {
            container.withVhost("test-vhost");
            container.withExchange(
                    "test-vhost",
                    "test-exchange",
                    "direct",
                    false,
                    false,
                    false,
                    Collections.emptyMap()
            );

            container.start();

            assertThat(container.execInContainer("rabbitmqctl", "list_exchanges", "-p", "test-vhost").getStdout())
                    .containsPattern("test-exchange\\s+direct");
        }
    }

    @Test
    public void shouldCreateRabbitMQContainerWithQueues() throws IOException, InterruptedException {
        try (RabbitMQContainer container = new RabbitMQContainer(RabbitMQTestImages.RABBITMQ_IMAGE)) {
            container
                    .withQueue("queue-one")
                    .withQueue("queue-two", false, true, ImmutableMap.of("x-message-ttl", 1000));

            container.start();

            assertThat(container.execInContainer("rabbitmqctl", "list_queues", "name", "arguments").getStdout())
                    .containsPattern("queue-one");
            assertThat(container.execInContainer("rabbitmqctl", "list_queues", "name", "arguments").getStdout())
                    .containsPattern("queue-two\\s.*x-message-ttl");
        }
    }

}