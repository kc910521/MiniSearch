package com.ck.common.mini.index.struct;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.DictTree;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.workshop.nlp.NLPWorker;

/**
 * @Author caikun
 * @Description base construct for customizing
 * @Date 下午1:30 22-5-20
 **/
public interface GodInstance {

    /**
     * config applying
     *
     * @param config
     */
    void setConfig(MiniSearchConfigure config);

    /**
     * basic structure setting
     *
     * @param dictTree
     */
    void setTree(SpellingDictTree dictTree);

    /**
     * nlp worker setting
     * @param nlpWorker
     */
    void setNLPWorker(NLPWorker nlpWorker);
    /**
     * after finish setting config\tree\nlpWorker,
     * instance will be activated.
     * in fact, it's a post-init-method
     *
     */
    default void activate() {
        // do nothing by default
    }


}
