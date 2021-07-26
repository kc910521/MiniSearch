package com.ck.common.mini.index;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.timing.RebuildCycler;

import java.util.Collection;
import java.util.Map;

/**
 * @Author caikun
 * @Description
 *
 * 每个索引一个实例，
 * 以多例模式出现
 *
 * @Date 下午3:14 20-4-21
 **/
public interface Instancer {

    void init(Map<String, Object> data);

    <CARRIER> Collection<CARRIER> find(String keywords);

    <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize);

    /**
     * @param id       do if id is null or not
     * @param keywords
     * @param carrier
     * @return
     */
    int addWithId(String id, String keywords, Object carrier);

    int add(String keywords, Object carrier);

    int add(String keywords);

    int remove(String keywords);

    /**
     * @param id       do if id is null or not
     * @param keywords
     * @return
     */
    int removeWithId(String id, String keywords);

    void printAll();

    MiniSearchConfigure getMiniSearchConfigure();

    String getInstancerName();

    /**
     * 不参与集群标识
     * not a clusters node
     * @see RebuildCycler
     *
     */
    interface BasicInstancer {

        /**
         * 定时器调用的方法
         */
        void timingRebuild();

        /**
         * 设定业务方规则
         *
         * @param rebuildWorker
         */
        void setRebuildWorker(RebuildWorker rebuildWorker);

    }

    /**
     * 给业务方定义重建规则
     */
    @FunctionalInterface
    interface RebuildWorker {

        /**
         * implements by biz
         *
         * @param searchInstance
         */
        void doWork(Instancer searchInstance);

    }
}
