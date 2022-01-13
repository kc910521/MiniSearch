package com.ck.common.mini.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MiniSearchBeanDefinitionSupport.class})
@ComponentScan("com.ck.common.mini.config")
public class MiniSearchBootConfig {

}
