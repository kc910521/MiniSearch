package com.ck.common.mini.controller;

import com.ck.common.mini.bean.ResponseWrapper;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.service.ClusterRedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author caikun
 * @Description API入口
 * @Date 下午4:55 21-1-27
 **/
@RestController
@RequestMapping("mini/search")
public class MiniSearchRestController {

    @Autowired
    private ClusterRedisServiceImpl clusterRedisService;

    @RequestMapping("save/{name}")
    public ResponseWrapper save(@PathVariable String name, String key, @RequestParam(required = false) String value) {
        return clusterRedisService.save(name, key, value);
    }

    @RequestMapping("remove/{name}")
    public ResponseWrapper remove(@PathVariable String name, String key) {
        return clusterRedisService.remove(name, key);
    }

    @RequestMapping("find/{name}")
    public ResponseWrapper find(@PathVariable String name, String key) {
        return clusterRedisService.find(name, key);
    }

    @RequestMapping("init/{name}")
    public ResponseWrapper init(@PathVariable String name, Map<String, Object> data) {
        return clusterRedisService.init(name, data);
    }

    // todo: config需要完善赋值
    @RequestMapping("config/{name}")
    public ResponseWrapper config(@PathVariable String name, MiniSearchConfigure config) {
        return clusterRedisService.config(name, config);
    }

}
