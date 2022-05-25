package com.ck.common;


import com.ck.common.mini.index.struct.MiniInstance;
import com.ck.common.mini.util.MiniSearch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

/**
 * @Author caikun
 * @Description 简单设置+搜索
 * @Date 下午4:33 20-4-20
 **/
public class SetAndGetTest {

    @BeforeEach
    public void init() {
        MiniInstance struct = MiniSearch.findInstance("hello1");
        struct.add("为什么放弃治疗");
        struct.add("为什么月经迟迟不来");
        struct.add("为什么晚上不能照镜子");
        struct.add("weishenme");
        struct.add("胃");
        struct.add("胃什么");
        struct.add("为什么");
        struct.add("胃什么放弃治疗");
        struct.add("胃什么放弃治疗啊你");
        System.out.println("init over");
    }

    @Test
    public void findPinyin1() {
        MiniInstance struct = MiniSearch.findInstance("hello1");
        Collection<Object> what = struct.find("什么");
        System.out.println(what);
    }


}
