package com.ck.common.mini.index;

/**
 * @Author caikun
 * @Description 集群用索引接口
 * @Date 下午5:02 21-7-29
 **/
public interface ClusterIndexInstance extends IndexInstance {

    /**
     * 获取本地实例
     *
     * @return
     */
    LocalIndexInstance getLocalInstance();

}
