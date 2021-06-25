package com.ck.common.mini.cluster.redis;

import com.ck.common.mini.cluster.IndexEventSender;
import com.ck.common.mini.cluster.Intent;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.spring.MiniSearchSpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

/**
 * @Author caikun
 * @Description redis的集群协作发送者
 * @Date 下午1:38 20-4-24
 **/
public class RedisIndexCoordinateSender implements IndexEventSender {

    private static final Logger logger = LoggerFactory.getLogger(RedisIndexCoordinateSender.class);

    private RedisTemplate redisTemplate;

    private MiniSearchConfigure miniSearchConfigure;

    public RedisIndexCoordinateSender() {
        try {
            redisTemplate = MiniSearchSpringUtil.getBean("redisTemplate", RedisTemplate.class);
        } catch (Throwable e) {
            logger.error("redisTemplate init failed", e);
        }
        try {
            miniSearchConfigure = MiniSearchSpringUtil.getBean(MiniSearchConfigure.class);
        } catch (Throwable e) {
            logger.error("miniSearchConfigure init failed, new one generated");
            miniSearchConfigure = new MiniSearchConfigure();
        }
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
