package com.ck.common.mini.service;

import com.ck.common.mini.bean.ResponseWrapper;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.struct.MiniInstance;
import com.ck.common.mini.util.MiniSearch;
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
public class ClusterServiceImpl implements IClusterService {


    @Override
    public ResponseWrapper find(String name, String chars) {
        MiniInstance instance = MiniSearch.findInstance(name);
        Collection<Object> objects = instance.find(chars);
        return new ResponseWrapper(objects);
    }

    @Override
    public ResponseWrapper find(String name, String chars, int page, int pageSize) {
        MiniInstance instance = MiniSearch.findInstance(name);
        Collection<Object> objects = instance.find(chars, page, pageSize);
        return new ResponseWrapper(objects);
    }

    @Override
    public ResponseWrapper save(String name, String key, String value) {
        MiniInstance instance = MiniSearch.findInstance(name);
        if (Strings.isBlank(value) || key.equals(value)) {
            instance.add(key);
        } else {
            instance.add(key, value);
        }
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper saveWithId(String id, String name, String key, String value) {
        MiniInstance instance = MiniSearch.findInstance(name);
        if (Strings.isBlank(value) || key.equals(value)) {
            instance.addWithId(id, key, key);
        } else {
            instance.addWithId(id, key, value);
        }
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper remove(String name, String key) {
        MiniInstance instance = MiniSearch.findInstance(name);
        int remove = instance.remove(key);
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper removeWithId(String id, String name, String key) {
        MiniInstance instance = MiniSearch.findInstance(name);
        int remove = instance.removeWithId(id, key);
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper init(String name, Map<String, Object> initMap) {
        MiniInstance instance = MiniSearch.findInstance(name);
        if (initMap == null || initMap.isEmpty()) {
            initMap = null;
        }
        instance.initData(initMap);
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper config(String name, MiniSearchConfigure miniSearchConfigure) {
        MiniInstance instance = MiniSearch.findInstance(name);
        instance.initData(null);
        return new ResponseWrapper();
    }

}
