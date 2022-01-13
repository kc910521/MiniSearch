package com.ck.common.mini.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author caikun
 * @Description
 * 统一响应体
 *
 * @Date 下午5:10 21-1-27
 **/
public class ResponseWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private int ts;

    private int result;

    private Collection<Object> data;

    public ResponseWrapper() {
        this.data = new ArrayList<>();
    }

    public ResponseWrapper(Collection<Object> data) {
        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }
        this.result = data.size();
    }


    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public Collection<Object> getData() {
        return data;
    }

    public void setData(Collection<Object> data) {
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
