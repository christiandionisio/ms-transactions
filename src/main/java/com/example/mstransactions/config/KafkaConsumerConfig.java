package com.example.mstransactions.config;

import com.example.mstransactions.data.dto.PaymentDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * KafkaConsumerConfig.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Configuration
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  /**
   * consumerFactory configuration.
   *
   * @author Christian Dionisio
   * @version 1.0
   */
  @Bean
  public ConsumerFactory<String, PaymentDto> consumerFactory() {
    JsonDeserializer<PaymentDto> jsonDeserializer = new JsonDeserializer<>(PaymentDto.class, false);
    jsonDeserializer.addTrustedPackages("*");
    return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
        new StringDeserializer(), jsonDeserializer);
  }

  /**
   * kafkaListenerContainerFactory configuration.
   *
   * @author Christian Dionisio
   * @version 1.0
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PaymentDto>> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, PaymentDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());

    return factory;
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-group");
    return props;
  }

}
