package com.duoku.common.mini.spring;

import com.duoku.common.mini.cluster.redis.MSRedisMessageListener;
import com.duoku.common.mini.cluster.redis.MSRedisMessageListenerContainer;
import com.duoku.common.mini.cluster.redis.RedisIndexCoordinateSender;
import com.duoku.common.mini.config.MiniSearchConfigure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author caikun
 * @Description 接入默认集群配置的spring配置类
 * @Date 下午6:53 20-4-26
 **/
@Configuration
public class DefaultMiniSearchSpringConfig {

    @Autowired
    private RedisTemplate redisTemplate;


    @Bean(name = "miniSearchSpringUtil")
    public MiniSearchSpringUtil miniSearchSpringUtil(ApplicationContext applicationContext) {
        MiniSearchSpringUtil miniSearchSpringUtil = new MiniSearchSpringUtil();
        miniSearchSpringUtil.setApplicationContext(applicationContext);
        return miniSearchSpringUtil;
    }

    @Bean
    public MiniSearchConfigure miniSearchConfigure() {
        return new MiniSearchConfigure();
    }

    @Bean(name = "msRedisMessageListener")
    public MSRedisMessageListener msRedisMessageListener(RedisTemplate redisTemplate) {
        MSRedisMessageListener msRedisMessageListener = new MSRedisMessageListener();
        msRedisMessageListener.setRedisTemplate(redisTemplate);
        return msRedisMessageListener;
    }

    @Bean(name = "msRedisMessageListenerContainer")
    public MSRedisMessageListenerContainer msRedisMessageListenerContainer(MSRedisMessageListener msRedisMessageListener
            , MiniSearchConfigure miniSearchConfigure, JedisConnectionFactory jedisConnectionFactory) {
        MSRedisMessageListenerContainer msRedisMessageListenerContainer = new MSRedisMessageListenerContainer();
        msRedisMessageListenerContainer.setMiniSearchConfigure(miniSearchConfigure);
        msRedisMessageListenerContainer.setMsRedisMessageListener(msRedisMessageListener);
        msRedisMessageListenerContainer.setConnectionFactory(jedisConnectionFactory);

        return msRedisMessageListenerContainer;
    }

    @Bean
    public RedisIndexCoordinateSender redisIndexCoordinateSender(RedisTemplate redisTemplate) {
        RedisIndexCoordinateSender redisIndexCoordinateSender = new RedisIndexCoordinateSender();
        redisIndexCoordinateSender.setRedisTemplate(redisTemplate);
        return redisIndexCoordinateSender;
    }
}
