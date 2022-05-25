package com.ck.common.mini.cluster;

import com.ck.common.mini.constant.EventType;

import java.io.Serializable;

/**
 * @Author caikun
 * @Description 集群广播内容
 * @Date 下午4:49 20-4-24
 **/
public class Intent<CARRIER> implements Serializable {

    private String indexName;

    private CARRIER carrier;

    /**
     * 消息版本号
     * 和集群内消息对应，数据顺序一致性保证.
     * 也可以设置为二进制，作为消费凭据等
     *
     */
    private long version;

    /**
     * @see EventType
     */
    private String action;

    private String key;

    public Intent(long version) {
        this.version = version;
    }

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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
