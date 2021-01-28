package com.ck.common;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.SimpleInstancer;

import java.util.Collection;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午6:55 21-1-25
 **/
public class BaseTest {

    static MiniSearchConfigure miniSearchConfigure = new MiniSearchConfigure();

    static {
        miniSearchConfigure.setFreeMatch(false);
    }

    static SimpleInstancer simpleInstancer = new SimpleInstancer("hussar", miniSearchConfigure);

    public static void tt1() {
        int abcdefghi = simpleInstancer.add("abcdefghi");
        abcdefghi += simpleInstancer.add("abmnop");
        abcdefghi += simpleInstancer.add("bmw");
        System.out.println(abcdefghi);

        Collection<Object> results = simpleInstancer.find("abcdefghi");
        System.out.println(results);
        results = simpleInstancer.find("abcdefgh");
        System.out.println(results);
        results = simpleInstancer.find("a");
        System.out.println(results);
        results = simpleInstancer.find("bcdefgh");
        System.out.println(results);
        results = simpleInstancer.find("acd");
        System.out.println(results);

        results = simpleInstancer.find("bcdefghii");
        System.out.println(results);
        results = simpleInstancer.find("i");
        System.out.println(results);
        results = simpleInstancer.find("b");
        System.out.println(results);
    }


    public static void tt2() {
        int abcdefghi = 0;
        abcdefghi = simpleInstancer.remove("a");
        abcdefghi = simpleInstancer.add("a");
        abcdefghi += simpleInstancer.add("ab");
        abcdefghi += simpleInstancer.add("abc");
        abcdefghi += simpleInstancer.add("bc");
        abcdefghi += simpleInstancer.add("abcdefghijklmn");

        abcdefghi = simpleInstancer.remove("a");
        abcdefghi += simpleInstancer.remove("ab");
        abcdefghi += simpleInstancer.remove("abc");
        abcdefghi += simpleInstancer.remove("bc");


//        abcdefghi += simpleInstancer.add("ab");
//        abcdefghi = simpleInstancer.add("abcd");

        Collection<Object> a = simpleInstancer.find("a");
        System.out.println(a);
        abcdefghi += simpleInstancer.add("ab");
        abcdefghi += simpleInstancer.add("b");
        int abc = simpleInstancer.remove("ab");
        System.out.println(simpleInstancer.find("b"));
        System.out.println(simpleInstancer.find("abc"));


    }

    public static void pageTest() {
        System.out.println("pageTest");
        SimpleInstancer simpleInstancer = new SimpleInstancer("hussar", miniSearchConfigure);
        simpleInstancer.add("abc016");
        simpleInstancer.add("abc020");
        simpleInstancer.add("abc004");
        simpleInstancer.add("abc005");
        simpleInstancer.add("abc012");
        simpleInstancer.add("abc009");
        simpleInstancer.add("abc013");
        simpleInstancer.add("abc015");
        simpleInstancer.add("abc003");
        simpleInstancer.add("abc011");
        simpleInstancer.add("abc010");
        simpleInstancer.add("abc006");
        simpleInstancer.add("abc007");
        simpleInstancer.add("abc008");
        simpleInstancer.add("abc001");
        simpleInstancer.add("abc002");
        simpleInstancer.add("abc013");
        System.out.println(simpleInstancer.find("ab", 0, 1));
        System.out.println(simpleInstancer.find("ab", 1, 1));
        System.out.println(simpleInstancer.find("ab", 2, 1));
        System.out.println(simpleInstancer.find("ab", 3, 1));


    }

    public static void main(String[] args) {
        tt1();
        simpleInstancer.init(null);
        tt2();
        pageTest();
    }

}
