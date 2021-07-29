package com.ck.common.mini.timing;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.Instancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TimingIndexReBuilder {

    private static final Logger logger = LoggerFactory.getLogger(TimingIndexReBuilder.class);

    /**
     * 注册在这个容器的都会被反复调用
     */
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

    static {

        TimingIndexReBuilder.rebuildPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("rebuilding tools actives : {}", rebuildPool.getActiveCount());
                try {
                    TimingIndexReBuilder.mapHolder.forEach((k, mp) -> {
                        logger.debug("{} go rebuilding", k);
                        mp.forEach((insName, instance) -> {
                            if (instance instanceof Instancer.BasicInstancer) {
                                ((Instancer.BasicInstancer) instance).timingRebuild();
                            }
                        });

                    });
                } catch (Throwable t) {
                    logger.error("rebuild pool exception ", t);
                }
            }
        }, 0, MiniSearchConfigure.getRebuildTaskInterval(), TimeUnit.SECONDS);

    }

    /**
     * 被调用的方法
     * 相同的 Map<String, Instancer> miniSearchMap 会使用一个定时任务
     *
     * @param miniSearchMap
     * @see com.ck.common.mini.util.MiniSearch
     */
    public static void registerReBuildMap(final Map<String, Instancer> miniSearchMap) {
        synchronized (mapHolder) {
            for (Map.Entry<String, Map<String, Instancer>> mapEntry : mapHolder.entrySet()) {
                if (mapEntry.getValue() == miniSearchMap) {
                    // 存在相等则退出
                    return;
                }
            }
            mapHolder.put(UUID.randomUUID().toString(), miniSearchMap);
        }
    }


}
