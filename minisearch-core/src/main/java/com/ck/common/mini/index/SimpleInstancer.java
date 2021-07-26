package com.ck.common.mini.index;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.DictTree;
import com.ck.common.mini.core.SpellingComponent;
import com.ck.common.mini.workshop.nlp.NLPAdmin;
import com.ck.common.mini.workshop.nlp.NLPWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static com.ck.common.mini.util.LiteTools.beQueue;

/**
 * @Author caikun
 * @Description
 * 搜索实例
 *
 * @Date 下午2:45 20-4-21
 **/
public class SimpleInstancer implements Instancer, Instancer.BasicInstancer {

    private static final Logger logger = LoggerFactory.getLogger(SimpleInstancer.class);

    private DictTree dictTree;

    private String instancerName;

    private MiniSearchConfigure miniSearchConfigure = null;

    private NLPWorker nlpWorker;

    private RebuildWorker rebuildWorker;

    public SimpleInstancer(String instancerName) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = new MiniSearchConfigure();
        this.dictTree = new DictTree();
        this.nlpWorker = NLPAdmin.pickBy(this.miniSearchConfigure);
    }

    public SimpleInstancer(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = miniSearchConfigure;
        this.dictTree = new DictTree();
        this.nlpWorker = NLPAdmin.pickBy(this.miniSearchConfigure);
    }

    @Override
    public synchronized void init(Map<String, Object> data) {
        this.dictTree.clear();
        if (data != null) {
            Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                add(next.getKey(), next.getValue());
            }
        }
        logger.info("init success");
    }

    @Override
    public synchronized <CARRIER> Collection<CARRIER> find(String keywords) {
        return this.find(keywords, 0, miniSearchConfigure.getMaxFetchNum());
    }

    @Override
    public synchronized <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize) {
        if (keywords == null || keywords.trim().length() == 0) {
            return Collections.emptySet();
        }
        if (pageSize > miniSearchConfigure.getMaxFetchNum()) {
            throw new RuntimeException("out of max fetch number in Config");
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        return this.dictTree.fetchSimilar(beQueue(keywords), miniSearchConfigure.isStrict(), page, pageSize);
    }

    @Override
    @Deprecated
    public synchronized int addWithId(String id, String keywords, Object carrier) {
        if (!(carrier instanceof Serializable)) {
            System.err.println("The carrier is not a instance of Serializable");
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        if (keywords == null || "".equals(keywords.trim())) {
            return -1;
        }
        Serializable ser = (Serializable) carrier;
        int rs = 0;
        List<String> subKeywords = nlpWorker.work(keywords);
        SpellingComponent spellingComponent = new SpellingComponent(keywords, ser);
        if (id != null) {
            spellingComponent.setId(id);
        }
        for (String kw : subKeywords) {
            rs += this.dictTree.insert(beQueue(kw), spellingComponent);
        }
        return rs;
    }

    @Override
    public synchronized int add(String keywords, Object carrier) {
        return addWithId(null, keywords, carrier);
    }

    /**
     * insert keywords and setting keywords as carrier
     *
     * @param keywords
     * @return
     */
    @Override
    public synchronized int add(String keywords) {
        return this.add(keywords, keywords);
    }

    @Override
    public synchronized int remove(String keywords) {
        return removeWithId(null, keywords);
    }

    @Override
    @Deprecated
    public synchronized int removeWithId(String id, String keywords) {
        List<String> subKeywords = nlpWorker.work(keywords);
        int rs = 0;
        SpellingComponent spellingComponent = new SpellingComponent(keywords);
        if (id != null) {
            spellingComponent.setId(id);
        }
        for (String kw : subKeywords) {
            rs += this.dictTree.removeToLastTail(beQueue(kw), this.dictTree.getRoot(), spellingComponent);
        }
        return rs;
    }

    @Override
    public synchronized void printAll() {
        this.dictTree.printChild(this.dictTree.getRoot());
    }

    @Override
    public MiniSearchConfigure getMiniSearchConfigure() {
        return miniSearchConfigure;
    }


    @Override
    public String getInstancerName() {
        return instancerName;
    }

    @Override
    public void timingRebuild() {
        if (this.rebuildWorker != null) {
            this.rebuildWorker.doWork(this);
        }
    }

    @Override
    public void setRebuildWorker(RebuildWorker rebuildWorker) {
        this.rebuildWorker = rebuildWorker;
    }
}
