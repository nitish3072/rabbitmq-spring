package com.nitish.rabbitmq;

import com.nitish.rabbitmq.config.RabbitMqConfig;
import com.nitish.rabbitmq.consumer.AbstractQueueConsumer;
import com.nitish.rabbitmq.producer.QueueSender;
import com.rabbitmq.client.AMQP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Consumer.class, RabbitMqConfig.class, QueueSender.class })
@TestPropertySource(locations = {"classpath:application.properties"})
@Testcontainers
public class IntegrationTest {

	static String exchangeName = "exchange-1";

	static String queueName = "queue-1";

	static String routingKey = "key-1";

	@Container
	public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(RabbitMQTestImages.RABBITMQ_IMAGE)
			.withVhost("/", true)
			.withUser("guest", "guest", ImmutableSet.of("administrator"))
			.withPluginsEnabled("rabbitmq_management")
			.withPermission("/", "guest", ".*", ".*", ".*")
			.withQueue(queueName, false, true, Collections.emptyMap())
			.withExchange(exchangeName, ExchangeTypes.DIRECT.toLowerCase(Locale.ROOT), false, false, true, Collections.emptyMap())
			.withBinding(exchangeName, queueName, Collections.emptyMap(), routingKey, Binding.DestinationType.QUEUE.name());

	@PostConstruct
	public void postConstruct() {
		rabbitMQContainer.start();
	}

	@PreDestroy
	public void preDestroy() {
		rabbitMQContainer.stop();
	}

	@DynamicPropertySource
	static void rabbitMqProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
		registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
		registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
		registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
	}

//
//	public static class Initializer implements
//			ApplicationContextInitializer<ConfigurableApplicationContext> {
//		@Override
//		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//			TestPropertyValues values = TestPropertyValues.of(
//					"spring.rabbitmq.username=" + "guest",
//					"spring.rabbitmq.password=" + "guest",
//					"spring.rabbitmq.host=" + rabbitMQContainer.getHost(),
//					"spring.rabbitmq.port=" + rabbitMQContainer.getMappedPort(5672)
//			);
//			values.applyTo(configurableApplicationContext);
//		}
//	}

	@Autowired
	Consumer consumer;

	@Autowired
	QueueSender queueSender;

	@Test
	public void checkExchangeAndQueueCreated() throws InterruptedException, IOException {
		assertThat(rabbitMQContainer.execInContainer("rabbitmqctl", "list_exchanges").getStdout())
				.containsPattern(exchangeName + "\\s+direct");
		assertThat(rabbitMQContainer.execInContainer("rabbitmqctl", "list_queues", "name", "arguments").getStdout())
				.containsPattern(queueName);
	}

	@Test
	public void checkSenderAndConsumer() throws InterruptedException, IOException {
		String message = "Check Message";
		queueSender.send(message);
		boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
		assertTrue(messageConsumed);
		assertEquals(consumer.getPayload(), message);
	}

}
