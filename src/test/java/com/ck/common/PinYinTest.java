package com.ck.common;

import com.ck.common.mini.index.Instancer;
import com.ck.common.mini.util.MiniSearch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 上午10:56 20-4-28
 **/
public class PinYinTest {

    static Instancer instance = MiniSearch.findInstance("hello_world");

    public static void main(String[] args) {
        // add all into index
//        instance.add("为什么放弃治疗");
//        instance.add("为什么月经迟迟不来");
//        instance.add("为什么晚上不能照镜子");
//        instance.add("weishenme");
//        instance.add("胃");
//        instance.add("胃什么");
//        instance.add("为什么");
//        instance.add("胃什么放弃治疗");
//        instance.add("胃什么放弃治疗啊你");
//        //try searching
//        System.out.println(findBy("wei什么fang"));
//        instance.remove("胃什么放弃治疗");
//        instance.remove("为什么放弃治疗");
//        System.out.println(findBy("wei什么fang"));
//        instance.add("为什么放弃治疗");
//        System.out.println(findBy("w"));

        //
        Map<String, Object> data = new HashMap<>();
        data.put("woca卧槽666", "sa");
        instance.init(data);
        instance.add("为什么放弃治疗");
        System.out.println(findBy("为"));
    }

    public static Collection<Object> findBy(String kw) {
        return instance.find(kw);
    }
}
