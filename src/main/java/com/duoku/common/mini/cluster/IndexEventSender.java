package com.duoku.common.mini.cluster;

import com.duoku.common.mini.constant.EventType;

/**
 * @Author caikun
 * @Description 索引事件发送器
 * @Date 下午1:52 20-4-24
 **/
public interface IndexEventSender {

    void publish(EventType eventType, String key, Object carrier) throws Exception;

    void setCoreName(String coreName);

    // indexName == instancerName
    void setIndexName(String indexName);
}
