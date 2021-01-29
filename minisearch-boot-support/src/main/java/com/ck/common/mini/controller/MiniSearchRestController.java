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

    @RequestMapping("save/{indexName}")
    public ResponseWrapper save(@PathVariable String indexName, String key, @RequestParam(required = false) String value) {
        return clusterService.save(indexName, key, value);
    }

    @RequestMapping("save/{indexName}/id/{id}")
    public ResponseWrapper saveWithId(@PathVariable String indexName, @PathVariable String id, String key, @RequestParam(required = false) String value) {
        return clusterService.saveWithId(id, indexName, key, value);
    }

    @RequestMapping("remove/{indexName}")
    public ResponseWrapper remove(@PathVariable String indexName, String key) {
        return clusterService.remove(indexName, key);
    }

    @RequestMapping("remove/{indexName}/id/{id}")
    public ResponseWrapper remove(@PathVariable String indexName, @PathVariable String id, String key) {
        return clusterService.removeWithId(id, indexName, key);
    }

    @RequestMapping("find/{indexName}")
    public ResponseWrapper find(@PathVariable String indexName, String key) {
        return clusterService.find(indexName, key);
    }


    @RequestMapping("find/{indexName}/page/{page}/size/{pageSize}")
    public ResponseWrapper find(@PathVariable String indexName, String key, @PathVariable int page, @PathVariable int pageSize) {
        return clusterService.find(indexName, key, page, pageSize);
    }

    @RequestMapping("init/{indexName}")
    public ResponseWrapper init(@PathVariable String indexName, Map<String, Object> data) {
        return clusterService.init(indexName, data);
    }

    // todo: config需要完善赋值
    @RequestMapping("config/{indexName}")
    public ResponseWrapper config(@PathVariable String indexName, MiniSearchConfigure config) {
        return clusterService.config(indexName, config);
    }

}
