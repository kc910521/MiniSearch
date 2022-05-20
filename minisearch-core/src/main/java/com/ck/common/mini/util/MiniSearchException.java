package com.ck.common.mini.util;

/**
 * @Author caikun
 * @Description 异常
 * @Date 下午5:33 22-5-20
 **/
public class MiniSearchException extends RuntimeException {

    public MiniSearchException(String message) {
        super(message);
    }

    public MiniSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
