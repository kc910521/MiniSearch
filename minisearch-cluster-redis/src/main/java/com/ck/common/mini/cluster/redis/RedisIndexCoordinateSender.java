package com.ck.common.mini.cluster.redis;

import com.ck.common.mini.cluster.IndexEventSender;
import com.ck.common.mini.cluster.Intent;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.index.struct.RemoteDataChangeInstance;
import com.ck.common.mini.util.exception.MiniSearchException;
import com.ck.common.mini.util.exception.MiniSearchSuperException;
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
 * @see RemoteDataChangeInstance
 *
 *
 **/
public class RedisIndexCoordinateSender implements IndexEventSender {

    private static final Logger logger = LoggerFactory.getLogger(RedisIndexCoordinateSender.class);

    private static RedisTemplate redisTemplate;

    private static MiniSearchConfigure miniSearchConfigure;

    private final String senderIdentity;

    public RedisIndexCoordinateSender() {
        this.senderIdentity = "as";
    }

    private void check() {
        if (redisTemplate == null) {
            logger.error("redisTemplate is null.");
            throw new MiniSearchException("miniSearchConfigure is null");
        }
        if (this.miniSearchConfigure == null) {
            logger.error("miniSearchConfigure is null, default");
            throw new MiniSearchException("miniSearchConfigure is null");
        }
        logger.debug("RedisIndexCoordinateSender loaded.");
        //todo: sender identity
    }



    @Override
    public void publish(EventType eventType, String instanceName, String key, Object carrier) throws MiniSearchSuperException {
        if (this.senderIdentity == null) {
            throw new MiniSearchException("senderIdentity is null");
        }
        //  不考虑时钟回拨、节点所在时间对时不一致问题
        Intent intent = new Intent(System.currentTimeMillis());
        intent.setCarrier(carrier);
        intent.setAction(eventType.name());
        intent.setIndexName(instanceName);
        intent.setKey(key);
        if (logger.isDebugEnabled()) {
            logger.debug("send to " + miniSearchConfigure.getNotifyCharsPrefix() + instanceName);
        }
        try {
            redisTemplate.convertAndSend(miniSearchConfigure.getNotifyCharsPrefix() + instanceName, intent);
        } catch (Exception e) {
            throw new MiniSearchSuperException("redisTemplate may not involved, all operations will be standalone", e);
        }

    }

    @Override
    public void loadMiniSearchConfig(MiniSearchConfigure configure) {
        RedisIndexCoordinateSender.miniSearchConfigure = configure;
    }

    public static void loadRedisTemplate(RedisTemplate redisTemplate) {
        RedisIndexCoordinateSender.redisTemplate = redisTemplate;
    }

}
