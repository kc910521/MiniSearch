package com.ck.common.mini.cluster.redis;

import com.ck.common.mini.cluster.IndexCoordinatorInstancerProxy;
import com.ck.common.mini.cluster.IndexEventSender;
import com.ck.common.mini.cluster.Intent;
import com.ck.common.mini.cluster.redis.spring.MiniSearchSpringUtil;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.cluster.redis.spring.SpringRedisDefinitionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author caikun
 * @Description redis的集群协作发送者
 * @Date 下午1:38 20-4-24
 *
 * 因为这是来自核心包 IndexEventSender 的SPI实现类，所以在构造函数中，
 * 通过依赖查找的方式获取spring容器中依赖的bean
 * redisTemplate,miniSearchConfigure 都有可能找不到，
 * 找不到则会变为单机操作
 *
 *
 * @see IndexCoordinatorInstancerProxy
 *
 *
 **/
public class RedisIndexCoordinateSender implements IndexEventSender {

    private static final Logger logger = LoggerFactory.getLogger(RedisIndexCoordinateSender.class);

    @Autowired
    @Qualifier(SpringRedisDefinitionSupport.MSRedisTemplateBeanName)
    private RedisTemplate redisTemplate;

    @Autowired
    private MiniSearchConfigure miniSearchConfigure;

    public RedisIndexCoordinateSender() {
        redisTemplate = MiniSearchSpringUtil.getBean(SpringRedisDefinitionSupport.MSRedisTemplateBeanName, RedisTemplate.class);
        miniSearchConfigure = MiniSearchSpringUtil.getBean(MiniSearchConfigure.class);
        init1();
    }

    public void init1() {
        if (redisTemplate == null) {
            logger.error("WARN: redisTemplate is null.");
        }
        if (this.miniSearchConfigure == null) {
            logger.error("WARN: miniSearchConfigure null, default");
            miniSearchConfigure = new MiniSearchConfigure();
        }
        logger.debug("RedisIndexCoordinateSender loaded.");
    }

    @Override
    public void publish(EventType eventType, String instancerName, String key, Object carrier) throws Exception {
        Intent intent = new Intent();
        intent.setCarrier(carrier);
        intent.setAction(eventType.name());
        intent.setIndexName(instancerName);
        intent.setKey(key);
        logger.debug("send to " + miniSearchConfigure.getNotifyPatternChars() + instancerName);
        if (redisTemplate == null) {
            throw new RuntimeException("redisTemplate may not involved, all operations will be standalone");
        }
        redisTemplate.convertAndSend(miniSearchConfigure.getNotifyPatternChars() + instancerName, intent);
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setMiniSearchConfigure(MiniSearchConfigure miniSearchConfigure) {
        this.miniSearchConfigure = miniSearchConfigure;
    }
}
