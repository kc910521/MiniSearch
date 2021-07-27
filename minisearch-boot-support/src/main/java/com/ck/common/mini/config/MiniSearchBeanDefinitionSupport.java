package com.ck.common.mini.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @Author caikun
 * @Description spring-config 支持组件
 * @Date 上午10:47 21-7-27
 * @see BeanDefinitionRegistryPostProcessor
 **/
public class MiniSearchBeanDefinitionSupport implements BeanDefinitionRegistryPostProcessor {

    public final static String MSConfigBeanName = "miniSearchConfigure";

    private static final Logger logger = LoggerFactory.getLogger(MiniSearchBeanDefinitionSupport.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // config
        if (!beanDefinitionRegistry.containsBeanDefinition(MSConfigBeanName)) {
            BeanDefinitionBuilder beanDefinitionBuilder1 = BeanDefinitionBuilder.genericBeanDefinition(MiniSearchConfigure.class);
            beanDefinitionRegistry.registerBeanDefinition(MSConfigBeanName, beanDefinitionBuilder1.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
}
