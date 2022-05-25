package com.ck.common.mini.index.struct;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.DictTree;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.util.LiteTools;
import com.ck.common.mini.workshop.nlp.NLPWorker;

import java.util.Collection;
import java.util.Collections;

import static com.ck.common.mini.util.LiteTools.beQueue;
import static com.ck.common.mini.util.LiteTools.getPingYin;

/**
 * @Author caikun
 * @Description 本地搜索核心
 * @Date 下午1:46 22-5-20
 **/
public class LocalMatchInstance implements ISearchInstance, GodInstance {

    private MiniSearchConfigure miniSearchConfigure;

    private SpellingDictTree tree;

    private NLPWorker nlpWorker;

    public LocalMatchInstance(MiniSearchConfigure configure, SpellingDictTree tree, NLPWorker nlpWorker) {
        this.miniSearchConfigure = configure;
        this.tree = tree;
        this.nlpWorker = nlpWorker;
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        return this.find(keywords, 0, miniSearchConfigure.getMaxFetchNum());
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize) {
        return this.findByCondition(keywords, null, page, pageSize);
    }

    @Override
    public <CARRIER> Collection<CARRIER> findByCondition(String keywords, Object condition, int page, int pageSize) {
        if (keywords == null || keywords.trim().length() == 0) {
            return Collections.emptySet();
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        return this.tree.fetchSimilar(beQueue(getPingYin(keywords)), catchBigChars(keywords), condition, miniSearchConfigure.isStrict(), page, pageSize);
    }

    @Override
    public void printAll() {
        this.tree.printChild(this.tree.getRoot());
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


    /**
     * 仅搂出中文，可以修改为也搂出其他占位符即可更加精确的匹配
     * todo:国际化内容移出
     *
     * @param keywords
     * @return
     */
    protected char[] catchBigChars(String keywords) {
        char[] chars = keywords.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : chars) {
            if ((c >= 0x4e00) && (c <= 0x9fa5)) {
                // chinese
                stringBuilder.append(c);
            }
        }
        return LiteTools.defUnDupSort(stringBuilder.toString().toCharArray());
    }
}
