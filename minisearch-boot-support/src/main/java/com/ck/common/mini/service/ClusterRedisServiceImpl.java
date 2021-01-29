package com.ck.common.mini.service;

import com.ck.common.mini.bean.ResponseWrapper;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.Instancer;
import com.ck.common.mini.util.ClusterMiniSearch;
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.Map;

/**
 * @Author caikun
 * @Description
 * 此处是调用demo，并非最佳实践
 *
 *
 * @Date 下午4:56 21-1-27
 **/
public class ClusterRedisServiceImpl implements IClusterService {


    @Override
    public ResponseWrapper find(String name, String chars) {
        Instancer instance = ClusterMiniSearch.findInstance(name);
        Collection<Object> objects = instance.find(chars);
        return new ResponseWrapper(objects);
    }

    @Override
    public ResponseWrapper find(String name, String chars, int page, int pageSize) {
        Instancer instance = ClusterMiniSearch.findInstance(name);
        Collection<Object> objects = instance.find(chars, page, pageSize);
        return new ResponseWrapper(objects);
    }

    @Override
    public ResponseWrapper save(String name, String key, String value) {
        Instancer instance = ClusterMiniSearch.findInstance(name);
        if (Strings.isBlank(value) || key.equals(value)) {
            instance.add(key);
        } else {
            instance.add(key, value);
        }
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper saveWithId(String id, String name, String key, String value) {
        Instancer instance = ClusterMiniSearch.findInstance(name);
        if (Strings.isBlank(value) || key.equals(value)) {
            instance.addWithId(id, key, key);
        } else {
            instance.addWithId(id, key, value);
        }
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper remove(String name, String key) {
        Instancer instance = ClusterMiniSearch.findInstance(name);
        int remove = instance.remove(key);
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper removeWithId(String id, String name, String key) {
        Instancer instance = ClusterMiniSearch.findInstance(name);
        int remove = instance.removeWithId(id, key);
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper init(String name, Map<String, Object> initMap) {
        Instancer instance = ClusterMiniSearch.findInstance(name);
        if (initMap == null || initMap.isEmpty()) {
            initMap = null;
        }
        instance.init(initMap);
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper config(String name, MiniSearchConfigure miniSearchConfigure) {
        Instancer instance = ClusterMiniSearch.findInstance(name, miniSearchConfigure);
        instance.init(null);
        return new ResponseWrapper();
    }

}
