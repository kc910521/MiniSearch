package com.ck.common.util;

import java.util.Collection;

public class CheckUtil {

    public static void sizeCheck(int targetSize, Collection collection) {
        System.out.println(collection);
        assert targetSize == collection.size();
    }

    public static void itemInCheck(Object target, Collection collection) {
        System.out.println(collection);
        assert collection.contains(target);
    }



    private CheckUtil(){}
}
