package com.duoku.common;

import com.duoku.common.core.DictTree;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author caikun
 * @Description
 * @Date 下午4:33 20-4-20
 **/
public class CoreTest {


    public void atest() {

    }

    public static void main(String[] args) {
        DictTree dictTree = new DictTree();
        dictTree.insert("abc");
        dictTree.insert("abcd");
        dictTree.insert("cd");
        dictTree.insert("男士撒是200我");
        dictTree.insert("和平认识");
        dictTree.insert("男士31撒");
        dictTree.insert("男士撒");
        dictTree.insert("男士撒sad");
        dictTree.insert("男士撒sda");
        dictTree.insert("男士撒asa");
        dictTree.insert("男士撒12");
        dictTree.insert("男士撒ggg");
        dictTree.insert("男士撒ddd");
        dictTree.printAll(dictTree.getRoot());

        //====================1=
//        DictTree.Node node = dictTree.fixPositionNode(DictTree.beQueue("男士"), dictTree.getRoot());
//        System.out.println(node.getKey());
//
//
//        Set aset = new LinkedHashSet();
        Collection collection = dictTree.fetchSimilar("男");
        System.out.println(collection);
        //=====================

    }

}
