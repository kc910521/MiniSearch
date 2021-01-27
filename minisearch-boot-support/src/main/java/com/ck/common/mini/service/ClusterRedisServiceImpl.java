package com.ck.common.mini.service;

import com.ck.common.mini.bean.ResponseWrapper;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.Instancer;
import com.ck.common.mini.index.PinYinInstancer;
import com.ck.common.mini.util.MiniSearch;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午4:56 21-1-27
 **/
@Component
public class ClusterRedisServiceImpl {


    public ResponseWrapper find(String name, String chars) {
        Instancer instance = MiniSearch.findInstance(name);
        Collection<Object> objects = instance.find(chars);
        return new ResponseWrapper(objects);
    }

    public ResponseWrapper save(String name, String key, String value) {
        Instancer instance = MiniSearch.findInstance(name);
        if (Strings.isBlank(value) || key.equals(value)) {
            instance.add(key);
        } else {
            instance.add(key, value);
        }
        return new ResponseWrapper();
    }

    public ResponseWrapper remove(String name, String key) {
        Instancer instance = MiniSearch.findInstance(name);
        int remove = instance.remove(key);
        return new ResponseWrapper();
    }

    public ResponseWrapper init(String name, Map<String, Object> initMap) {
        Instancer instance = MiniSearch.findInstance(name);
        if (initMap == null || initMap.isEmpty()) {
            initMap = null;
        }
        instance.init(initMap);
        return new ResponseWrapper();
    }

    public ResponseWrapper config(String name, MiniSearchConfigure miniSearchConfigure) {
        Instancer instance = MiniSearch.findInstance(name, miniSearchConfigure);
        instance.init(null);
        return new ResponseWrapper();
    }

}
