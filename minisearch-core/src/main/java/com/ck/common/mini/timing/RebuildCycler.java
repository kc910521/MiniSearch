package com.ck.common.mini.timing;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.Instancer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Author caikun
 * @Description 定时调用容器内所有 BasicInstancer#timingRebuild
 * @Date 下午4:01 21-7-26
 * @see Instancer.BasicInstancer
 * @see Instancer.RebuildWorker
 **/
public class RebuildCycler {


    private static Map</* machineTaskId */String, /* instanceMap */Map<String, Instancer>> mapHolder = new ConcurrentHashMap<>(256);
    /**
     * warn, unlimited queue
     */
    private final static ScheduledThreadPoolExecutor rebuildPool = new ScheduledThreadPoolExecutor(
            MiniSearchConfigure.getuCoreNumber(),
            (Runnable r) -> {
                Thread t = new Thread(r);
                t.setName("| Rebuilding in MiniSearch |");
                return t;
            },
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    /**
     * 被调用的方法
     * 相同的 Map<String, Instancer> miniSearchMap 会使用一个定时任务
     *
     * @param miniSearchMap
     * @see com.ck.common.mini.util.MiniSearch
     */
    public static void goForCycler(final Map<String, Instancer> miniSearchMap) {
        for (Map.Entry<String, Map<String, Instancer>> mapEntry : mapHolder.entrySet()) {
            if (mapEntry.getValue() == miniSearchMap) {
                // 相等则退出
                return;
            }
        }
        mapHolder.put(UUID.randomUUID().toString(), miniSearchMap);
        rebuildPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                miniSearchMap.forEach((k, v) -> {
                    if (v instanceof Instancer.BasicInstancer) {
                        ((Instancer.BasicInstancer) v).timingRebuild();
                    }
                });
            }
        }, 10, 10, TimeUnit.SECONDS);

    }


}
