package com.rackspacecloud.blueflood.kafkametricsmetadataconsumer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Configuration
@Profile("development")
@EnableConfigurationProperties(KafkaConfigurationProperties.class)
public class DevelopmentConsumerProperties extends CommonConsumerProperties {
    @Override
    Map<String, Object> consumerProperties() {
        return super.consumerProperties();
    }
}
