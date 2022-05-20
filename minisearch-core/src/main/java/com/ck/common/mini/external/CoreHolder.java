package com.ck.common.mini.external;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.DictTree;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.index.instance.FlexibleMatchInstance;
import com.ck.common.mini.index.struct.IExternalInstance;

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
    private static Map<String, IExternalInstance> structHolder = new ConcurrentHashMap<>(64);

    /**
     * indexName --> Config
     */
    private static Map<String, MiniSearchConfigure> configHolder = new ConcurrentHashMap<>(64);


    public static IExternalInstance findOrSet(String indexName) {
        MiniSearchConfigure config = configHolder.computeIfAbsent(indexName, in -> new MiniSearchConfigure());
        return structHolder.computeIfAbsent(indexName, in -> new FlexibleMatchInstance(config));
    }

    public static IExternalInstance findOrSet(String indexName, MiniSearchConfigure configure) {
        configHolder.put(indexName, configure);
        return structHolder.computeIfAbsent(indexName, in -> new FlexibleMatchInstance(configure));
    }


    public static IExternalInstance geInstance(String indexName) {
        return structHolder.get(indexName);
    }

    public static MiniSearchConfigure getConfig(String indexName) {
        return configHolder.get(indexName);
    }

    public static void clear(String indexName) {
        structHolder.remove(indexName);
        configHolder.remove(indexName);
    }

    private CoreHolder() {
    }


}
