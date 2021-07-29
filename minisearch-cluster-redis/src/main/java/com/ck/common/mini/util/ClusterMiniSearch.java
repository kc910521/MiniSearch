package com.ck.common.mini.util;

import com.ck.common.mini.cluster.IndexCoordinatorIndexInstanceProxy;
import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.IndexInstance;

/**
 * @Author caikun
 * @Description
 * MiniSearch集群用入口
 *
 * @Date 下午6:55 20-4-24
 **/
public class ClusterMiniSearch extends MiniSearch {


    public static IndexInstance findInstance(String instancerName) {
        IndexInstance instance = MiniSearch.findInstance(instancerName);
        MiniSearch.enableRebuild();
        return new IndexCoordinatorIndexInstanceProxy(instance);
    }

    /**
     * use MiniSearchConfigure bean plz
     *
     * @param instancerName
     * @param miniSearchConfigure
     * @return
     */
    public static IndexInstance findInstance(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        IndexInstance instance = MiniSearch.findInstance(instancerName, miniSearchConfigure);
        MiniSearch.enableRebuild();
        return new IndexCoordinatorIndexInstanceProxy(instance);
    }
}
