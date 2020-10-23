package com.ck.common.mini.core;

import java.io.Serializable;

/**
 * @Author caikun
 * @Description 拼音树赋值用
 * @Date 下午5:23 20-4-27
 **/
public class SpellingComponent<CARRIER extends Serializable> implements Serializable {

    private String originKey;

    private CARRIER carrier;

    public SpellingComponent(String originKey, CARRIER carrier) {
        this.originKey = originKey;
        this.carrier = carrier;
    }

    public SpellingComponent() {
    }

    public CARRIER getCarrier() {
        return carrier;
    }

    public void setCarrier(CARRIER carrier) {
        this.carrier = carrier;
    }

    public String getOriginKey() {
        return originKey;
    }

    public void setOriginKey(String originKey) {
        this.originKey = originKey;
    }
}
