package com.duoku.common.mini.cluster.redis;

import com.duoku.common.mini.constant.EventType;
import com.duoku.common.mini.cluster.IndexEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author caikun
 * @Description redis的集群协作发送者
 * @Date 下午1:38 20-4-24
 **/
public class RedisIndexCoordinateSender implements IndexEventSender {

    private static final Logger logger = LoggerFactory.getLogger(RedisIndexCoordinateSender.class);

    private String indexPatterName;

    private String indexName;

    public RedisIndexCoordinateSender(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        if (this.redisTemplate == null) {
            logger.error("redisTemplate is null");
        }
    }

    public RedisIndexCoordinateSender() {
    }

    private RedisTemplate redisTemplate;

    private String indexRedisKey;


    @Override
    public void publish(EventType eventType, String key, Object carrier) throws Exception {
        if (indexPatterName == null || "".equals(indexPatterName.trim())) {
            throw new RuntimeException("indexPatterName is null");
        }
        Intent intent = new Intent();
        intent.setCarrier(carrier);
        intent.setAction(eventType.name());
        intent.setIndexName(indexName);
        intent.setKey(key);
        redisTemplate.convertAndSend(indexRedisKey + eventType.name(), intent);
        logger.debug("do " + indexRedisKey + eventType.name());
    }

    @Override
    public void setCoreName(String coreName) {
        this.indexRedisKey = coreName;
    }

    @Override
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }


    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
