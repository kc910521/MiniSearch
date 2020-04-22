package com.duoku.common.util;

import com.duoku.common.factory.Instancer;
import com.duoku.common.factory.SimpleInstancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author caikun
 * @Description 推荐的mini搜初始化方式，直接使用简单初始化配置
 * @Date 下午2:57 20-4-21
 **/
public class MiniSearch {

    private static final Map<String, Instancer> instancerMap = new HashMap<String, Instancer>();

    public static synchronized Instancer findInstance(String instancerName) {
        if (instancerMap.containsKey(instancerName)) {
            return instancerMap.get(instancerName);
        } else {
            Instancer instancer = instancer();
            instancerMap.put(instancerName, instancer);
            return instancer;
        }
    }

    protected static synchronized Instancer instancer() {
        return new SimpleInstancer();
    }


}
