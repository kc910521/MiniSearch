package com.ck.common.mini.service;

import com.ck.common.mini.bean.ResponseWrapper;
import com.ck.common.mini.config.MiniSearchConfigure;

import java.util.Map;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午7:03 21-1-27
 **/
public interface IClusterService {
    ResponseWrapper find(String name, String chars);

    ResponseWrapper save(String name, String key, String value);

    ResponseWrapper remove(String name, String key);

    ResponseWrapper init(String name, Map<String, Object> initMap);

    ResponseWrapper config(String name, MiniSearchConfigure miniSearchConfigure);
}
