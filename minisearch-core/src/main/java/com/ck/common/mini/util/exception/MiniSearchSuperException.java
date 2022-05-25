package com.ck.common.mini.util.exception;

/**
 * @Author caikun
 * @Description
 * 超级错误，需要捕获
 *
 * @Date 下午4:01 22-5-25
 **/
public class MiniSearchSuperException extends Exception {

    public MiniSearchSuperException() {
    }

    public MiniSearchSuperException(String s) {
        super(s);
    }

    public MiniSearchSuperException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
