package io.redis.jedis.jedisdemo.config;

import io.redis.jedis.jedisdemo.model.Programmer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SpringConf {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.jedis.pool.max-total}")
    private int maxTotal;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Bean
    public JedisClientConfiguration getJedisClientConfiguration() {

       JedisClientConfiguration.JedisPoolingClientConfigurationBuilder JedisPoolingClientConfigurationBuilder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();

       GenericObjectPoolConfig GenericObjectPoolConfig = new GenericObjectPoolConfig();

       GenericObjectPoolConfig.setMaxTotal(maxTotal);
       GenericObjectPoolConfig.setMaxIdle(maxIdle);
       GenericObjectPoolConfig.setMinIdle(minIdle);

       return JedisPoolingClientConfigurationBuilder.poolConfig(GenericObjectPoolConfig).build();

    }

    @Bean
    public JedisConnectionFactory getJedisConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);

        if(!StringUtils.isEmpty(password)) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }
        redisStandaloneConfiguration.setPort(port);
        return new JedisConnectionFactory(redisStandaloneConfiguration, getJedisClientConfiguration());
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(getJedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    @Qualifier("listOperations")
    public ListOperations<String, Programmer> listOperations(RedisTemplate<String, Programmer> redisTemplate) {
        return redisTemplate.opsForList();
    }

}