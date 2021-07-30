package com.ck.common.mini.timing;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.ClusterIndexInstance;
import com.ck.common.mini.index.IndexInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Author caikun
 * @Description 定时调用容器内所有 BasicInstancer#reindexing
 * @Date 下午4:01 21-7-26
 * @see IndexInstance.TimingLocalReindex
 * @see IndexInstance.RebuildWorker
 **/
public class TimingIndexReBuilder {

    private static final Logger logger = LoggerFactory.getLogger(TimingIndexReBuilder.class);

    /**
     * 注册在这个容器的都会被反复调用
     */
    private static Map</* machineTaskId */String, /* instanceMap */Map<String, IndexInstance>> mapHolder = new ConcurrentHashMap<>(256);

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

    static {

        TimingIndexReBuilder.rebuildPool.scheduleAtFixedRate(() -> {
            logger.info("rebuilding tools actives : {}", rebuildPool.getActiveCount());
            try {
                TimingIndexReBuilder.mapHolder.forEach((k, mp) -> {
                    logger.debug("{} go rebuilding", k);
                    mp.forEach((insName, instance) -> {
                        IndexInstance r1 = instance;
                        if (r1 instanceof ClusterIndexInstance) {
                            r1 = ((ClusterIndexInstance) r1).getLocalInstance();
                        }
                        if (r1 instanceof IndexInstance.TimingLocalReindex) {
                            ((IndexInstance.TimingLocalReindex) instance).reindexing();
                        } else {
                            logger.warn("unknown type of {} ", r1);
                        }
                    });

                });
            } catch (Throwable t) {
                logger.error("rebuild pool exception ", t);
            }
        }, MiniSearchConfigure.getRebuildTaskInterval(), MiniSearchConfigure.getRebuildTaskInterval(), TimeUnit.SECONDS);

    }

    /**
     * 被调用的方法
     * 相同的 Map<String, Instancer> miniSearchMap 会使用一个定时任务
     *
     * @param miniSearchMap
     * @see com.ck.common.mini.util.MiniSearch
     */
    public static void registerReBuildMap(final Map<String, IndexInstance> miniSearchMap) {
        synchronized (mapHolder) {
            for (Map.Entry<String, Map<String, IndexInstance>> mapEntry : mapHolder.entrySet()) {
                if (mapEntry.getValue() == miniSearchMap) {
                    // 存在相等则退出
                    return;
                }
            }
            mapHolder.put(UUID.randomUUID().toString(), miniSearchMap);
        }
    }


}
