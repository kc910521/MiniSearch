package com.duoku.common;

import com.duoku.common.core.DictTree;
import com.duoku.common.factory.Instancer;
import com.duoku.common.factory.SimpleInstancer;
import com.duoku.common.util.MiniSearch;

import java.util.*;

/**
 * @Author caikun
 * @Description
 * @Date 下午4:33 20-4-20
 **/
public class CoreTest {


    public void atest() {

    }

    public static void main(String[] args) {
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

        String name = "search";
        SimpleInstancer instance = (SimpleInstancer) MiniSearch.findInstance(name);
        instance.add("abc");
        instance.add("abcd");
        instance.add("cd");
        instance.add("男1");
        instance.add("男士撒sa");
        instance.add("男士撒s");
        instance.add("男士撒sbd");
        instance.add("男士撒sac");
        instance.add("男士撒saf");
        instance.add("男士撒sadm");
        instance.add("男士撒sad");

        Collection<String> men = instance.find("男");
        System.out.println(men);
        int w = instance.remove("男士撒s");
        System.out.println("r" + w);
        Collection<String> men99 = instance.find("男");
        System.out.println(men99);
        instance.add("男士撒s");
        Collection<String> men98 = instance.find("男");
        System.out.println(men98);
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

}
