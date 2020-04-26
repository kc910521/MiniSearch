package com.duoku.common.mini.cluster.redis;

import com.duoku.common.mini.cluster.Intent;
import com.duoku.common.mini.config.MiniSearchConfigure;
import com.duoku.common.mini.constant.EventType;
import com.duoku.common.mini.cluster.IndexEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author caikun
 * @Description redis的集群协作发送者
 * @Date 下午1:38 20-4-24
 **/
@Component
public class RedisIndexCoordinateSender implements IndexEventSender {

    private static final Logger logger = LoggerFactory.getLogger(RedisIndexCoordinateSender.class);

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private MiniSearchConfigure miniSearchConfigure;

    public RedisIndexCoordinateSender() {
        if (this.redisTemplate == null) {
            logger.error("WARN: redisTemplate may not involved, all operations will be standalone");
        }
        if (this.miniSearchConfigure == null) {
            logger.error("WARN: miniSearchConfigure null, default");
            miniSearchConfigure = new MiniSearchConfigure();
        }
    }

    @Override
    public void publish(EventType eventType, String instancerName, String key, Object carrier) throws Exception {
        Intent intent = new Intent();
        intent.setCarrier(carrier);
        intent.setAction(eventType.name());
        intent.setIndexName(instancerName);
        intent.setKey(key);
        logger.debug("send to " + miniSearchConfigure.getNotifyPatternChars() + instancerName);
        redisTemplate.convertAndSend(miniSearchConfigure.getNotifyPatternChars() + instancerName, intent);
    }


    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
