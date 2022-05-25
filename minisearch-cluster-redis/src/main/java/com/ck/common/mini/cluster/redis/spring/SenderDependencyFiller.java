package com.ck.common.mini.cluster.redis.spring;

import com.ck.common.mini.cluster.redis.RedisIndexCoordinateSender;
import com.ck.common.mini.config.MiniSearchConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author caikun
 * @Description 填充依赖
 * @Date 下午3:24 22-1-13
 **/
public class SenderDependencyFiller implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SenderDependencyFiller.class);

    @Override
    public Object postProcessBeforeInitialization(Object o, String bn) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String bn) throws BeansException {
        if (bn.equals(SpringRedisDefinitionSupport.MSRedisTemplateBeanName)) {
            logger.info("{} checked", SpringRedisDefinitionSupport.MSRedisTemplateBeanName);
            if (o instanceof RedisTemplate) {
                RedisIndexCoordinateSender.loadRedisTemplate((RedisTemplate) o);
            } else {
//                System.out.println("?");
            }
        } else if (o instanceof MiniSearchConfigure) {
//            logger.info("MiniSearchConfigure")
//            RedisIndexCoordinateSender.setMiniSearchConfigure((MiniSearchConfigure) o);
        }
        return o;
    }
}
