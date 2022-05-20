package com.ck.common.mini.util;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.external.CoreHolder;
import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.index.struct.IExternalInstance;
import com.ck.common.mini.timing.IRotateInstance;
import com.ck.common.mini.timing.TimingIndexReBuilder;

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

    public static synchronized IExternalInstance findInstance(String indexName) {
        return CoreHolder.findOrSet(indexName);
    }

    public static IExternalInstance findInstance(String indexName, @Nullable MiniSearchConfigure miniSearchConfigure) {
        if (miniSearchConfigure == null) {
            return CoreHolder.findOrSet(indexName);
        } else {
            return CoreHolder.findOrSet(indexName, miniSearchConfigure);
        }
    }

    private static IExternalInstance getExistInstance(String indexName) {
        IExternalInstance instance = CoreHolder.geInstance(indexName);
        if (instance == null) {
            throw new MiniSearchException("instance missing");
        }
        return instance;
    }

    /**
     * 注册定时任务，比如定时索引重建
     *
     * @param indexName 必须存在，不存在无法执行，fail-fast
     * @param builder
     */
    public static void registerJob(String indexName, IRotateInstance.RebuildWorker builder) {
        if (builder == null) {
            throw new MiniSearchException("builder is null");
        }
        IExternalInstance instance = getExistInstance(indexName);
        if (instance == null) {
            throw new MiniSearchException("init your index instance first");
        }
        TimingIndexReBuilder.register(indexName, builder);
    }


}
