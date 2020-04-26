package com.duoku.common.mini.config;

/**
 * @Author caikun
 * @Description
 * 全局总配置
 * @Date 下午2:31 20-4-21
 **/
public class MiniSearchConfigure {

    // 遍历条目时最大返回结果数
    private int maxFetchNum = Integer.MAX_VALUE;

    // 仅返回全部匹配的入参结果，false则根据入参从尾向头截取进行匹配
    private boolean strict = true;

    // 推荐关闭，开启则使用KMP去匹配树头
    private boolean freeMatch = false;

    // 构建和搜索时忽略所有特殊字符
    private boolean ignoreSymbol = true;

    // 设置忽略的正则表达式，同 @ignoreSymbol 合用
    private String symbolPattern = "[\\pP\\pS\\pZ]";

    // 集群化通知标识前缀,后接 实例（index）名
    private String notifyPatternChars = "search:notify:core:instancer:";

    // 持久化方式
    private int persistence = Persistence.NO.getCode();

    // 集群容器线程池
    private int clusterContainerPoolSize = 10;

    public int getClusterContainerPoolSize() {
        return clusterContainerPoolSize;
    }

    public void setClusterContainerPoolSize(int clusterContainerPoolSize) {
        this.clusterContainerPoolSize = clusterContainerPoolSize;
    }

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

    public boolean isFreeMatch() {
        return freeMatch;
    }

    public void setFreeMatch(boolean freeMatch) {
        this.freeMatch = freeMatch;
    }

    public boolean isIgnoreSymbol() {
        return ignoreSymbol;
    }

    public void setIgnoreSymbol(boolean ignoreSymbol) {
        this.ignoreSymbol = ignoreSymbol;
    }

    public String getSymbolPattern() {
        return symbolPattern;
    }

    public void setSymbolPattern(String symbolPattern) {
        this.symbolPattern = symbolPattern;
    }

    public String getNotifyPatternChars() {
        return notifyPatternChars;
    }

    public void setNotifyPatternChars(String notifyPatternChars) {
        this.notifyPatternChars = notifyPatternChars;
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
