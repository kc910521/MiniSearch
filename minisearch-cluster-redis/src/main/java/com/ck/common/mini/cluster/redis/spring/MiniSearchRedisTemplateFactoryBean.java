package com.ck.common.mini.cluster.redis.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

/**
 * @Author caikun
 * @Description
 * @Date 下午4:37 21-7-28
 **/
public class MiniSearchRedisTemplateFactoryBean implements FactoryBean<RedisTemplate>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * for spring boot fast startup
     */
    private final static String defaultRedisConnectionFactoryBeanName = "miniSearchRedisConnectionFactory";

    @Override
    public RedisTemplate getObject() throws Exception {
        Map<String, RedisConnectionFactory> beans = this.applicationContext.getBeansOfType(RedisConnectionFactory.class);
        if (beans.isEmpty()) {
            throw new RuntimeException("RedisConnectionFactory is not defined in Spring !");
        }
        RedisConnectionFactory target = beans.getOrDefault(defaultRedisConnectionFactoryBeanName,
                beans.entrySet().iterator().next().getValue()
        );
        RedisTemplate redisTemplate = new StringRedisTemplate(target);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
