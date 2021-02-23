package com.ck.common.mini.config;

import com.ck.common.mini.controller.MiniSearchRestController;
import com.ck.common.mini.service.ClusterServiceImpl;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author caikun
 * @Description 打上此注解，使springboot成为搜索服务器
 * @Date 下午5:53 21-1-27
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MiniSearchRestController.class, ClusterServiceImpl.class})
public @interface MiniSearchServer {
}
