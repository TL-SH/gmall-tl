package com.atguigu.gmall.index.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * @author tanglei
 */
@Configuration
public class GmallJedisConfig {

    @Bean
    public JedisPool jedisPool(){
        return new JedisPool("121.199.40.167", 6379);
    }

}
