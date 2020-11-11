package com.ck.common.mini.workshop.nlp;

import java.util.ArrayList;

/**
 * @Author caikun
 * @Description 分词和自然语言处理策略
 * @Date 上午10:23 20-11-11
 **/
public interface NLPWorker {

    /**
     * nlp具体工作
     *
     * @param origin 原始字符串
     * @return 处理后结果
     */
    ArrayList<String> work(String origin);

}
