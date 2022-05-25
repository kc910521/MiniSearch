package com.ck.common.mini.timing;

import com.ck.common.mini.index.struct.MiniInstance;

/**
 * @Author caikun
 * @Description indexing job action
 * <p>
 * * 不参与集群标识
 * * 定时任务标志
 * * not a clusters node
 * *
 * * @see TimingIndexReBuilder
 * * @see #setRebuildWorker
 * @Date 下午1:36 22-5-20
 **/
public interface IRotateInstance {

    /**
     * 定时器循环调用
     */
    void doRotating();

    /**
     * 给业务方定义重建规则
     */
    @FunctionalInterface
    interface RebuildWorker {

        /**
         * should implemented by user Code,
         * register this method to instance
         *
         * @param instance only work for localization
         */
        void register(MiniInstance instance);


    }
}
