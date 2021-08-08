package com.genericnpc.kafkafinorp;

import com.example.Sensor;
import com.genericnpc.kafkafinorp.customavro.CustomKafkaAvroDeserializer;
import com.genericnpc.kafkafinorp.customavro.CustomKafkaAvroSerializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class KafkaFinOrpApplicationTests {
    private static final String INPUT_TOPIC = "sensor-topic";
    private static final String OUTPUT_TOPIC = "sensor-topic-out";
    private static final String GROUP_NAME = "embeddedKafkaApplication";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, OUTPUT_TOPIC)
            .brokerProperty("offsets.topic.replication.factor", (short)1)
            .brokerProperty("transaction.state.log.replication.factor", (short)1)
            .brokerProperty("transaction.state.log.min.isr", 1);

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.cloud.stream.kafka.binder.brokers", embeddedKafka.getEmbeddedKafka().getBrokersAsString());
    }

    @Test
    public void testSendReceive() {
        EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafka.getEmbeddedKafka();
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        senderProps.put("key.serializer", ByteArraySerializer.class);
        senderProps.put("value.serializer", CustomKafkaAvroSerializer.class);
        senderProps.put("schema.registry.url", "nolongerused.com");

        DefaultKafkaProducerFactory<byte[], Sensor> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<byte[], Sensor> template = new KafkaTemplate<byte[], Sensor>(pf, true);
        template.setDefaultTopic(INPUT_TOPIC);
        template.sendDefault(randomSensor());

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put("key.deserializer", ByteArrayDeserializer.class);
        consumerProps.put("value.deserializer", CustomKafkaAvroDeserializer.class);
        consumerProps.put("schema.registry.url", "nolongerused.com");
        DefaultKafkaConsumerFactory<byte[], Sensor> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<byte[], Sensor> consumer = cf.createConsumer();
        consumer.subscribe(Collections.singleton(OUTPUT_TOPIC));
        ConsumerRecords<byte[], Sensor> records = consumer.poll(Duration.ofSeconds(30, 0));
        consumer.commitSync();


        var a = records;
    }

    private Sensor randomSensor() {
        Random random = new Random();
        Sensor sensor = new Sensor();
        sensor.setId(random.nextInt(100000));
        sensor.setAcceleration(random.nextFloat() * 10);
        sensor.setVelocity(random.nextFloat() * 100);
        sensor.setTemperature(random.nextFloat() * 50);
        return sensor;
    }

}
