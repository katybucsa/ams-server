package ro.ubbcluj.cs.ams.assignment.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

//    @Value("${spring.redis.host}")
//    private String REDIS_HOSTNAME;
//
//    @Value("${spring.redis.port}")
//    private int REDIS_PORT;
//
//    @Bean
//    protected JedisConnectionFactory jedisConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(REDIS_HOSTNAME, REDIS_PORT);
//        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().build();
//        JedisConnectionFactory factory = new JedisConnectionFactory(configuration, jedisClientConfiguration);
//        factory.afterPropertiesSet();
//        return factory;
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
//        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        return redisTemplate;
//    }
}
