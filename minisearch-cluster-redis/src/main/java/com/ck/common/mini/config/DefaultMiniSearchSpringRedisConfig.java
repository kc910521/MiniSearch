package com.ck.common.mini.config;

import com.ck.common.mini.cluster.redis.MSRedisMessageListener;
import com.ck.common.mini.cluster.redis.MSRedisMessageListenerContainer;
import com.ck.common.mini.cluster.redis.RedisIndexCoordinateSender;
import com.ck.common.mini.cluster.redis.spring.MiniSearchSpringUtil;
import com.ck.common.mini.cluster.redis.spring.SenderDependencyFiller;
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
@Import({SpringRedisDefinitionSupport.class, SenderDependencyFiller.class})
public class DefaultMiniSearchSpringRedisConfig {

}
