package com.duoku.common.mini.cluster.redis;

import com.duoku.common.mini.config.MiniSearchConfigure;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import java.util.*;

/**
 * @Author caikun
 * @Description 监听容器配置
 * @Date 上午11:07 20-4-26
 **/
public class MSRedisMessageListenerContainer extends RedisMessageListenerContainer {

    // private RedisConnectionFactory connectionFactory;

    // private Executor taskExecutor;

    private MiniSearchConfigure miniSearchConfigure;

    private MSRedisMessageListener msRedisMessageListener;

    public MSRedisMessageListenerContainer() {
        super();
        String s = miniSearchConfigure.getNotifyPatternChars().replaceAll("%s", "*");
        // 1
        PatternTopic patternTopic = new PatternTopic(s);
        List<Topic> list = new ArrayList<Topic>();
        list.add(patternTopic);
        Map<MSRedisMessageListener, Collection<? extends Topic>> rs = new HashMap<>();
        rs.put(msRedisMessageListener, list);
        this.setMessageListeners(rs);
        // 2

    }

    public void setMiniSearchConfigure(MiniSearchConfigure miniSearchConfigure) {
        this.miniSearchConfigure = miniSearchConfigure;
    }
}
