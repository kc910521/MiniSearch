package com.duoku.common.mini.util;

import com.duoku.common.mini.config.MiniSearchConfigure;
import com.duoku.common.mini.factory.Instancer;
import com.duoku.common.mini.factory.SimpleInstancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author caikun
 * @Description 推荐的mini搜初始化方式，直接使用简单初始化配置
 * @Date 下午2:57 20-4-21
 **/
public class MiniSearch {

    protected static final Map<String, Instancer> instancerMap = new HashMap<String, Instancer>();

    public static synchronized Instancer findInstance(String instancerName) {
        if (instancerMap.containsKey(instancerName)) {
            return instancerMap.get(instancerName);
        } else {
            Instancer instancer = instancer(instancerName);
            instancerMap.put(instancerName, instancer);
            return instancer;
        }
    }

    public static synchronized Instancer findInstance(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        if (instancerMap.containsKey(instancerName)) {
            return instancerMap.get(instancerName);
        } else {
            Instancer instancer = instancer(instancerName, miniSearchConfigure);
            instancerMap.put(instancerName, instancer);
            return instancer;
        }
    }

    protected static synchronized Instancer instancer(String instancerName) {
        return new SimpleInstancer(instancerName);
    }

    protected static synchronized Instancer instancer(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        return new SimpleInstancer(instancerName, miniSearchConfigure);
    }
}
