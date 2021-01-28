package com.ck.common.mini.controller;

import com.ck.common.mini.bean.ResponseWrapper;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.service.ClusterRedisServiceImpl;
import com.ck.common.mini.service.IClusterService;
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
@RequestMapping("ms-cluster-service")
public class MiniSearchRestController {

    @Autowired
    private IClusterService clusterService;

    @RequestMapping("save/{name}")
    public ResponseWrapper save(@PathVariable String name, String key, @RequestParam(required = false) String value) {
        return clusterService.save(name, key, value);
    }

    @RequestMapping("remove/{name}")
    public ResponseWrapper remove(@PathVariable String name, String key) {
        return clusterService.remove(name, key);
    }

    @RequestMapping("find/{name}")
    public ResponseWrapper find(@PathVariable String name, String key) {
        return clusterService.find(name, key);
    }


    @RequestMapping("find/{name}/page/{page}/size/{pageSize}")
    public ResponseWrapper find(@PathVariable String name, String key, @PathVariable int page, @PathVariable int pageSize) {
        return clusterService.find(name, key, page, pageSize);
    }

    @RequestMapping("init/{name}")
    public ResponseWrapper init(@PathVariable String name, Map<String, Object> data) {
        return clusterService.init(name, data);
    }

    // todo: config需要完善赋值
    @RequestMapping("config/{name}")
    public ResponseWrapper config(@PathVariable String name, MiniSearchConfigure config) {
        return clusterService.config(name, config);
    }

}
