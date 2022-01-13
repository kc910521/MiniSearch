package com.ck.common.mini.compoment;

import com.ck.common.mini.controller.MiniSearchRestController;
import com.ck.common.mini.service.ClusterServiceImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author caikun
 * @Description
 * @Date 上午10:58 22-1-13
 **/
@Configuration
@Import({MiniSearchRestController.class, ClusterServiceImpl.class,})
@ComponentScan("com.ck.common.mini.service")
public class OpenAPIConfig {
}
