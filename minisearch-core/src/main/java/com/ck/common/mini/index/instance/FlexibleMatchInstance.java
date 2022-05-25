package com.ck.common.mini.index.instance;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.index.struct.*;
import com.ck.common.mini.util.MiniSearch;
import com.ck.common.mini.workshop.nlp.NLPAdmin;
import com.ck.common.mini.workshop.nlp.NLPWorker;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @Author caikun
 * @Description SPI class composer, customize search instance
 * 此为容器装入的顶层实例, 采用组合模式处理搜索实例searchInstance，
 * 和数据变更实例changeInstance。
 * @see MiniSearch#findInstance(java.lang.String)
 *
 * @Date 下午1:54 22-5-20
 **/
public class FlexibleMatchInstance implements MiniInstance {

    private MiniSearchConfigure configure;

    private SpellingDictTree tree;

    private ISearchInstance searchInstance;

    private IChangeInstance changeInstance;

    private NLPWorker nlpWorker;


    public FlexibleMatchInstance(String indexName, MiniSearchConfigure configure) {
        this.init0(configure, new SpellingDictTree(indexName), NLPAdmin.pickBy(configure));
    }

    /**
     * @param configure
     * @param tree
     */
    public FlexibleMatchInstance(MiniSearchConfigure configure, SpellingDictTree tree) {
        this.init0(configure, tree, NLPAdmin.pickBy(configure));
    }

    /**
     * real initData
     *
     * @param configure
     * @param tree
     * @param nlpWorker
     */
    private void init0(MiniSearchConfigure configure, SpellingDictTree tree, NLPWorker nlpWorker) {
        this.configure = configure;
        this.tree = tree;
        this.nlpWorker = nlpWorker;
        // SPI get:
        ServiceLoader<ISearchInstance> searchInstanceSL = ServiceLoader.load(ISearchInstance.class);
        ServiceLoader<IChangeInstance> changeInstanceSL = ServiceLoader.load(IChangeInstance.class);
        this.searchInstance = null;
        this.changeInstance = null;
        searchInstanceSL.forEach(s -> {
            s.setConfig(configure);
            s.setTree(tree);
            s.setNLPWorker(nlpWorker);
            this.searchInstance = s;
        });
        changeInstanceSL.forEach(s -> {
            s.setConfig(configure);
            s.setTree(tree);
            s.setNLPWorker(nlpWorker);
            this.changeInstance = s;
        });
        if (this.searchInstance == null) {
            this.searchInstance = new LocalMatchInstance(this.configure, this.tree, this.nlpWorker);
        }
        if (this.changeInstance == null) {
            this.changeInstance = new LocalIndexInstance(this.configure, this.tree, this.nlpWorker);
        }
        // 准备进行索引激活
        this.changeInstance.activate();
        this.searchInstance.activate();
    }

    @Override
    public void initData(Map<String, Object> data) {
        changeInstance.initData(data);
    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        return changeInstance.addWithId(id, keywords, carrier);
    }

    @Override
    public int add(String keywords, Object carrier) {
        return changeInstance.add(keywords, carrier);
    }

    @Override
    public int add(String keywords) {
        return changeInstance.add(keywords);
    }

    @Override
    public int remove(String keywords) {
        return changeInstance.remove(keywords);
    }

    @Override
    public int removeWithId(String id, String keywords) {
        return changeInstance.removeWithId(id, keywords);
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        return searchInstance.find(keywords);
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize) {
        return searchInstance.find(keywords, page, pageSize);
    }

    @Override
    public <CARRIER> Collection<CARRIER> findByCondition(String keywords, Object condition, int page, int pageSize) {
        return searchInstance.findByCondition(keywords, condition, page, pageSize);
    }

    @Override
    public void printAll() {
        searchInstance.printAll();
    }

    @Override
    public void setConfig(MiniSearchConfigure config) {
        this.configure = config;
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
