package com.ck.common;

import com.ck.common.mini.index.struct.MiniInstance;
import com.ck.common.mini.util.MiniSearch;
import com.ck.common.util.CheckUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ObjectSeGetterTest {

    @BeforeEach
    public void init() {
        MiniInstance instance = MiniSearch.findInstance("info_object");

    }

    @Test
    public void objectSearch0() {
        MiniInstance instance = MiniSearch.findInstance("info_object");
        // add 1
        instance.add("为什么放弃治疗", new Info("fangqi"));
        // add 2
        Info bulai = new Info("bulai");
        bulai.tm2 = 31111;
        bulai.tm = 10087;
        instance.add("为什么月经迟迟不来", bulai);
        // add 3
        Info zhaojingzi = new Info("zhaojingzi");
        zhaojingzi.tm = 998;
        instance.add("为什么晚上不能照镜子", zhaojingzi);
        // condition
        // 因为bulaiCondition的tm2和i与数据对应，所以输出，因为condition的tm为空，所以不对此字段进行筛选
        Info bulaiCondition = new Info("bulai");
        bulaiCondition.tm2 = 31111;
        Collection<Object> result1 = instance.findByCondition("为什么", bulaiCondition, 0, 200);
        System.out.println("result1:" + result1);
        // 因为条件语句tm2为对象默认值0,但是原始数据被赋值，所以无法筛选出数据
        Collection<Object> result2 = instance.findByCondition("为什么", new Info("bulai"), 0, 200);
        System.out.println("result2:" + result2);
        Collection<Object> result3 = instance.findByCondition("为什么", new Info(), 0, 200);
        System.out.println("result3:" + result3);
        Info info3Condition = new Info();
        info3Condition.tm = 0;
        Collection<Object> result4 = instance.findByCondition("为什么", info3Condition, 0, 200);
        System.out.println("result4:" + result4);
    }

    @Test
    public void objectDelAndSearch() {
        // create
        MiniInstance instance = MiniSearch.findInstance("info_object");
        // add all into index
        instance.add("为什么放弃治疗", new Info("weishenmefangqizhiliao1"));
        instance.add("为什么月经迟迟不来", new Info("weishenmeyuejingchichibulai2"));
        instance.add("为什么晚上不能照镜子", new Info("weishenmewanshangbunengzhaojingzi3"));
        instance.add("为蛇要放弃治疗", new Info("weisheyaofangqizhiliao4"));
        //try searching
        Collection<Object> result1 = instance.find("为什么");
        System.out.println("result1:" + result1);
        Collection<Object> result2 = instance.findByCondition("为什么", new Info("weishenmeyuejingchichibulai2"), 0, 200);
        System.out.println("result2:" + result2);

        Map<String, Object> params = new HashMap<>();
        params.put("为什么月经迟迟不来", new Info("weishenmefangqizhiliao1"));
        params.put("为什么晚上不能照镜子", new Info("weishenmewanshangbunengzhaojingzi3"));
        instance.initData(params);

        int wc = instance.remove("为什么月经迟迟不来");
        int a22 = instance.remove("为什么月经迟迟不来");
        int a33 = instance.remove("为什么晚上不能照镜子");
        System.out.println(wc + "," + a22 + "," + a33);
        Collection<Object> weis = instance.find("为");
        CheckUtil.sizeCheck(0, weis);
    }

    @Test
    public void pagerSearchTest() {
        MiniInstance instance = MiniSearch.findInstance("hello_world_page");
        instance.add("高频赫兹充电");
        instance.add("赫兹充电器1");
        instance.add("新品-贺子品牌鞋垫3");
        instance.add("新品-贺子品牌鞋垫1");
        instance.add("新品-贺子品牌鞋垫2");
        instance.add("lily-合资电动车1");
        instance.add("lily-合资电动车2");
        instance.add("赫兹治疗仪2");
        instance.add("赫兹治疗仪3");
        instance.add("赫兹治疗仪4");
        instance.add("赫兹治疗仪5");
        instance.add("小盒子装钱用3");
        instance.add("小盒子装钱用4");
        instance.add("小盒子装钱用5");
        instance.add("小盒子装钱用6");
        instance.add("小盒子装钱用7");
        instance.add("小盒子装钱用8");
        instance.add("小盒子装钱用9");
        instance.add("可怕的正毒1");
        instance.add("可怕的正毒2");
        instance.add("可怕的正毒3");
        instance.add("赫兹治疗仪6");
        instance.add("赫兹治疗仪7");
        instance.add("赫兹治疗仪8");
        instance.add("赫兹治疗仪9");
        instance.add("赫兹充电头1");
        instance.add("小盒子装钱用1");
        instance.add("小盒子装钱用2");
        instance.add("小盒子装钱用10");

        String input = "盒子";
        System.out.println(instance.find(input));
//
        System.out.println("第0页");
        System.out.println(instance.find(input, 0, 5));
        System.out.println("第1页");
        System.out.println(instance.find(input, 1, 5));
        System.out.println("第2页");
        System.out.println(instance.find(input, 2, 5));
        System.out.println("第3页");
        System.out.println(instance.find(input, 3, 5));
        System.out.println("第4页");
        System.out.println(instance.find(input, 4, 5));
        System.out.println("第5页");
        System.out.println(instance.find(input, 5, 5));
        System.out.println("第6页");
        System.out.println(instance.find(input, 6, 5));
    }


    public static class Info implements Serializable {
        private String i;

        private Integer tm;

        private int tm2;

        public Info() {
        }

        private Info(String i) {
            this.i = i;
        }

        public String getI() {
            return i;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "i='" + i + '\'' +
                    ", tm=" + tm +
                    ", tm2=" + tm2 +
                    '}';
        }
    }

}
