package in.skippr.autoconfigure;

import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.ai.model.chat.memory.redis.autoconfigure.RedisChatMemoryAutoConfiguration;
import org.springframework.ai.model.chat.memory.redis.autoconfigure.RedisChatMemoryProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.RedisClient;

@AutoConfiguration(before = RedisChatMemoryAutoConfiguration.class)
@ConditionalOnClass({ RedisChatMemoryRepository.class, RedisClient.class })
@EnableConfigurationProperties({RedisChatMemoryProperties.class, DataRedisProperties.class})
public class RedisClientAutoConfigurer {

    @Bean
    @Primary
    public RedisClient jedisClient(RedisChatMemoryProperties properties, DataRedisProperties redisProperties) {
        return RedisClient.builder()
                .hostAndPort(properties.getHost(), properties.getPort())
                .clientConfig(DefaultJedisClientConfig.builder()
                        .user(redisProperties.getUsername())
                        .password(redisProperties.getPassword())
                        .clientName(redisProperties.getClientName())
                        .build())
               .build();
    }
}
