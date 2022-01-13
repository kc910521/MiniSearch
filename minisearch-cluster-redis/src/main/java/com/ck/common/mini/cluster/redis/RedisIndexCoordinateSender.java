package com.ck.common.mini.cluster.redis;

import com.ck.common.mini.cluster.IndexCoordinatorIndexInstanceProxy;
import com.ck.common.mini.cluster.IndexEventSender;
import com.ck.common.mini.cluster.Intent;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.util.SpringTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

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
public class RedisIndexCoordinateSender implements IndexEventSender {

    private static final Logger logger = LoggerFactory.getLogger(RedisIndexCoordinateSender.class);

    private static RedisTemplate redisTemplate;

    private static MiniSearchConfigure miniSearchConfigure;

    public RedisIndexCoordinateSender() {
        checkDep();
    }

    public void checkDep() {
        if (RedisIndexCoordinateSender.redisTemplate == null) {
            logger.error("WARN: redisTemplate is null.");
        }
        if (RedisIndexCoordinateSender.miniSearchConfigure == null) {
            logger.error("WARN: miniSearchConfigure null, default");
            miniSearchConfigure = new MiniSearchConfigure();
        }
        logger.debug("RedisIndexCoordinateSender loaded.");
    }

    @Override
    public void publish(EventType eventType, String instancerName, String key, Object carrier) throws Exception {
        //  不考虑时钟回拨、节点所在时间对时不一致问题
        Intent intent = new Intent(System.currentTimeMillis());
        intent.setCarrier(carrier);
        intent.setAction(eventType.name());
        intent.setIndexName(instancerName);
        intent.setKey(key);
        logger.debug("send to " + miniSearchConfigure.getNotifyCharsPrefix() + instancerName);
        if (redisTemplate == null) {
            throw new RuntimeException("redisTemplate may not involved, all operations will be standalone");
        }
        redisTemplate.convertAndSend(miniSearchConfigure.getNotifyCharsPrefix() + instancerName, intent);
    }

    public static void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisIndexCoordinateSender.redisTemplate = redisTemplate;
    }

    public static void setMiniSearchConfigure(MiniSearchConfigure miniSearchConfigure) {
        RedisIndexCoordinateSender.miniSearchConfigure = miniSearchConfigure;
    }

}
