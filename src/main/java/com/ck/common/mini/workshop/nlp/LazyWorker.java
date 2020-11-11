package com.ck.common.mini.workshop.nlp;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author caikun
 * @Description 不做工作
 * @Date 上午10:24 20-11-11
 **/
public class LazyWorker implements NLPWorker {
    @Override
    public ArrayList<String> work(String origin) {
        return new ArrayList<String>(Arrays.asList(origin));
    }
}
