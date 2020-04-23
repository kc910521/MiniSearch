package com.duoku.common.factory;

import com.duoku.common.core.DictTree;

import java.util.Collection;
import java.util.Map;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午3:14 20-4-21
 **/
public interface Instancer {

    void init(Map<String, Object> data);

    <CARRIER> Collection<CARRIER> find(String keywords);

    int add(String keywords, Object carrier);

    int remove(String keywords);

    void printAll();

}
