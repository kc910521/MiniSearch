package com.ck.common.mini.cluster.redis.spring;

import com.ck.common.mini.cluster.redis.util.SpringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author caikun
 * @Description redis-spring 支持组件
 * @Date 上午10:47 21-7-27
 * @see BeanDefinitionRegistryPostProcessor
 **/
public class SpringRedisDefinitionSupport implements BeanDefinitionRegistryPostProcessor {

    public final static String MSRedisTemplateBeanName = "msRedisTemplateChunk";

    private final static String RedisTemplateClassName = "org.springframework.data.redis.core.StringRedisTemplate";

    /**
     * for spring boot fast startup
     */
    private final static String defaultRedisConnectionFactoryBeanName = "redisConnectionFactory";

    private static final Logger logger = LoggerFactory.getLogger(SpringRedisDefinitionSupport.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // redis
        if (!beanDefinitionRegistry.containsBeanDefinition(MSRedisTemplateBeanName)) {
            if (SpringTools.isClassPresent(RedisTemplateClassName)) {
                BeanDefinitionBuilder sdtBDBuilder = BeanDefinitionBuilder.genericBeanDefinition(StringRedisTemplate.class);
                sdtBDBuilder.addConstructorArgReference(defaultRedisConnectionFactoryBeanName);
                sdtBDBuilder.addDependsOn(defaultRedisConnectionFactoryBeanName);
                sdtBDBuilder.addPropertyValue("defaultSerializer", new GenericJackson2JsonRedisSerializer());
                sdtBDBuilder.addPropertyValue("keySerializer", new StringRedisSerializer());
                sdtBDBuilder.addPropertyValue("valueSerializer", new GenericJackson2JsonRedisSerializer());
                sdtBDBuilder.addPropertyValue("hashKeySerializer", new GenericJackson2JsonRedisSerializer());
                sdtBDBuilder.addPropertyValue("hashValueSerializer", new GenericJackson2JsonRedisSerializer());
                beanDefinitionRegistry.registerBeanDefinition(MSRedisTemplateBeanName, sdtBDBuilder.getBeanDefinition());
            } else {
                throw new RuntimeException(RedisTemplateClassName + " not found");
            }
        } else {
            logger.warn(MSRedisTemplateBeanName + " override");
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if (configurableListableBeanFactory.containsBean(MSRedisTemplateBeanName)) {
//            BeanDefinition redisTemplateBeanD = configurableListableBeanFactory.getMergedBeanDefinition(MSRedisTemplateBeanName);
//            for (String beanDefinitionName : configurableListableBeanFactory.getBeanDefinitionNames()) {
//                BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanDefinitionName);
//                if (beanDefinition.get) {
//
//                }
//            }
            // 装配connFactory
//            mergedBeanDefinition.setAutowireCandidate(true);
//            mergedBeanDefinition.setAttribute();
            // RedisConnectionFactory is required
//            RedisTemplate redisTemplate = (RedisTemplate) configurableListableBeanFactory.getBean(MSRedisTemplateBeanName);
//            configurableListableBeanFactory.autowireBean(redisTemplate);
        }
    }
}
