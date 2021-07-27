package com.ck.common.mini.config;

import com.ck.common.mini.controller.MiniSearchRestController;
import com.ck.common.mini.service.ClusterServiceImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MiniSearchRestController.class, ClusterServiceImpl.class, MiniSearchBeanDefinitionSupport.class})
@ComponentScan("com.ck.common.mini.config")
public class MiniSearchBootConfig {

}
