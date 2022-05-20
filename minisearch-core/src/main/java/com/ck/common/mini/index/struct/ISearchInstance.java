package com.ck.common.mini.index.struct;

import java.util.Collection;

/**
 * @Author caikun
 * @Description searching action
 * @Date 下午1:34 22-5-20
 **/
public interface ISearchInstance extends GodInstance {

    /**
     * @param keywords  indexing key
     * @param <CARRIER>
     * @return
     */
    <CARRIER> Collection<CARRIER> find(String keywords);

    /**
     * @param keywords
     * @param page
     * @param pageSize
     * @param <CARRIER>
     * @return
     */
    <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize);

    /**
     * @param keywords
     * @param condition fixme:
     * @param page
     * @param pageSize
     * @param <CARRIER>
     * @return
     */
    <CARRIER> Collection<CARRIER> findByCondition(String keywords, Object condition, int page, int pageSize);

    /**
     * for debug only
     */
    void printAll();
}
