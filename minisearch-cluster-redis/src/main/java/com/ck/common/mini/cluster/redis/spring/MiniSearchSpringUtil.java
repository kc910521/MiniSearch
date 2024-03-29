package com.ck.common.mini.cluster.redis.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author caikun
 * @Description
 * 获取bean
 *
 * @Date 下午5:05 20-4-26
 **/
//@Component("miniSearchSpringUtil")
public class MiniSearchSpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MiniSearchSpringUtil.applicationContext = applicationContext;
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return getApplicationContext().getBeansOfType(clazz);
    }

    /**
     * 通过name or class获取Bean.
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
