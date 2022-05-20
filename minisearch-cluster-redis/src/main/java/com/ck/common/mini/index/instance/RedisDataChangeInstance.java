package com.ck.common.mini.index.instance;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.index.struct.IChangeInstance;

import java.util.Map;

/**
 * @Author caikun
 * @Description //TODO $END
 * <p>
 * 1 publisher 写入
 * 2 继承local
 * 3 回环
 * @Date 下午6:36 22-5-20
 **/
public class RedisDataChangeInstance implements IChangeInstance {

    private

    @Override
    public void init(Map<String, Object> data) {

    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        return 0;
    }

    @Override
    public int add(String keywords, Object carrier) {
        return 0;
    }

    @Override
    public int add(String keywords) {
        return 0;
    }

    @Override
    public int remove(String keywords) {
        return 0;
    }

    @Override
    public int removeWithId(String id, String keywords) {
        return 0;
    }

    @Override
    public void setConfig(MiniSearchConfigure config) {

    }

    @Override
    public void setTree(SpellingDictTree dictTree) {

    }
}
