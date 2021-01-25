package com.ck.common.mini.util;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.Instancer;
import com.ck.common.mini.index.PinYinInstancer;
import com.ck.common.mini.index.SimpleInstancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author caikun
 * @Description
 * 推荐的单点miniSearch初始化方式
 * 直接使用简单初始化配置
 *
 * @Date 下午2:57 20-4-21
 **/
public class MiniSearch {

    static final Map<String, Instancer> instancerMap = new HashMap<String, Instancer>();

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
        return new PinYinInstancer(instancerName);
    }

    protected static synchronized Instancer instancer(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        if (miniSearchConfigure.getCoreType() == MiniSearchConfigure.CoreType.PINYIN.getCode()) {
            return new PinYinInstancer(instancerName, miniSearchConfigure);
        } else {
            return new SimpleInstancer(instancerName, miniSearchConfigure);
        }

    }
}
