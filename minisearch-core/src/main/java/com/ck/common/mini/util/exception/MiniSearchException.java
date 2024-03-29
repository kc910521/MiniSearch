package com.ck.common.mini.util.exception;

/**
 * @Author caikun
 * @Description 运行时异常
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
