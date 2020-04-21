package com.duoku.common.core;

/**
 * @Author caikun
 * @Description configure
 * @Date 下午2:31 20-4-21
 **/
public class TreeConfigure {

    // 遍历条目时最大返回结果数
    private int maxFetchNum = 5;

    // 仅返回全部匹配的入参结果，否则根据入参从尾向头截取进行匹配
    private boolean fullMatch = true;


    public int getMaxFetchNum() {
        return maxFetchNum;
    }

    public void setMaxFetchNum(int maxFetchNum) {
        this.maxFetchNum = maxFetchNum;
    }

    public boolean isFullMatch() {
        return fullMatch;
    }

    public void setFullMatch(boolean fullMatch) {
        this.fullMatch = fullMatch;
    }
}
