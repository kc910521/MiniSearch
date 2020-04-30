package com.duoku.common.mini.index;

import com.duoku.common.mini.config.MiniSearchConfigure;
import com.duoku.common.mini.core.SpellingComponent;
import com.duoku.common.mini.core.SpellingDictTree;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static com.duoku.common.mini.util.LiteTools.beQueue;
import static com.duoku.common.mini.util.LiteTools.getPingYin;

/**
 * @Author caikun
 * @Description 针对中文的，优化过的搜索实例
 * @Date 上午11:49 20-4-28
 **/
public class PinYinInstancer implements Instancer, Instancer.BasicInstancer {

    private static final Logger logger = LoggerFactory.getLogger(PinYinInstancer.class);

    private SpellingDictTree spellingDictTree;

    private String instancerName;

    private MiniSearchConfigure miniSearchConfigure;

    public PinYinInstancer(String instancerName) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = new MiniSearchConfigure();
        this.spellingDictTree = new SpellingDictTree(miniSearchConfigure);
    }

    public PinYinInstancer(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = miniSearchConfigure;
        this.spellingDictTree = new SpellingDictTree(miniSearchConfigure);
    }

    @Override
    public void init(Map<String, Object> data) {
        this.spellingDictTree.clear();
        Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            add(next.getKey(), next.getValue());
        }
        logger.info("init success");
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        if (keywords == null || keywords.trim().length() == 0) {
            return Collections.emptySet();
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        return this.spellingDictTree.fetchSimilar(beQueue(getPingYin(keywords)), catchPattern(keywords));
    }

    private static final String PT_PREFIX = "^";
    private static final String PT_AMPLE_ONE_AT_LEAST = "(.+)";
    private static final String PT_AMPLE_ANY = "(.*)"; // null accept

    protected String catchPattern(String keywords) {
        char[] chars = keywords.toCharArray();
        List<String> stringList = new LinkedList<>();
        stringList.add("^");
        for (char c : chars) {
            // 中文直接追加
            // 英文判断上个节点是否为 PT_AMPLE_ONE_AT_LEAST 是continue 否则加入
            if ((c >= 0x4e00) && (c <= 0x9fa5)) {
                // chinese
                stringList.add(String.valueOf(c));
            } else {
                String last = ((LinkedList<String>) stringList).getLast();
                if (last.equals(PT_AMPLE_ONE_AT_LEAST)) {

                } else {
                    stringList.add(PT_AMPLE_ONE_AT_LEAST);
                }
            }
        }
        stringList.add(PT_AMPLE_ANY);

        return Joiner.on("").join(stringList);
    }

    //
    @Override
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
        return this.spellingDictTree.insert(beQueue(getPingYin(keywords)), new SpellingComponent(keywords, (Serializable) carrier));
    }

    @Override
    public int add(String keywords) {
        return this.add(keywords, keywords);
    }

    @Override
    public int remove(String keywords) {
        return this.spellingDictTree.removeToLastTail(beQueue(getPingYin(keywords)), this.spellingDictTree.getRoot(), keywords);
    }

    @Override
    public void printAll() {
        this.spellingDictTree.printChild(this.spellingDictTree.getRoot());
    }

    @Override
    public MiniSearchConfigure getMiniSearchConfigure() {
        return miniSearchConfigure;
    }

    @Override
    public String getInstancerName() {
        return instancerName;
    }
}
