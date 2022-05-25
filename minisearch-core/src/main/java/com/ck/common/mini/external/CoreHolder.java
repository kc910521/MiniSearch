package com.ck.common.mini.external;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.instance.FlexibleMatchInstance;
import com.ck.common.mini.index.struct.MiniInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author caikun
 * @Description holding Config and Struct
 * @Date 下午2:14 22-5-20
 **/
public class CoreHolder {

    /**
     * indexName --> IExternalInstance
     */
    private static Map<String, MiniInstance> instanceHolder = new ConcurrentHashMap<>(64);

    /**
     * indexName --> Config
     */
    private static Map<String, MiniSearchConfigure> configHolder = new ConcurrentHashMap<>(64);


    public static MiniInstance findOrSet(String indexName) {
        MiniSearchConfigure config = configHolder.computeIfAbsent(indexName, in -> new MiniSearchConfigure());
        return instanceHolder.computeIfAbsent(indexName, in -> new FlexibleMatchInstance(indexName, config));
    }

    public static MiniInstance findOrSet(String indexName, MiniSearchConfigure configure) {
        configHolder.put(indexName, configure);
        return instanceHolder.computeIfAbsent(indexName, in -> new FlexibleMatchInstance(indexName, configure));
    }

    public static MiniInstance geInstance(String indexName) {
        return instanceHolder.get(indexName);
    }

    public static MiniSearchConfigure getConfig(String indexName) {
        return configHolder.get(indexName);
    }

    public static void clear(String indexName) {
        instanceHolder.remove(indexName);
        configHolder.remove(indexName);
    }

    private CoreHolder() {
    }


}
