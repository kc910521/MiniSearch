package com.ck.common.mini.config;

import com.ck.common.mini.cluster.redis.MSRedisMessageListener;
import com.ck.common.mini.cluster.redis.MSRedisMessageListenerContainer;
import com.ck.common.mini.cluster.redis.spring.MiniSearchSpringUtil;
import com.ck.common.mini.cluster.redis.spring.SpringRedisDefinitionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author caikun
 * @Description
 * 接入默认集群配置的spring配置类
 * 高优先级
 *
 * @Date 下午6:53 20-4-26
 **/
@Configuration
@ComponentScan("com.ck.common.mini.cluster.redis")
@Import(SpringRedisDefinitionSupport.class)
public class DefaultMiniSearchSpringRedisConfig {

//    @Autowired
//    private MSRedisMessageListenerContainer msRedisMessageListenerContainer;
//
//    @Autowired
//    private MSRedisMessageListener msRedisMessageListener;
//
//    @Autowired
//    private MiniSearchSpringUtil miniSearchSpringUtil;


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

}
