package com.duoku.common.mini.cluster.redis;

import com.duoku.common.mini.config.MiniSearchConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @Author caikun
 * @Description 监听容器配置
 * @Date 上午11:07 20-4-26
 **/
@Component
@Scope("singleton")
public class MSRedisMessageListenerContainer extends RedisMessageListenerContainer {

    private static final Logger logger = LoggerFactory.getLogger(MSRedisMessageListenerContainer.class);
    // private RedisConnectionFactory connectionFactory;

    @Autowired(required = false)
    private MiniSearchConfigure miniSearchConfigure;

    @Autowired
    private MSRedisMessageListener msRedisMessageListener;

    @PostConstruct
    public void init() {
        if (miniSearchConfigure == null) {
            miniSearchConfigure = new MiniSearchConfigure();
            logger.warn("no MiniSearchConfigure found, use default.");
        }

        String s = miniSearchConfigure.getNotifyPatternChars() + "*";
        // 1
        PatternTopic patternTopic = new PatternTopic(s);
        List<Topic> list = new ArrayList<Topic>();
        list.add(patternTopic);
        Map<MSRedisMessageListener, Collection<? extends Topic>> rs = new HashMap<>();
        rs.put(msRedisMessageListener, list);
        this.setMessageListeners(rs);
        // 2
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(miniSearchConfigure.getClusterContainerPoolSize());
        this.setTaskExecutor(threadPoolTaskScheduler);
    }

    public void setMiniSearchConfigure(MiniSearchConfigure miniSearchConfigure) {
        this.miniSearchConfigure = miniSearchConfigure;
    }

    public void setMsRedisMessageListener(MSRedisMessageListener msRedisMessageListener) {
        this.msRedisMessageListener = msRedisMessageListener;
    }
}
