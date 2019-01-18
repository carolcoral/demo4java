package site.cnkj.util.config;

import lombok.Data;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap.servers}")
    private String hosts;

    @Value("${spring.kafka.producer.key.serializer}")
    private String key = "org.apache.kafka.common.serialization.StringSerializer";

    @Value("${spring.kafka.producer.value.serializer}")
    private String value = "org.apache.kafka.common.serialization.StringSerializer";

    @Value("${spring.kafka.producer.acks}")
    private String acks = "all";

    @Value("${spring.kafka.producer.retries}")
    private String retries = "0";

    @Value("${spring.kafka.producer.buffer.memory}")
    private String bufferMemory = "3000000";

    @Value("${spring.kafka.producer.max.block.ms}")
    private String maxBlockMs = "6000";

    @Value("${spring.kafka.producer.compression.type}")
    private String compressionType = "gzip";

    @Value("${spring.kafka.producer.batch.size}")
    private String batchSize = "89590";

    @Value("${spring.kafka.producer.client.id}")
    private String clientId = "";

    @Value("${spring.kafka.connections.max.idle.ms}")
    private String maxConnectionsIdleMs = "540000";

    @Value("${spring.kafka.max.request.size}")
    private String maxRequestSize = "206407532";

    @Value("${spring.kafka.topic.partitions}")
    private String topicPartitions = "10";

    // ----------------producer---------------
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.parseLong(maxBlockMs));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,Long.parseLong(bufferMemory));
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,compressionType);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,Integer.parseInt(batchSize));
        props.put(ProducerConfig.CLIENT_ID_CONFIG,clientId);
        props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, Long.parseLong(maxConnectionsIdleMs));
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, Integer.parseInt(maxRequestSize));
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        return new KafkaAdmin(configs);
    }
}
