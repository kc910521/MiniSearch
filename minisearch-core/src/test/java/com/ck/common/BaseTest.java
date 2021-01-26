package com.ck.common;

import com.ck.common.mini.index.SimpleInstancer;

import java.util.Collection;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午6:55 21-1-25
 **/
public class BaseTest {


    static SimpleInstancer simpleInstancer = new SimpleInstancer("hussar");

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
        abcdefghi = simpleInstancer.add("abc");
        abcdefghi += simpleInstancer.add("a");
        abcdefghi += simpleInstancer.add("ab");

        Collection<Object> a = simpleInstancer.find("a");
        System.out.println(a);

    }

    public static void main(String[] args) {
//        tt1();
        tt2();
    }

}
