package com.duoku.common.factory;

import com.duoku.common.core.DictTree;
import com.duoku.common.core.TreeConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午2:45 20-4-21
 **/
public class SimpleInstancer implements Instancer {

    private static final Logger logger = LoggerFactory.getLogger(SimpleInstancer.class);

    private DictTree dictTree = null;

    private TreeConfigure treeConfigure = null;

    public SimpleInstancer() {
        this.treeConfigure = new TreeConfigure();
        this.dictTree = new DictTree(treeConfigure);
    }

    public void init(Map<String, Object> data) {
        this.dictTree.clear();
        Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            add(next.getKey(), next.getValue());
        }
        logger.info("init success");
        this.dictTree.printAll(this.dictTree.getRoot());
    }

    public Collection<String> find(String keywords) {
        return this.dictTree.fetchSimilar(keywords);
    }

    public int add(String keywords, Object carrier) {
        return this.dictTree.insert(keywords, carrier);
    }

    /**
     * insert keywords and setting keywords as carrier
     *
     * @param keywords
     * @return
     */
    public int add(String keywords) {
        return this.dictTree.insert(keywords, keywords);
    }

    @Override
    public int remove(String keywords) {
        throw new RuntimeException("not supported yet.");
    }


}
