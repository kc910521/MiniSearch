package com.ck.common.mini.cluster;

import java.io.Serializable;

/**
 * @Author caikun
 * @Description 集群广播内容
 * @Date 下午4:49 20-4-24
 **/
public class Intent<CARRIER> implements Serializable {

    private String indexName;

    private CARRIER carrier;

    // #EventType
    private String action;

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public CARRIER getCarrier() {
        return carrier;
    }

    public void setCarrier(CARRIER carrier) {
        this.carrier = carrier;
    }
}
