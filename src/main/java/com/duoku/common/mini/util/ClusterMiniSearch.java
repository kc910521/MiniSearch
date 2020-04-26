package com.duoku.common.mini.util;

import com.duoku.common.mini.config.MiniSearchConfigure;
import com.duoku.common.mini.cluster.IndexCoordinatorInstancerProxy;
import com.duoku.common.mini.cluster.IndexEventSender;
import com.duoku.common.mini.factory.Instancer;
import com.duoku.common.mini.factory.SimpleInstancer;

/**
 * @Author caikun
 * @Description 集群用入口
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

    public static Instancer findInstance(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        if (!instancerMap.containsKey(instancerName)) {
            Instancer instancer = instancer(instancerName, miniSearchConfigure);
            instancerMap.put(instancerName, instancer);
        }
        return new IndexCoordinatorInstancerProxy(instancerMap.get(instancerName));
    }
}
