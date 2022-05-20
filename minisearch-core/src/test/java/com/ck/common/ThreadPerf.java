package com.ck.common;

import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.index.struct.IExternalInstance;
import com.ck.common.mini.util.MiniSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author caikun
 * @Description 并发测试
 * @Date 上午10:46 21-11-19
 **/
public class ThreadPerf {

    static IExternalInstance instance = MiniSearch.findInstance("hello_world");

    //存放请求响应时间的列表
    static List<Long> timeList = new ArrayList<>();


    public static void addAndSearch(int total) {
        CountDownLatch cd = new CountDownLatch(total + 1);
        // build base
        instance.add("我撒娇撒撒asas方法和是是12发");
        instance.add("我撒dadsaasas方法和是是12发");
        instance.add("我撒娇撒撒agggg2发");
        instance.add("我撒娇撒撒asa我撒娇撒撒asas方法和是是12发2发");
        instance.add("合并12发");
        instance.add("丢哦发欢哥是撒asas方法和是是12发");
        instance.add("我撒娇撒撒asas方法和是是12发");
        instance.add("我撒娇撒答点是12发");
        instance.add("大搜打和是是12发");
        instance.add("阿斯顿撒旦12发");
        instance.add("误操作参哥");


        long s1 = System.currentTimeMillis();
        // search
        for (int i = total; i > 0; i--) {
            new Thread(() -> {
                Collection<Object> hm = Collections.EMPTY_LIST;
                // read 100s
                int a = 5;
                System.out.println("search start ");
                while (a-- > 0) {
                    instance.add("sasasas");
                    for (int k = 0; k < 100000; k++) {
                        Collection<Object> hm2 = instance.find("我撒娇撒撒asas方法和是是12发");
//                        if (!hm2.equals(hm)) {
//                            hm = hm2;
//                            System.out.println(hm);
//                        }
                    }
                }
                System.out.println("search over ");
                cd.countDown();
            }).start();

        }
        new Thread(() -> {
            // write
            System.out.println("write start ");
            int b = 20;
            while (b-- > 0) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                instance.add("我撒娇撒撒asas方法和是是12发", "hello" + b);
//                System.out.println("add ok:" + System.currentTimeMillis());
            }
            cd.countDown();
            System.out.println("write over ");
        }).start();

        try {
            cd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long s2 = System.currentTimeMillis();
        long l = s2 - s1;
        timeList.add(l);
        System.out.println("result is: " + l);


    }

    public static void main(String[] args) {
        addAndSearch(1);
        addAndSearch(3);
        addAndSearch(5);
        addAndSearch(7);
        addAndSearch(9);
        addAndSearch(10);
        addAndSearch(12);
        addAndSearch(14);
        System.out.println(timeList);
        long sum = timeList.stream().mapToLong(Long::longValue).sum();
        System.out.println(sum / timeList.size());
    }


}
