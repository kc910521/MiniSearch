package com.ck.common.mini.config;

import com.ck.common.mini.controller.MiniSearchRestController;
import com.ck.common.mini.service.ClusterServiceImpl;
import com.ck.common.mini.spring.DefaultMiniSearchSpringConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MiniSearchRestController.class, ClusterServiceImpl.class, DefaultMiniSearchSpringConfig.class})
public class MiniSearchBootConfig {

    @Autowired
    private DefaultMiniSearchSpringConfig defaultMiniSearchSpringConfig;
}
