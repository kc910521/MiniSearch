package com.duoku.common;

import com.duoku.common.mini.index.Instancer;
import com.duoku.common.mini.util.LiteTools;
import com.duoku.common.mini.util.MiniSearch;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 上午10:56 20-4-28
 **/
public class PinYinTest {

    static Instancer instance = MiniSearch.findInstance("hello_world");

    public static void main(String[] args) {
        // add all into index
        instance.add("为什么放弃治疗");
        instance.add("为什么月经迟迟不来");
        instance.add("为什么晚上不能照镜子");
        instance.add("weishenme");
        instance.add("胃");
        instance.add("胃什么");
        instance.add("为什么");
        instance.add("胃什么放弃治疗");
        instance.add("胃什么放弃治疗a");
        //try searching
        System.out.println(findBy("wei什么fang"));
        instance.remove("胃什么放弃治疗");
        instance.remove("为什么放弃治疗");
        System.out.println(findBy("wei什么fang"));
        instance.add("为什么放弃治疗");
        System.out.println(findBy("wei什么fang"));
    }

    public static Collection<Object> findBy(String kw) {
        return instance.find(kw);
    }
}
