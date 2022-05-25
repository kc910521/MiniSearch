package com.ck.common;


import com.ck.common.mini.index.struct.MiniInstance;
import com.ck.common.mini.util.MiniSearch;
import com.ck.common.util.CheckUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

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
        struct.add("我吃了20个丸子");
        struct.add("那个男人看起来像条狗唉");
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
        CheckUtil.sizeCheck(8, what);
    }

    @Test
    public void findPinyin2() {
        MiniInstance struct = MiniSearch.findInstance("hello1");
        Collection<Object> what = struct.find("fangqi");
        CheckUtil.itemInCheck("胃什么放弃治疗啊你", what);
    }

    @Test
    public void findPinyin3() {
        MiniInstance struct = MiniSearch.findInstance("hello1");
        Collection<Object> what = struct.find("日月");
        CheckUtil.sizeCheck(0, what);
    }

    @Test
    public void update1() {
        MiniInstance struct = MiniSearch.findInstance("hello1");
        List<Object> what = (List) struct.find("男人");
        CheckUtil.sizeCheck(1, what);
        int remove = struct.remove(what.get(0).toString());
        what = (List) struct.find("男人");
        CheckUtil.sizeCheck(0, what);
        //
        struct.add("那个女人");
        struct.add("那个男人");
        struct.add("那个男人唉");
        what = (List) struct.find("男人");
        CheckUtil.sizeCheck(2, what);
    }

}
