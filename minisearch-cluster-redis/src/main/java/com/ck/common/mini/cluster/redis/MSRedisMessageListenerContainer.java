package com.ck.common.mini.cluster.redis;

import com.ck.common.mini.config.MiniSearchConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @Author caikun
 * @Description 监听容器配置
 * @Date 上午11:07 20-4-26
 **/
@Scope("singleton")
@Component("msRedisMessageListenerContainer")
@DependsOn("msRedisMessageListener")
public class MSRedisMessageListenerContainer extends RedisMessageListenerContainer {

    private static final Logger logger = LoggerFactory.getLogger(MSRedisMessageListenerContainer.class);

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired(required = false)
    private MiniSearchConfigure miniSearchConfigure;

    @Autowired
    private MSRedisMessageListener msRedisMessageListener;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    @PostConstruct
    public void init() {
        try {
            if (msRedisMessageListener == null) {
                logger.error("MSRedisMessageListener not found");
                throw new RuntimeException("MSRedisMessageListener not found");
            }
        } catch (Exception e) {
            logger.error("msRedisMessageListener:{}", e);
            return;
        }
        // thread pool
        if (miniSearchConfigure == null) {
            miniSearchConfigure = new MiniSearchConfigure();
            logger.warn("no MiniSearchConfigure found, use default.");
        }

        threadPoolTaskScheduler.setPoolSize(miniSearchConfigure.getClusterContainerPoolSize());
        threadPoolTaskScheduler.initialize();



        String s = miniSearchConfigure.getNotifyPatternChars() + "*";
        // 1
        PatternTopic patternTopic = new PatternTopic(s);
        List<Topic> list = new ArrayList<Topic>();
        list.add(patternTopic);
        Map<MSRedisMessageListener, Collection<? extends Topic>> rs = new HashMap<>();
        rs.put(this.msRedisMessageListener, list);
        this.setMessageListeners(rs);
        this.setConnectionFactory(connectionFactory);
        // 2
//        threadPoolTaskScheduler.setPoolSize(miniSearchConfigure.getClusterContainerPoolSize());
        this.setTaskExecutor(threadPoolTaskScheduler);
        this.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(Throwable throwable) {
                logger.warn("err", throwable);
            }
        });
    }

    public void setMiniSearchConfigure(MiniSearchConfigure miniSearchConfigure) {
        this.miniSearchConfigure = miniSearchConfigure;
    }

    public void setMsRedisMessageListener(MSRedisMessageListener msRedisMessageListener) {
        this.msRedisMessageListener = msRedisMessageListener;
    }

}
