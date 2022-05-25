package com.ck.common.mini.index.struct;

import java.util.Map;

/**
 * @Author caikun
 * @Description putting data action
 * @Date 下午1:35 22-5-20
 **/
public interface IChangeInstance extends GodInstance {

    /**
     * batch insert
     *
     * @param data
     */
    void initData(Map<String, Object> data);

    /**
     * @param id       do if id is null or not
     * @param keywords
     * @param carrier
     * @return
     */
    int addWithId(String id, String keywords, Object carrier);

    int add(String keywords, Object carrier);

    int add(String keywords);

    int remove(String keywords);

    /**
     * @param id       do if id is null or not
     * @param keywords
     * @return
     */
    int removeWithId(String id, String keywords);

}
