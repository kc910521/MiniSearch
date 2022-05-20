package com.ck.common.mini.index.struct;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.DictTree;
import com.ck.common.mini.core.SpellingDictTree;

/**
 * @Author caikun
 * @Description common struct for customizing
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


}
