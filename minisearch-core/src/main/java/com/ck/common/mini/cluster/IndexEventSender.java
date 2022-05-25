package com.ck.common.mini.cluster;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.constant.EventType;
import com.ck.common.mini.util.exception.MiniSearchSuperException;

/**
 * @Author caikun
 * @Description 索引事件发送器
 * @Date 下午1:52 20-4-24
 *
 * 支持单点向集群发送事件通知的功能
 *
 **/
public interface IndexEventSender {

    void publish(EventType eventType, String indexName, String key, Object carrier) throws MiniSearchSuperException;

    void loadMiniSearchConfig(MiniSearchConfigure configure);

}
