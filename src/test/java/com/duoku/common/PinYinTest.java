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


    public static void main(String[] args) {
        Instancer instance = MiniSearch.findInstance("hello_world");
        // add all into index
        instance.add("为什么放弃治疗");
        instance.add("为什么月经迟迟不来");
        instance.add("为什么晚上不能照镜子");
        instance.add("weishenme");
        instance.add("胃");
        instance.add("胃什么");
        instance.add("为什么");
        instance.add("为蛇要放弃治疗");
        //try searching
        Collection<Object> result = instance.find("wei什么");
        System.out.println(result);

        boolean ws = LiteTools.match("^(.+)什(.*)", "wei什");
        System.out.println(ws);
    }
}
