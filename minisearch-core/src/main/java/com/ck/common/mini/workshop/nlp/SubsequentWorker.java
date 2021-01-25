package com.ck.common.mini.workshop.nlp;

import java.util.ArrayList;

/**
 * @Author caikun
 * @Description 挨个返回从头到尾的字符串:
 * eg:abcde
 * return:
 * abcde,bcde,cde,de,e
 * @Date 上午10:32 20-11-11
 **/
public class SubsequentWorker implements NLPWorker {
    @Override
    public ArrayList<String> work(String origin) {
        ArrayList<String> result = new ArrayList<>();
        if (origin == null || origin.length() == 0) {
            return result;
        }
        for (int i = 0; i < origin.length(); i++) {
            result.add(origin.substring(i, origin.length()));
        }
        return result;
    }

    public static void main(String[] args) {
        SubsequentWorker subsequentWorker = new SubsequentWorker();
        System.out.println(subsequentWorker.work("abcde"));
    }
}
