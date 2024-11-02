package com.example.hh3week.common.config.Kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.hh3week.adapter.out.messaging.kafka.dto.SeatReservationRequest;
import com.example.hh3week.adapter.out.messaging.kafka.dto.SeatReservationResponse;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	// ProducerFactory for SeatReservationRequest
	@Bean
	public ProducerFactory<String, SeatReservationRequest> requestProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(
			ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
			bootstrapServers);
		configProps.put(
			ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			StringSerializer.class);
		configProps.put(
			ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	// KafkaTemplate for SeatReservationRequest
	@Bean
	public KafkaTemplate<String, SeatReservationRequest> requestKafkaTemplate() {
		return new KafkaTemplate<>(requestProducerFactory());
	}

	// ProducerFactory for SeatReservationResponse
	@Bean
	public ProducerFactory<String, SeatReservationResponse> responseProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(
			ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
			bootstrapServers);
		configProps.put(
			ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			StringSerializer.class);
		configProps.put(
			ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	// KafkaTemplate for SeatReservationResponse
	@Bean
	public KafkaTemplate<String, SeatReservationResponse> responseKafkaTemplate() {
		return new KafkaTemplate<>(responseProducerFactory());
	}

	// ConsumerFactory for SeatReservationResponse
	@Bean
	public ConsumerFactory<String, SeatReservationResponse> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(
			ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
			bootstrapServers);
		props.put(
			ConsumerConfig.GROUP_ID_CONFIG,
			"reservation-response-group");
		props.put(
			ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
			StringDeserializer.class);
		props.put(
			ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
			JsonDeserializer.class);
		props.put(
			JsonDeserializer.TRUSTED_PACKAGES,
			"*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new JsonDeserializer<>(SeatReservationResponse.class));
	}

	// Listener Container Factory for SeatReservationResponse
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SeatReservationResponse>
	kafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, SeatReservationResponse> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(5);  // 동시성 설정
		return factory;
	}
}
