package com.example.hh3week.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://backjin.iptime.org:6379");
		// .setPassword("your_redis_password"); // 필요 시 비밀번호 설정
		return Redisson.create(config);
	}
}
