package com.duoku.common.mini.factory;

import com.duoku.common.mini.config.MiniSearchConfigure;

import java.util.Collection;
import java.util.Map;

/**
 * @Author caikun
 * @Description
 *
 * 每个索引一个实例，
 * 以多例模式出现
 *
 * @Date 下午3:14 20-4-21
 **/
public interface Instancer {

    void init(Map<String, Object> data);

    <CARRIER> Collection<CARRIER> find(String keywords);

    int add(String keywords, Object carrier);

    int add(String keywords);

    int remove(String keywords);

    void printAll();

    MiniSearchConfigure getMiniSearchConfigure();

    String getInstancerName();

    /**
     * 不参与集群标识
     */
    interface BasicInstancer {

    }
}
