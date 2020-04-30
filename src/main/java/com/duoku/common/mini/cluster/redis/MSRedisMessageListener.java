package com.duoku.common.mini.cluster.redis;

import com.duoku.common.mini.cluster.Intent;
import com.duoku.common.mini.constant.EventType;
import com.duoku.common.mini.cluster.IndexEventExecutor;
import com.duoku.common.mini.index.Instancer;
import com.duoku.common.mini.util.MiniSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Author caikun
 * @Description 集群广播信息监听
 * @Date 下午3:06 20-4-24
 **/
@Component("msRedisMessageListener")
public class MSRedisMessageListener implements MessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private IndexEventExecutor indexEventExecutor;

    private final Byte[] lock = new Byte[0];

    private static final Logger logger = LoggerFactory.getLogger(MSRedisMessageListener.class);

    @PostConstruct
    public void init() {
        System.out.println("MSRedisMessageListener");
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        synchronized (lock) {
            logger.info("redis message received");
            try {
                byte[] body = message.getBody();
                Intent deserializeBody = (Intent) getRedisTemplate().getValueSerializer().deserialize(body);
                String deserializeChannel = (String) getRedisTemplate().getKeySerializer().deserialize(message.getChannel());
                logger.debug("deserializeChannel:{}", deserializeChannel);
                Instancer instance = MiniSearch.findInstance(deserializeBody.getIndexName());
                if (EventType.REMOVE.name().equals(deserializeBody.getAction())) {
                    logger.debug(deserializeBody.getAction());
                    instance.remove(deserializeBody.getKey());
                } else if (EventType.UPDATE.name().equals(deserializeBody.getAction())) {
                    logger.debug(deserializeBody.getAction());
                    instance.remove(deserializeBody.getKey());
                    instance.add(deserializeBody.getKey(), deserializeBody.getCarrier());
                } else if (EventType.ADD.name().equals(deserializeBody.getAction())) {
                    logger.debug(deserializeBody.getAction());
                    instance.add(deserializeBody.getKey(), deserializeBody.getCarrier());
                } else if (EventType.INIT.name().equals(deserializeBody.getAction())) {
                    logger.debug(deserializeBody.getAction());
                    // fixme : try it
                    instance.init((Map<String, Object>) deserializeBody.getCarrier());
                } else {
                    logger.error("action {} ,not support", deserializeBody.getAction());
                }
            } catch (Exception e) {
                logger.error("error: ", e);
            } finally {
                logger.debug("consume finished");
            }
        }
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public IndexEventExecutor getIndexEventExecutor() {
        return indexEventExecutor;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setIndexEventExecutor(IndexEventExecutor indexEventExecutor) {
        this.indexEventExecutor = indexEventExecutor;
    }

}
