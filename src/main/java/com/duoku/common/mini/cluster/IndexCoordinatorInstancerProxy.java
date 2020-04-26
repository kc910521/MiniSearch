package com.duoku.common.mini.cluster;

import com.duoku.common.mini.cluster.redis.RedisIndexCoordinateSender;
import com.duoku.common.mini.constant.EventType;
import com.duoku.common.mini.config.MiniSearchConfigure;
import com.duoku.common.mini.factory.Instancer;
import com.duoku.common.mini.spring.MiniSearchSpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @Author caikun
 * @Description
 * 索引协调器，处理集群环境索引同步
 *
 * @Date 上午11:29 20-4-24
 **/
public class IndexCoordinatorInstancerProxy implements Instancer {

    private Instancer instancer;

    private IndexEventSender indexEventSender;

    private static final Logger logger = LoggerFactory.getLogger(IndexCoordinatorInstancerProxy.class);

    public IndexCoordinatorInstancerProxy(Instancer instancer) {
        this.instancer = instancer;
        try {
            this.indexEventSender = MiniSearchSpringUtil.getBean(IndexEventSender.class);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        if (this.indexEventSender == null) {
            logger.warn("not indexEventSender found, by default");
            this.indexEventSender = new RedisIndexCoordinateSender();
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
        Collection<CARRIER> objects = this.instancer.find(keywords);
        return objects;
    }

    @Override
    public int add(String keywords, Object carrier) {
        try {
            indexEventSender.publish(EventType.ADD, instancer.getInstancerName(), keywords, carrier);
            return 1;
        } catch (Exception e) {
            logger.error("due to {}, standalone adding only.", e.toString(), e);
            this.instancer.add(keywords, carrier);// 1 ok
        }
        return 0;
    }

    @Override
    public int add(String keywords) {
        try {
            indexEventSender.publish(EventType.ADD, instancer.getInstancerName(), keywords, keywords);
            return 1;
        } catch (Exception e) {
            logger.error("due to {}, standalone adding only.", e.toString(), e);
            this.instancer.add(keywords);// 1 ok
        }
        return 0;
    }

    @Override
    public int remove(String keywords) {
        try {
            indexEventSender.publish(EventType.REMOVE, instancer.getInstancerName(), keywords, keywords);
            return 1;
        } catch (Exception e) {
            logger.error("due to {}, standalone removing only.", e.toString(), e);
            this.instancer.remove(keywords);
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
