package com.ck.common.mini.cluster.redis;

import com.ck.common.mini.cluster.Intent;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.cluster.redis.spring.SpringRedisDefinitionSupport;
import com.ck.common.mini.index.struct.MiniInstance;
import com.ck.common.mini.util.MiniSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author caikun
 * @Description 集群广播信息监听
 * 对redis实现来说，此处需注意线程安全问题.
 *
 * 不需要考虑循环消费，因为接收后不再转发。
 *
 * @Date 下午3:06 20-4-24
 **/
@Component("msRedisMessageListener")
public class MSRedisMessageListener implements MessageListener {

    @Autowired
    @Qualifier(SpringRedisDefinitionSupport.MSRedisTemplateBeanName)
    private RedisTemplate redisTemplate;

    private AtomicLong lastAcceptedVersion = new AtomicLong(-1L);

    private static final Logger logger = LoggerFactory.getLogger(MSRedisMessageListener.class);

    @PostConstruct
    public void init() {
        logger.info("MSRedisMessageListener initData");
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        logger.debug("redis message received");
        boolean executed = false;
        try {
            byte[] body = message.getBody();
            Intent deserializeBody = (Intent) redisTemplate.getValueSerializer().deserialize(body);
            long msgVersion = deserializeBody.getVersion();
            logger.debug("deserializeBody:{}", deserializeBody);
            while (msgVersion > lastAcceptedVersion.get()
                    &&
                    lastAcceptedVersion.compareAndSet(lastAcceptedVersion.get(), msgVersion)) {
                MiniInstance instance = MiniSearch.findInstance(deserializeBody.getIndexName());
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
                    instance.initData((Map<String, Object>) deserializeBody.getCarrier());
                } else {
                    logger.error("action {} ,not support", deserializeBody.getAction());
                }
                executed = true;
            }
        } catch (Exception e) {
            logger.error("error: ", e);
            executed = false;
        } finally {
            logger.debug("consume finished");
            if (!executed) {
                logger.warn("info: {} , no operation, ", message);
            }
        }

    }

}
