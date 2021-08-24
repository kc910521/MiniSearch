package com.ck.common;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.util.MiniSearch;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author caikun
 * @Description
 * @Date 下午4:33 20-4-20
 **/
public class CoreTest {

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

    public static void conditionTest() {
        IndexInstance instance = MiniSearch.findInstance("hello_world");
        // add all into index
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

    public static void fast() {
        // create
        IndexInstance instance = MiniSearch.findInstance("hello_world");
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
        instance.init(params);

        int wc = instance.remove("为什么月经迟迟不来");
        int a22 = instance.remove("为什么月经迟迟不来");
        int a33 = instance.remove("为什么晚上不能照镜子");
        System.out.println(wc + "," + a22 + "," + a33);
    }

    public static void dup() {
        // create
        IndexInstance instance = MiniSearch.findInstance("hello_world");
        // add all into index
        instance.add("为什么放弃治疗", new Info("因为我没钱了"));
        instance.add("为什么月经迟迟不来", new Info("因为我爱你"));
        instance.add("为什么晚上不能照镜子", new Info("因为没交电费"));
        //try searching
        Collection<Object> result = instance.find("为什么晚上不能照镜子");
        System.out.println(result);
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("为什么月经迟迟不来", new Info("weishenmefangqizhiliao1"));
//        params.put("为什么晚上不能照镜子", new Info("weishenmewanshangbunengzhaojingzi3"));
//        instance.init(params);
//
//        int wc = instance.remove("为什么月经迟迟不来");
//        int a22 = instance.remove("为什么月经迟迟不来");
//        int a33 = instance.remove("为什么晚上不能照镜子");
//        System.out.println(wc + "," + a22 + "," + a33);
    }

    public static void dup2() {
        // create
        IndexInstance instance = MiniSearch.findInstance("hello_world");
        // add all into index
        instance.add("我爱");
//        instance.add("爱");
//        instance.add("为什么月经迟迟不来", new Info("因为我爱你"));
//        instance.add("为什么晚上不能照镜子", new Info("因为没交电费"));
        //try searching
        Collection<Object> result = instance.find("爱");
        System.out.println(result);
        instance.remove("我爱");
//        instance.remove("爱");
        Collection<Object> result2 = instance.find("我爱");
        System.out.println(result2);
        instance.remove("我爱");
        instance.add("我爱");
        System.out.println("============");
        result2 = instance.find("我爱");
        System.out.println(result2);
        result2 = instance.find("我");
        System.out.println(result2);
        result2 = instance.find("爱");
        System.out.println(result2);
    }

    public static void dup3() {
        // create
        IndexInstance instance = MiniSearch.findInstance("hello_world");
        // add all into index
        instance.add("我爱花");
        instance.add("woaihua");
        instance.add("wo爱h");
        instance.add("爱花");
        instance.add("我");

        instance.remove("我爱花");
        instance.remove("woaihua");
        instance.remove("wo爱h");
        instance.remove("爱花");
        instance.remove("我");


        instance.add("我爱花");
        instance.add("woaihua");
        instance.add("wo爱h");
        instance.add("爱花");
        instance.add("我");
//        instance.add("爱");
//        instance.add("为什么月经迟迟不来", new Info("因为我爱你"));
//        instance.add("为什么晚上不能照镜子", new Info("因为没交电费"));
        //try searching
        Collection<Object> result = instance.find("爱");
        System.out.println(result);
        instance.remove("我爱");
//        instance.remove("爱");
        Collection<Object> result2 = instance.find("我爱");
        System.out.println(result2);
        instance.remove("我爱");
        instance.remove("我爱花");
        instance.add("我爱");
        System.out.println("============");
        result2 = instance.find("我爱");
        System.out.println(result2);
        result2 = instance.find("我");
        System.out.println(result2);
        result2 = instance.find("ai");
        System.out.println(result2);
    }

    public static void dupTest() {
        IndexInstance instance = MiniSearch.findInstance("duplicate_chars");
        instance.add(new String("1111"));
        instance.add(new String("111"));

        instance.add(new String("11"));

        instance.add("aaaa2", UUID.randomUUID().toString());

        instance.add("aaaab", UUID.randomUUID().toString());
        System.out.println(instance.find("11"));

    }



    public static void pageTest() {
        System.out.println("========= pageTest============");
        IndexInstance instance = MiniSearch.findInstance("hello_world_page");
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

        String input = "盒子";
        System.out.println(instance.find(input));
//
        System.out.println(instance.find(input, -2, -5));
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

    static MiniSearchConfigure ufConfig = new MiniSearchConfigure();

    static {
        ufConfig.setFreeMatch(false);
    }


    public static void idTest() {
        System.out.println("========= idTest============");
        IndexInstance instance = MiniSearch.findInstance("id_test");

        instance.addWithId("0001", "极品狗粮", "极品狗粮1");
        instance.addWithId("0002", "极品狗粮", "极品狗粮2");
        instance.addWithId("0003", "杂粮煎饼", "极品狗粮3");

        Collection<Object> jp = instance.find("极品狗粮");
        System.out.println(jp);
//
//        System.out.println();
//        System.out.println(instance.add("素卡素卡你"));
//        System.out.println(instance.remove("素卡素卡你"));
//        System.out.println(instance.remove("素卡素卡你"));
//        System.out.println(instance.find("素"));
//        System.out.println("----go---");
//        System.out.println(instance.addWithId("32", "素卡1", "素卡素卡你32"));
//        System.out.println(instance.addWithId("33", "素卡1", "素卡素卡你33"));
//        System.out.println(instance.find("素"));
//        instance.printAll();

    }

    public static void pinyin() {
        IndexInstance instance = MiniSearch.findInstance("pinyin");
        instance.add("和蔼");
        instance.add("heai2");
        System.out.println(instance.find("heai"));

    }


    public static void main(String[] args) {
//        fast();
//        dup();
//        dup2();
//        dup3();
//        pageTest();
//        idTest();
//        pinyin();
//        orderSearchTest();
//        taskTiming();
//        dupTest();
        conditionTest();
    }

    public static void taskTiming() {
        // create
        IndexInstance instance1 = MiniSearch.findInstance("hello_world");
        MiniSearch.enableRebuild();
        // add all into index
        IndexInstance instance2 = MiniSearch.findInstance("hello_world2");
        MiniSearch.enableRebuild();
        MiniSearch.enableRebuild();
        //try searching
        Collection<Object> result = instance1.find("为什么");
        System.out.println(result);
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("为什么月经迟迟不来", new Info("weishenmefangqizhiliao1"));
//        params.put("为什么晚上不能照镜子", new Info("weishenmewanshangbunengzhaojingzi3"));
//        instance.init(params);
        instance1.setRebuildWorker((instancer) -> {
            int add = instancer.add("为什么放弃治疗", new Info("weishenmefangqizhiliao1"));
            instancer.add("为什么月经迟迟不来", new Info("weishenmeyuejingchichibulai2"));
            instancer.add("为什么晚上不能照镜子", new Info("weishenmewanshangbunengzhaojingzi3"));
            instancer.add("为蛇要放弃治疗", new Info("weisheyaofangqizhiliao4"));
            System.out.println(Thread.currentThread().getName() + " rebuild " + add + " in " + System.currentTimeMillis());
        });
        try {
            Thread.sleep(1000 * 40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = instance1.find("为什么");
        System.out.println("1" + result);
        result = instance2.find("为什么");
        System.out.println("2" + result);
    }


    public static void orderSearchTest() {
        MiniSearchConfigure miniSearchConfigure = new MiniSearchConfigure();
        miniSearchConfigure.setFreeMatch(false);
        miniSearchConfigure.setCoreType(0);
        IndexInstance instance = MiniSearch.findInstance("code_finder", miniSearchConfigure);
        instance.add("abc12345");
        instance.add("mbc12345");
        instance.add("bck12345");
        Collection<Object> bc = instance.find("bc");
        System.out.println(bc);
    }

    //        DictTree dictTree = new DictTree();
//        dictTree.insert("abc");
//        dictTree.insert("abcd");
//        dictTree.insert("cd");
//        dictTree.insert("男士撒是200我");
//        dictTree.insert("和平认识");
//        dictTree.insert("男士31撒");
//        dictTree.insert("男士撒");
//        dictTree.insert("男士撒sad");
//        dictTree.insert("男士撒sda");
//        dictTree.insert("男士撒asa");
//        dictTree.insert("男士撒12");
//        dictTree.insert("男士撒ggg");
//        dictTree.insert("男士撒ddd");
//        dictTree.printAll(dictTree.getRoot());

    //====================1=
//        DictTree.Node node = dictTree.fixPositionNode(DictTree.beQueue("男士"), dictTree.getRoot());
//        System.out.println(node.getKey());
//
//
//        Set aset = new LinkedHashSet();
//        Collection collection = dictTree.fetchSimilar("男");
//        System.out.println(collection);
    //=====================

//    String name = "search";
//    Instancer instance = MiniSearch.findInstance(name);
//        instance.add("abc");
//        instance.add("abcd");
//        instance.add("cd");
//        instance.add("男1");
//        instance.add("男士撒sa");
//        instance.add("男士撒s");
//        instance.add("男士撒sbd");
//        instance.add("男士撒sac");
//        instance.add("男士撒saf");
//        instance.add("男士撒sadm");
//        instance.add(",.!，，D_NAME。！；‘’”“**dfs  #$%^&()-+1431221\"\"中           国123漢字かどうかのjavaを決定");

    //        Collection<String> men = instance.find("男");
//        System.out.println(men);
//        int w = instance.remove("男士撒s");
//        System.out.println("r" + w);
//        Collection<String> men99 = instance.find("男");
//        System.out.println(men99);
//        instance.add("男士撒s");
//    Collection<String> men98 = instance.find("");
//        System.out.println(men98);
//        Map<String, Object> map = new HashMap<>();
//        map.put("a6", new TestBean("a6", 3));
//        map.put("aa6", new TestBean("aa6", 31));
//        map.put("ac6", new TestBean("ac6", 311));
//        map.put("aff6", new TestBean("aff6", 3111));
//        map.put("ff", new TestBean("ff", 23));
//        map.put("赛2撒及2", new TestBean("赛2撒及2", 223));
//        map.put("男2撒及22", new TestBean("男2撒及22", 2223));
//        map.put("dasa", new TestBean("dasa", 443));
//        map.put("aadasd6", new TestBean("aadasd6", 43));
//        map.put("aaaaaa6", new TestBean("aaaaaa6", 4443));
//        map.put("aaaa6", new TestBean("aaaa6", 53));
//        map.put("aa素", new TestBean("aa素", 553));
//        map.put("po", new TestBean("po", 5553));
//        instance.init(new HashMap<>());
//        instance.init(map);
//
//        Collection<String> men2 = instance.find("男");
//        System.out.println(men2);
//
//        Collection<String> men3 = instance.find("c");
//        System.out.println(men3);
//
//        Collection<String> men4 = instance.find("a");
//        System.out.println(men4);

}
