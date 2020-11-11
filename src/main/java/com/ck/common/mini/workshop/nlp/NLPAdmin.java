package com.ck.common.mini.workshop.nlp;

import com.ck.common.mini.config.MiniSearchConfigure;

/**
 * @Author caikun
 * @Description NPL管控器
 * @Date 上午10:54 20-11-11
 **/
public class NLPAdmin {

    public static NLPWorker pickBy(MiniSearchConfigure miniSearchConfigure) {
        if (miniSearchConfigure == null) {
            return new LazyWorker();
        }
        if (miniSearchConfigure.isFreeMatch()) {
            return new SubsequentWorker();
        }
        return new LazyWorker();
    }

}
