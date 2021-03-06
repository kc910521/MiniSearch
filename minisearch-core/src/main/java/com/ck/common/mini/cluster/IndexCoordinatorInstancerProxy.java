package com.ck.common.mini.cluster;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.index.Instancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @Author caikun
 * @Description
 * 索引协调器，处理集群环境索引同步
 * 代理基础instancer
 *
 * @Date 上午11:29 20-4-24
 **/
public class IndexCoordinatorInstancerProxy implements Instancer {

    private Instancer instancer;

    private IndexEventSender indexEventSender;

    private static final Logger logger = LoggerFactory.getLogger(IndexCoordinatorInstancerProxy.class);

    public IndexCoordinatorInstancerProxy(Instancer instancer) {
        this.instancer = instancer;
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
            indexEventSender.publish(EventType.INIT, instancer.getInstancerName(), "init", data);
        } catch (Exception e) {
            logger.error("due to {}, standalone update only.", e.toString(), e);
            this.instancer.init(data);
        }
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        return this.find(keywords, 0, getMiniSearchConfigure().getMaxFetchNum());
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize) {
        Collection<CARRIER> objects = this.instancer.find(keywords, page, pageSize);
        return objects;
    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        try {
            indexEventSender.publish(EventType.ADD, instancer.getInstancerName(), keywords, carrier);
            return 1;
        } catch (Exception e) {
            logger.error("due to {}, standalone adding only.", e.toString(), e);
            this.instancer.addWithId(id, keywords, carrier);
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
            indexEventSender.publish(EventType.REMOVE, instancer.getInstancerName(), keywords, keywords);
            return 1;
        } catch (Exception e) {
            logger.error("due to {}, standalone removing only.", e.toString(), e);
            this.instancer.removeWithId(id, keywords);
        }
        return 0;
    }

    @Override
    public void printAll() {
        this.instancer.printAll();
    }

    @Override
    public MiniSearchConfigure getMiniSearchConfigure() {
        return this.instancer.getMiniSearchConfigure();
    }

    @Override
    public String getInstancerName() {
        return this.instancer.getInstancerName();
    }


}
