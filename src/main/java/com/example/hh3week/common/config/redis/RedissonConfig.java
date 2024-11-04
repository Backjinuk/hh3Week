package com.example.hh3week.common.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.data.redis.password}")
	private String redisPassword; // 비밀번호가 없는 경우 빈 문자열로 설정

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() {
		Config config = new Config();
		String address = String.format("redis://%s:%d", redisHost, redisPort);
		config.useSingleServer().setAddress(address);

		if (redisPassword != null && !redisPassword.isEmpty()) {
			config.useSingleServer().setPassword(redisPassword);
		}

		return Redisson.create(config);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// JSON 직렬화기 설정
		Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
			ObjectMapper.DefaultTyping.NON_FINAL);
		jacksonSerializer.setObjectMapper(objectMapper);

		// 키와 값에 대한 직렬화기 설정
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(jacksonSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(jacksonSerializer);

		template.afterPropertiesSet();

		// Redis 연결 시도 로그
		try {
			template.getConnectionFactory().getConnection().ping();
			log.info("Redis 연결 성공!");
		} catch (Exception e) {
			log.error("Redis 연결 실패: {}", e.getMessage());
		}

		return template;
	}

}
