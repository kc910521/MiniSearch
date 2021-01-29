package com.ck.common.mini.core;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Author caikun
 * @Description 拼写组建，保存承载内容和原始key
 * @Date 下午5:23 20-4-27
 **/
public class SpellingComponent<CARRIER> implements Serializable {

    private String id = "19910521078e4ed7802aa6cba4b8f259";

    private String originKey;

    private CARRIER originCarrier;

    public SpellingComponent(String originKey, CARRIER originCarrier) {
        this.originKey = originKey;
        this.originCarrier = originCarrier;
    }

    public SpellingComponent(String id, String originKey, CARRIER originCarrier) {
        this.id = id;
        this.originKey = originKey;
        this.originCarrier = originCarrier;
    }

    public SpellingComponent(String originKey) {
        this.originKey = originKey;
    }

    public SpellingComponent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CARRIER getOriginCarrier() {
        return originCarrier;
    }

    public void setOriginCarrier(CARRIER originCarrier) {
        this.originCarrier = originCarrier;
    }

    public String getOriginKey() {
        return originKey;
    }

    public void setOriginKey(String originKey) {
        this.originKey = originKey;
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
    }
}
