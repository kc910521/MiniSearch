package com.duoku.common.mini.spring;

import com.duoku.common.mini.cluster.redis.MSRedisMessageListener;
import com.duoku.common.mini.cluster.redis.MSRedisMessageListenerContainer;
import com.duoku.common.mini.cluster.redis.RedisIndexCoordinateSender;
import com.duoku.common.mini.config.MiniSearchConfigure;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author caikun
 * @Description
 * 接入默认集群配置的spring配置类
 * 高优先级
 *
 * @Date 下午6:53 20-4-26
 **/
@Configuration
@ComponentScan("com.duoku.common.mini")
public class DefaultMiniSearchSpringConfig implements BeanPostProcessor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisIndexCoordinateSender redisIndexCoordinateSender;

    @Autowired
    private MSRedisMessageListenerContainer msRedisMessageListenerContainer;

    @Autowired
    private MSRedisMessageListener msRedisMessageListener;

    @Autowired
    private MiniSearchSpringUtil miniSearchSpringUtil;

//    @Bean(name = "miniSearchSpringUtil")
////    @DependsOn("msRedisMessageListener")
//    public MiniSearchSpringUtil miniSearchSpringUtil(ApplicationContext applicationContext) {
//        MiniSearchSpringUtil miniSearchSpringUtil = new MiniSearchSpringUtil();
//        miniSearchSpringUtil.setApplicationContext(applicationContext);
//        return miniSearchSpringUtil;
//    }

//    @Bean(name = "msRedisMessageListener")
//    @DependsOn("msRedisMessageListenerContainer")
//    public MSRedisMessageListener msRedisMessageListener() {
//        MSRedisMessageListener msRedisMessageListener = new MSRedisMessageListener();
//        msRedisMessageListener.setRedisTemplate(redisTemplate);
//        return msRedisMessageListener;
//    }

    @Bean("miniSearchConfigure")
    public MiniSearchConfigure miniSearchConfigure() {
        return new MiniSearchConfigure();
    }

//    @Bean(name = "msRedisMessageListenerContainer")
//    @DependsOn("redisIndexCoordinateSender")
//    public MSRedisMessageListenerContainer msRedisMessageListenerContainer(MSRedisMessageListener msRedisMessageListener
//            , MiniSearchConfigure miniSearchConfigure, JedisConnectionFactory jedisConnectionFactory) {
//        MSRedisMessageListenerContainer msRedisMessageListenerContainer = new MSRedisMessageListenerContainer();
//        msRedisMessageListenerContainer.setMiniSearchConfigure(miniSearchConfigure);
//        msRedisMessageListenerContainer.setMsRedisMessageListener(msRedisMessageListener);
//        msRedisMessageListenerContainer.setConnectionFactory(jedisConnectionFactory);
//        return msRedisMessageListenerContainer;
//    }

//    @Bean("redisIndexCoordinateSender")
//    public RedisIndexCoordinateSender redisIndexCoordinateSender(MiniSearchConfigure miniSearchConfigure) {
//        RedisIndexCoordinateSender redisIndexCoordinateSender = new RedisIndexCoordinateSender();
//        redisIndexCoordinateSender.setMiniSearchConfigure(miniSearchConfigure);
//        redisIndexCoordinateSender.setRedisTemplate(redisTemplate);
//        return redisIndexCoordinateSender;
//    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
