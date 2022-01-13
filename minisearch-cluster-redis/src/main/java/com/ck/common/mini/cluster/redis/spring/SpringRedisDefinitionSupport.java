package com.ck.common.mini.cluster.redis.spring;

import com.ck.common.mini.cluster.redis.RedisIndexCoordinateSender;
import com.ck.common.mini.util.SpringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author caikun
 * @Description redis-spring 支持组件
 * @Date 上午10:47 21-7-27
 * 注册mini search自使用 redisTemplate
 *
 *
 * @see BeanDefinitionRegistryPostProcessor
 **/
public class SpringRedisDefinitionSupport implements BeanDefinitionRegistryPostProcessor {

    public final static String MSRedisTemplateBeanName = "miniSearchRedisTemplate";
//    public final static String MSRedisTemplateBeanName = "miniSearchSpringUtil";


    private final static String RedisTemplateClassName = "org.springframework.data.redis.core.StringRedisTemplate";

    /**
     * for spring boot fast startup
     */

    private static final Logger logger = LoggerFactory.getLogger(SpringRedisDefinitionSupport.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // commons
        beanDefinitionRegistry.registerBeanDefinition("miniSearchSpringUtil", BeanDefinitionBuilder.genericBeanDefinition(MiniSearchSpringUtil.class).getBeanDefinition());
        // redis
        if (!beanDefinitionRegistry.containsBeanDefinition(MSRedisTemplateBeanName)) {
            if (SpringTools.isClassPresent(RedisTemplateClassName)) {
                beanDefinitionRegistry.registerBeanDefinition(SpringRedisDefinitionSupport.MSRedisTemplateBeanName, BeanDefinitionBuilder.genericBeanDefinition(MiniSearchRedisTemplateFactoryBean.class).getBeanDefinition());

//                BeanDefinitionBuilder sdtBDBuilder = BeanDefinitionBuilder.genericBeanDefinition(StringRedisTemplate.class);
//                sdtBDBuilder.addConstructorArgReference(defaultRedisConnectionFactoryBeanName);
//                sdtBDBuilder.addDependsOn(defaultRedisConnectionFactoryBeanName);
//                sdtBDBuilder.addPropertyValue("defaultSerializer", new GenericJackson2JsonRedisSerializer());
//                sdtBDBuilder.addPropertyValue("keySerializer", new StringRedisSerializer());
//                sdtBDBuilder.addPropertyValue("valueSerializer", new GenericJackson2JsonRedisSerializer());
//                sdtBDBuilder.addPropertyValue("hashKeySerializer", new GenericJackson2JsonRedisSerializer());
//                sdtBDBuilder.addPropertyValue("hashValueSerializer", new GenericJackson2JsonRedisSerializer());
//                beanDefinitionRegistry.registerBeanDefinition(MSRedisTemplateBeanName, sdtBDBuilder.getBeanDefinition());
            } else {
                throw new RuntimeException(RedisTemplateClassName + " not found");
            }
        } else {
            logger.error("bean with name: {} will be override !", MSRedisTemplateBeanName);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//        for (String bdName : configurableListableBeanFactory.getBeanDefinitionNames()) {
//            AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) configurableListableBeanFactory.getBeanDefinition(bdName);
//            if (beanDefinition.hasBeanClass()) {
//                Class<?> beanClass = beanDefinition.getBeanClass();
//                System.out.println(beanClass);
//            }
//        }
//        Object bean = configurableListableBeanFactory.getBean(defaultRedisConnectionFactoryBeanName);
//        if (!configurableListableBeanFactory.containsBean(defaultRedisConnectionFactoryBeanName)) {
//
//            RootBeanDefinition redisTemplateBeanDefinition = (RootBeanDefinition) configurableListableBeanFactory.getMergedBeanDefinition(MSRedisTemplateBeanName);
//
//
//            for (String bdName : configurableListableBeanFactory.getBeanDefinitionNames()) {
//                AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) configurableListableBeanFactory.getBeanDefinition(bdName);
//                if (beanDefinition.hasBeanClass()) {
//                    Class<?> beanClass = beanDefinition.getBeanClass();
//                    System.out.println(beanClass);
//                }
//            }
//            logger.warn("{} not in exist, find a default one", defaultRedisConnectionFactoryBeanName);
        /**
         *
         Description:

         The bean 'miniSearchRedisTemplate' could not be registered. A bean with that name has already been defined and overriding is disabled.

         Action:

         Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
         */
//            ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();
//            constructorArgs.addIndexedArgumentValue(0,  new RuntimeBeanReference("redisMiniMom"));
//            redisTemplateBeanDefinition.setConstructorArgumentValues(constructorArgs);
//            ((DefaultListableBeanFactory) configurableListableBeanFactory).registerBeanDefinition(MSRedisTemplateBeanName, redisTemplateBeanDefinition);

//            Map<String, RedisConnectionFactory> beansOfType = configurableListableBeanFactory.containsBean(RedisConnectionFactory.class);
//            if (beansOfType.isEmpty()) {
//                logger.error("{} not found", RedisConnectionFactory.class);
//                throw new RuntimeException(RedisConnectionFactory.class + "| bean of type not found");
//            }
//            Set<Map.Entry<String, RedisConnectionFactory>> entries = beansOfType.entrySet();
//            Map.Entry<String, RedisConnectionFactory> stringRedisConnectionFactoryEntry = entries.stream().findFirst().get();
//            logger.warn("bean Name:{} is chosen for Mini-Search !!! ", stringRedisConnectionFactoryEntry.getKey());
        // todo: need a proper way to find default redis connection factory

//            redisTemplateBeanDefinition.setAttribute();
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
//        }
    }
}
