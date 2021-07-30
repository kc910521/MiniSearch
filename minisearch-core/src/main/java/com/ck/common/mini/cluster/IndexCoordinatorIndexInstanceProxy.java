package com.ck.common.mini.cluster;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.index.ClusterIndexInstance;
import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.index.LocalIndexInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @Author caikun
 * @Description 索引协调器，处理集群环境索引同步
 * 代理基础instancer
 * IndexEventSender 由各个集群支持包自行实现
 * @Date 上午11:29 20-4-24
 * @see IndexEventSender
 **/
public class IndexCoordinatorIndexInstanceProxy implements ClusterIndexInstance, IndexInstance.TimingLocalReindex {

    private LocalIndexInstance localRealInstance;

    private IndexEventSender indexEventSender;

    private RebuildWorker rebuildWorker;

    private static final Logger logger = LoggerFactory.getLogger(IndexCoordinatorIndexInstanceProxy.class);

    public IndexCoordinatorIndexInstanceProxy(IndexInstance localRealInstance) {
        this.localRealInstance = (LocalIndexInstance) localRealInstance;
        // SPI find indexEventSender
        ServiceLoader<IndexEventSender> sl = ServiceLoader.load(IndexEventSender.class);
        for (IndexEventSender indexEventSender : sl) {
            this.indexEventSender = indexEventSender;
        }
        if (this.indexEventSender == null) {
            logger.warn("not indexEventSender found, standalone by default");
        }
    }

    @Override
    public void init(Map<String, Object> data) {
        try {
            indexEventSender.publish(EventType.INIT, localRealInstance.getInstanceName(), "init", data);
        } catch (Exception e) {
            logger.warn(" {}, standalone update only.", e.toString());
            getLocalInstance().init(data);
        }
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        return this.find(keywords, 0, getMiniSearchConfigure().getMaxFetchNum());
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize) {
        Collection<CARRIER> objects = getLocalInstance().find(keywords, page, pageSize);
        return objects;
    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        try {
            indexEventSender.publish(EventType.ADD, localRealInstance.getInstanceName(), keywords, carrier);
            return 1;
        } catch (Exception e) {
            logger.warn(" {}, standalone adding only.", e.toString());
            getLocalInstance().addWithId(id, keywords, carrier);
        }
        return 0;
    }

    @Override
    public int add(String keywords, Object carrier) {
        return addWithId(null, keywords, carrier);
    }

    @Override
    public int add(String keywords) {
        return this.add(keywords, keywords);
    }

    @Override
    public int remove(String keywords) {
        return removeWithId(null, keywords);
    }

    @Override
    public int removeWithId(String id, String keywords) {
        try {
            indexEventSender.publish(EventType.REMOVE, localRealInstance.getInstanceName(), keywords, keywords);
            return 1;
        } catch (Exception e) {
            logger.warn(" {}, standalone removing only.", e.toString());
            getLocalInstance().removeWithId(id, keywords);
        }
        return 0;
    }

    @Override
    public void printAll() {
        getLocalInstance().printAll();
    }

    @Override
    public MiniSearchConfigure getMiniSearchConfigure() {
        return getLocalInstance().getMiniSearchConfigure();
    }

    @Override
    public String getInstanceName() {
        return getLocalInstance().getInstanceName();
    }

    /**
     * rebuild work 还是设置到代理上
     *
     * @param rebuildWorker
     */
    @Override
    public void setRebuildWorker(RebuildWorker rebuildWorker) {
        this.rebuildWorker = rebuildWorker;
    }

    /**
     * 执行本地的rebuild
     *
     * @see #setRebuildWorker
     */
    @Override
    public void reindexing() {
        if (this.rebuildWorker != null) {
            // always do batch in local
            this.rebuildWorker.doWork(getLocalInstance());
        }
    }


    @Override
    public LocalIndexInstance getLocalInstance() {
        return this.localRealInstance;
    }
}
