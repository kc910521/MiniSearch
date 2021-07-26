package com.ck.common.mini.util;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.Instancer;
import com.ck.common.mini.timing.RebuildCycler;

import javax.annotation.Nullable;
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

    static final Map<String, Instancer> miniSearchMap = new HashMap<String, Instancer>();

    public static synchronized Instancer findInstance(String instancerName) {
        return MiniSearch.findInstance(instancerName, null);
    }

    public static Instancer findInstance(String instancerName, @Nullable MiniSearchConfigure miniSearchConfigure) {
        if (miniSearchMap.containsKey(instancerName)) {
            return miniSearchMap.get(instancerName);
        } else {
            synchronized (miniSearchMap) {
                if (!miniSearchMap.containsKey(instancerName)) {
                    Instancer instancer = instancer(instancerName, miniSearchConfigure);
                    miniSearchMap.put(instancerName, instancer);
                    return instancer;
                }
            }
            return miniSearchMap.get(instancerName);

        }
    }

    /**
     * 设置循环调用器
     */
    public static void enableRebuild() {
        RebuildCycler.goForCycler(miniSearchMap);
    }

    protected static synchronized Instancer instancer(String instancerName) {
        return MiniSearch.instancer(instancerName, null);
    }

    protected static synchronized Instancer instancer(String instancerName, @Nullable MiniSearchConfigure miniSearchConfigure) {
        if (miniSearchConfigure == null) {
            miniSearchConfigure = new MiniSearchConfigure();
        }
        try {
            return MiniSearchConfigure.InstanceType.judge(miniSearchConfigure.getCoreType()).getInstance(instancerName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
