package com.genericnpc.kafkafinorp.config;

import com.example.Sensor;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.RecoveringBatchErrorHandler;
import org.springframework.kafka.listener.RetryingBatchErrorHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {


    @Value("${spring.cloud.stream.kafka.bindings.supplier-out-0.producer.configuration.value.serializer}")
    public Class<KafkaAvroSerializer> valueSerializer;

    @Value("${spring.cloud.stream.kafka.binder.brokers}")
    public String broker;

    @Bean
    public ProducerFactory<?, ?> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    public ListenerContainerCustomizer<AbstractMessageListenerContainer<?, ?>> customizer(){
        return (container, destination, group) -> {
            container.setBatchErrorHandler(new RecoveringBatchErrorHandler());
        };
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "batch-tx-");
        props.put("schema.registry.url", "http://localhost:8081");
        props.put("specific.avro.reader", "true");
        props.put("useNativeEncoding", "true");


        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }

    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
