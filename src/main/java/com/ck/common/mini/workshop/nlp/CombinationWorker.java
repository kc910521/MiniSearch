package com.ck.common.mini.workshop.nlp;

import com.ck.common.mini.util.LiteTools;

import java.util.ArrayList;

/**
 * @Author caikun
 * @Description 组合工人
 * @Date 上午10:29 20-11-11
 **/
public class CombinationWorker implements NLPWorker {
    @Override
    public ArrayList<String> work(String origin) {
        return LiteTools.combinationKeywordsChar(origin);
    }
}
