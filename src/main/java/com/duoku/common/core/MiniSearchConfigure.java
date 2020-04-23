package com.duoku.common.core;

/**
 * @Author caikun
 * @Description configure
 * @Date 下午2:31 20-4-21
 **/
public class MiniSearchConfigure {

    // 遍历条目时最大返回结果数
    private int maxFetchNum = 10;

    // 仅返回全部匹配的入参结果，false则根据入参从尾向头截取进行匹配
    private boolean strict = true;

    // 推荐关闭，开启则使用KMP去匹配
    private boolean freeMatch = false;

    // 持久化方式
    private int persistence = Persistence.NO.getCode();


    public int getMaxFetchNum() {
        return maxFetchNum;
    }

    public void setMaxFetchNum(int maxFetchNum) {
        this.maxFetchNum = maxFetchNum;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public int getPersistence() {
        return persistence;
    }

    public void setPersistence(int persistence) {
        this.persistence = persistence;
    }

    enum Persistence {

        NO(0),

        REDIS(1),

        MONGO(2),

        FILE(3),

        ;

        private int code;

        private Persistence(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
