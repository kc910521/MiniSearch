package com.duoku.common.mini.util;

import com.duoku.common.mini.config.MiniSearchConfigure;
import com.duoku.common.mini.cluster.IndexCoordinatorInstancer;
import com.duoku.common.mini.cluster.IndexEventSender;
import com.duoku.common.mini.factory.Instancer;
import com.duoku.common.mini.factory.SimpleInstancer;

/**
 * @Author caikun
 * @Description 集群用入口
 * @Date 下午6:55 20-4-24
 **/
public class ClusterMiniSearch extends MiniSearch {

    private static IndexEventSender indexEventSender;

    protected static synchronized Instancer instancer(String instancerName) {
        return new IndexCoordinatorInstancer(new SimpleInstancer(instancerName), indexEventSender);
    }

    protected static synchronized Instancer instancer(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        return new IndexCoordinatorInstancer(new SimpleInstancer(instancerName, miniSearchConfigure), indexEventSender);
    }

    public IndexEventSender getIndexEventSender() {
        return indexEventSender;
    }
}
