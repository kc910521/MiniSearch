package com.ck.common.mini.compoment;

import com.ck.common.mini.config.MiniSearchBootConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author caikun
 * @Description 使用此注解才能加载openAPI
 * @Date 上午10:25 22-1-13
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MiniSearchBootConfig.class, OpenAPIConfig.class})
public @interface EnableMiniSearchAPI {
}
