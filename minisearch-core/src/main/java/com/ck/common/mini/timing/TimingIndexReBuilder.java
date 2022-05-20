package com.ck.common.mini.timing;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.external.CoreHolder;
import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.index.struct.IExternalInstance;
import com.ck.common.mini.util.MiniSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
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
     * 注册在这个容器的都会被定时反复调用,
     * 一个索引持有一个定时任务。
     *
     * WeakReference
     */
    private static final Map</* indexName */String, /* instance */IRotateInstance.RebuildWorker> jobHolder = new WeakHashMap<>(128);

    /**
     * warn, unlimited queue
     */
    private final static ScheduledThreadPoolExecutor rebuildPool = new ScheduledThreadPoolExecutor(
            MiniSearchConfigure.getuCoreNumber(),
            (Runnable r) -> {
                Thread t = new Thread(r);
                t.setName("| Rebuilding in MiniSearch |");
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    static {
        TimingIndexReBuilder.rebuildPool.scheduleAtFixedRate(() -> {
                    synchronized (rebuildPool) {
                        logger.debug("rebuilding tools actives : {}", rebuildPool.getActiveCount());
                        logger.debug("jobHolder size : {}", jobHolder.size());
                        Set<Map.Entry<String, IRotateInstance.RebuildWorker>> entries = jobHolder.entrySet();
                        for (Map.Entry<String, IRotateInstance.RebuildWorker> entry : entries) {
                            if (entry != null) {
                                // try in block for do not affecting other job
                                IExternalInstance iExternalInstance = CoreHolder.geInstance(entry.getKey());
                                if (iExternalInstance == null) {
                                    throw new MiniSearchException("instance missing");
                                }
                                try {
                                    entry.getValue().register(iExternalInstance);
                                } catch (Throwable t) {
                                    logger.error("mini-search time-job: {} exception ", entry.getKey(), t);
                                }
                            } else {
                                logger.warn("nnnnn");
                            }
                        }
            }

                }, MiniSearchConfigure.getRebuildTaskInterval()
                , MiniSearchConfigure.getRebuildTaskInterval()
                , TimeUnit.SECONDS);

    }

    /**
     * 注册builder到容器
     * @param indexName
     * @param builder
     */
    public static void register(String indexName, IRotateInstance.RebuildWorker builder) {
        synchronized (jobHolder) {
            jobHolder.put(indexName, builder);
        }
    }


}
