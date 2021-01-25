package com.ck.common.mini.util;

import com.ck.common.mini.cluster.IndexCoordinatorInstancerProxy;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.Instancer;

/**
 * @Author caikun
 * @Description
 * MiniSearch集群用入口
 *
 * @Date 下午6:55 20-4-24
 **/
public class ClusterMiniSearch extends MiniSearch {


    public static Instancer findInstance(String instancerName) {
        if (!instancerMap.containsKey(instancerName)) {
            Instancer instancer = instancer(instancerName);
            instancerMap.put(instancerName, instancer);
        }
        return new IndexCoordinatorInstancerProxy(instancerMap.get(instancerName));
    }

    /**
     * use MiniSearchConfigure bean plz
     *
     * @param instancerName
     * @param miniSearchConfigure
     * @return
     */
    @Deprecated
    public static Instancer findInstance(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        if (!instancerMap.containsKey(instancerName)) {
            Instancer instancer = instancer(instancerName, miniSearchConfigure);
            instancerMap.put(instancerName, instancer);
        }
        return new IndexCoordinatorInstancerProxy(instancerMap.get(instancerName));
    }
}
