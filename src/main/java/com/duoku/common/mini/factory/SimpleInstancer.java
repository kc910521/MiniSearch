package com.duoku.common.mini.factory;

import com.duoku.common.mini.core.DictTree;
import com.duoku.common.mini.config.MiniSearchConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

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

    public SimpleInstancer(String instancerName) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = new MiniSearchConfigure();
        this.dictTree = new DictTree(miniSearchConfigure);
    }

    public SimpleInstancer(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = miniSearchConfigure;
        this.dictTree = new DictTree(miniSearchConfigure);
    }

    public void init(Map<String, Object> data) {
        this.dictTree.clear();
        Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            add(next.getKey(), next.getValue());
        }
        logger.info("init success");
    }

    public <CARRIER> Collection<CARRIER> find(String keywords) {
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        return this.dictTree.fetchSimilar(beQueue(keywords));
    }

    public int add(String keywords, Object carrier) {
        if (!(carrier instanceof Serializable)) {
            System.err.println("The carrier is not a instance of Serializable");
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        if (keywords == null || "".equals(keywords.trim())) {
            return -1;
        }
        return this.dictTree.insert(beQueue(keywords), (Serializable) carrier);
    }

    /**
     * insert keywords and setting keywords as carrier
     *
     * @param keywords
     * @return
     */
    public int add(String keywords) {
        return this.add(keywords, keywords);
    }

    @Override
    public int remove(String keywords) {
        return this.dictTree.removeToLastTail(beQueue(keywords), this.dictTree.getRoot());
    }

    @Override
    public void printAll() {
        this.dictTree.printChild(this.dictTree.getRoot());
    }

    public MiniSearchConfigure getMiniSearchConfigure() {
        return miniSearchConfigure;
    }

    public final static Queue beQueue(String keywords) {
        char[] chars = keywords.toCharArray();
        Queue<Character> cq = new LinkedList<>();
        for (int i = 0; i < chars.length; i++) {
            cq.offer(chars[i]);
        }
        return cq;
    }

    @Override
    public String getInstancerName() {
        return instancerName;
    }
}
