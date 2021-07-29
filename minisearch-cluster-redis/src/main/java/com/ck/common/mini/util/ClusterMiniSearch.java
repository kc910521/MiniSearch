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
        Instancer instance = MiniSearch.findInstance(instancerName);
        MiniSearch.enableRebuild();
        return new IndexCoordinatorInstancerProxy(instance);
    }

    /**
     * use MiniSearchConfigure bean plz
     *
     * @param instancerName
     * @param miniSearchConfigure
     * @return
     */
    public static Instancer findInstance(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        Instancer instance = MiniSearch.findInstance(instancerName, miniSearchConfigure);
        MiniSearch.enableRebuild();
        return new IndexCoordinatorInstancerProxy(instance);
    }
}
