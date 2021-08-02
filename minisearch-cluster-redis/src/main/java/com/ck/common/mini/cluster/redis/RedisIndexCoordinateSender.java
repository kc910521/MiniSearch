package com.ck.common.mini.cluster.redis;

import com.ck.common.mini.cluster.IndexCoordinatorIndexInstanceProxy;
import com.ck.common.mini.cluster.IndexEventSender;
import com.ck.common.mini.cluster.Intent;
import com.ck.common.mini.config.DefaultMiniSearchSpringRedisConfig;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.cluster.redis.spring.SpringRedisDefinitionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

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
 * @see IndexCoordinatorIndexInstanceProxy
 *
 *
 **/
@DependsOn({SpringRedisDefinitionSupport.MSRedisTemplateBeanName, "miniSearchSpringUtil"})
public class RedisIndexCoordinateSender implements IndexEventSender, ApplicationContextAware {

    private volatile boolean init = false;

    private static final Logger logger = LoggerFactory.getLogger(RedisIndexCoordinateSender.class);

    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier(SpringRedisDefinitionSupport.MSRedisTemplateBeanName)
    private RedisTemplate redisTemplate;

    @Autowired
    private MiniSearchConfigure miniSearchConfigure;

    public RedisIndexCoordinateSender() {
    }

    @PostConstruct
    public void postCons() {
        redisTemplate = this.applicationContext.getBean(SpringRedisDefinitionSupport.MSRedisTemplateBeanName, RedisTemplate.class);
        miniSearchConfigure = this.applicationContext.getBean(MiniSearchConfigure.class);
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
        this.init = true;
    }

    @Override
    public void publish(EventType eventType, String instancerName, String key, Object carrier) throws Exception {
        if (!this.init) {
            throw new RuntimeException("RedisIndexCoordinateSender no init");
        }
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
