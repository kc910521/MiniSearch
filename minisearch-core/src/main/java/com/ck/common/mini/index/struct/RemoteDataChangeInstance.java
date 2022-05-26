package com.ck.common.mini.index.struct;


import com.ck.common.mini.cluster.IndexEventSender;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.util.exception.MiniSearchException;
import com.ck.common.mini.util.exception.MiniSearchSuperException;
import com.ck.common.mini.workshop.nlp.NLPWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * @Author caikun
 * @Description
 * 数据远程发送到中间件，并且更新本地数据,
 * 组合集成：
 * @see LocalIndexInstance
 * 具体的中间件采用SPI设置:
 * @see IndexEventSender
 *
 * @Date 下午6:36 22-5-21
 **/
public class RemoteDataChangeInstance implements IChangeInstance {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDataChangeInstance.class);

    private IChangeInstance localChangeInstance;

    private IndexEventSender eventSender;

    private MiniSearchConfigure miniSearchConfigure;

    private SpellingDictTree tree;

    private NLPWorker nlpWorker;

    @Override
    public void activate() {
        logger.info("RemoteDataChangeInstance local-worker init");
        this.localChangeInstance = new LocalIndexInstance(this.miniSearchConfigure, this.tree, this.nlpWorker);
        logger.info("RemoteDataChangeInstance eventSender init");
        // SPI find indexEventSender
        ServiceLoader<IndexEventSender> sl = ServiceLoader.load(IndexEventSender.class);
        for (IndexEventSender indexEventSender : sl) {
            logger.debug("eventSender here {}", indexEventSender);
            this.eventSender = indexEventSender;
            this.eventSender.loadMiniSearchConfig(this.miniSearchConfigure);
        }
        if (this.eventSender == null) {
            logger.error("no eventSender found, com.ck.common.mini.cluster.IndexEventSender should be set by SPI");
            throw new MiniSearchException("remoting but no eventSender found");
        } else {
            logger.info("RemoteDataChangeInstance all right");
        }
    }

    @Override
    public void initData(Map<String, Object> data) {
        try {
            this.eventSender.publish(EventType.INIT, this.tree.getIndexName(), "initData", data);
        } catch (MiniSearchSuperException e) {
            logger.warn("", e);
        }
        this.localChangeInstance.initData(data);
    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        try {
            this.eventSender.publish(EventType.ADD, this.tree.getIndexName(), keywords, carrier);
        } catch (MiniSearchSuperException e) {
            logger.warn("", e);
        }
        return this.localChangeInstance.addWithId(id, keywords, carrier);
    }

    @Override
    public int add(String keywords, Object carrier) {
        return this.addWithId(null, keywords, carrier);
    }

    @Override
    public int add(String keywords) {
        return this.add(keywords, keywords);
    }

    @Override
    public int remove(String keywords) {
        return this.removeWithId(null, keywords);
    }

    @Override
    public int removeWithId(String id, String keywords) {
        try {
            this.eventSender.publish(EventType.REMOVE, this.tree.getIndexName(), keywords, keywords);
        } catch (MiniSearchSuperException e) {
            logger.warn("", e);
        }
        return this.localChangeInstance.removeWithId(id, keywords);
    }

    // ===== 0

    @Override
    public void setConfig(MiniSearchConfigure config) {
        this.miniSearchConfigure = config;
    }

    @Override
    public void setTree(SpellingDictTree dictTree) {
        this.tree = dictTree;
    }

    @Override
    public void setNLPWorker(NLPWorker nlpWorker) {
        this.nlpWorker = nlpWorker;
    }
}
