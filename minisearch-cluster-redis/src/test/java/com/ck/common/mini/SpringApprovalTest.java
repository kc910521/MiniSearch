package com.ck.common.mini;


import com.ck.common.mini.cluster.redis.RedisIndexCoordinateSender;
import com.ck.common.mini.config.DefaultMiniSearchSpringRedisConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @Author caikun
 * @Description 对和spring的集成做测试
 * @Date 下午5:39 21-7-30
 **/
public class SpringApprovalTest {

    public static void springContainer() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.scan("com.ck.common.mini");

        annotationConfigApplicationContext.register(RedisIndexCoordinateSender.class);
        annotationConfigApplicationContext.refresh();
        System.out.println("refresh ok");
//        annotationConfigApplicationContext.getBeansOfType()

    }

    public static void main(String[] args) {
        springContainer();
    }

}
