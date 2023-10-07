package com.nitish.rabbitmq;

import org.testcontainers.utility.DockerImageName;

public interface RabbitMQTestImages {
    DockerImageName RABBITMQ_IMAGE = DockerImageName.parse("rabbitmq:3.12.6-management-alpine");
}
