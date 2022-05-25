package com.ck.common.mini.index.struct;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.DictTree;
import com.ck.common.mini.core.SpellingComponent;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.util.LiteTools;
import com.ck.common.mini.workshop.nlp.NLPWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.ck.common.mini.util.LiteTools.beQueue;
import static com.ck.common.mini.util.LiteTools.getPingYin;

/**
 * @Author caikun
 * @Description
 * @Date 下午1:52 22-5-20
 **/
public class LocalIndexInstance implements IChangeInstance {

    private static final Logger logger = LoggerFactory.getLogger(LocalIndexInstance.class);

    private MiniSearchConfigure miniSearchConfigure;

    private SpellingDictTree tree;

    private NLPWorker nlpWorker;

    public LocalIndexInstance() {
    }


    public LocalIndexInstance(MiniSearchConfigure configure, SpellingDictTree tree, NLPWorker nlpWorker) {
        this.miniSearchConfigure = configure;
        this.tree = tree;
        this.nlpWorker = nlpWorker;
    }

    @Override
    public void initData(Map<String, Object> data) {
        this.tree.clear();
        Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            add(next.getKey(), next.getValue());
        }
        logger.info("initData success");
    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        if (!(carrier instanceof Serializable)) {
            System.err.println("The carrier is not a struct of Serializable");
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        if (keywords == null || "".equals(keywords.trim())) {
            return -1;
        }
        SpellingComponent spellingComponent = new SpellingComponent(keywords, (Serializable) carrier);
        if (id != null) {
            spellingComponent.setId(id);
        }
        int rs = 0;
        List<String> subKeywords = nlpWorker.work(keywords);
        for (String kw : subKeywords) {
            rs += this.tree.insert(beQueue(getPingYin(kw)), spellingComponent);
        }
        return rs;
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
        return this.removeWithId(null, keywords);
    }

    @Override
    public int removeWithId(String id, String keywords) {
        List<String> subKeywords = nlpWorker.work(keywords);
        SpellingComponent spellingComponent = new SpellingComponent(keywords);
        if (id != null) {
            spellingComponent.setId(id);
        }
        int i = 0;
        for (String kw : subKeywords) {
            i += this.tree.removeToLastTail(beQueue(getPingYin(kw)), this.tree.getRoot(), spellingComponent);
        }
        return i;
    }

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
