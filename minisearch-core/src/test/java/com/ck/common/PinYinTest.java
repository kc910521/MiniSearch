package com.ck.common;

import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.util.MiniSearch;

import java.util.Collection;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 上午10:56 20-4-28
 **/
public class PinYinTest {

    static IndexInstance instance = MiniSearch.findInstance("hello_world");

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
//        Map<String, Object> data = new HashMap<>();
//        data.put("woca卧槽666", "sa");
//        instance.init(data);

        instance.add("为什么晚上不能照镜子");
        instance.add("光电鼠标没有球");
        instance.add("白色鼠标有球");
        instance.add("白色鼠标");
        instance.add("术镖，起立！");
        instance.add("鼠标(shubiao)的英文：mouse");
        instance.add("为什么shubiao没球了");
        findCase0();
        findCase1();
        findCase2();
        removeCase0();
    }

    private static void removeCase0() {
        System.out.println("==========removing========");
        instance.remove("白色");
        findAndPrint("色鼠标");
//        instance.printAll();
//        System.out.println(results);
    }

    private static void findCase0() {
        Collection<Object> results = findBy("为什么");
        System.out.println(results);
    }

    private static void findCase1() {
        Collection<Object> results = findBy("鼠标");
        System.out.println(results);
    }

    private static void findCase2() {
        Collection<Object> results = findBy("shubiao");
        System.out.println(results);
    }

    private static void findAndPrint(String keywords) {
        Collection<Object> results = findBy(keywords);
        System.out.println(results);
    }

    public static Collection<Object> findBy(String kw) {
        return instance.find(kw);
    }
}
