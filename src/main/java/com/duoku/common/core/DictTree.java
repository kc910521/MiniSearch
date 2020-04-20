package com.duoku.common.core;

import java.util.*;

/**
 * @Author caikun
 * @Description dictionary trees
 * mention the concurrent issues
 * or try to override the class
 *
 * @Date 下午2:43 20-4-20
 **/
public class DictTree <CARRIER> {

    private static int MAX_RETURN = 5;

    private Node root = new Node();

    public class Node {

        private Character key;

        // { nodeKey, nodeInfo}
        private HashMap<Character, Node> domains;

        private boolean tail;

        private CARRIER carrier;

        public CARRIER getCarrier() {
            return carrier;
        }

        public Character getKey() {
            return key;
        }
    }


    /**
     * 传入上1级别节点 father
     * 判断如果key不存在于 father的 domains，则插入
     * @param cq
     * @param father
     * @return
     */
    protected int insert(Queue<Character> cq, Node father, CARRIER carrier) {
        if (cq.size() == 0) {
            father.tail = true;
            father.carrier = carrier;
            return 1;
        }
        char nchar = cq.poll();
        if (father.domains == null) {
            father.domains = new HashMap<>();
            Node cnode = new Node();
            cnode.key = nchar;
            father.domains.put(cnode.key, cnode);
            insert(cq, cnode, carrier);
        } else {
            if (father.domains.containsKey(nchar)) {
                Node cnode = father.domains.get(nchar);
                return insert(cq, cnode, carrier);
            } else {
                // 不存在
                Node node = new Node();
                node.key = nchar;
                father.domains.put(node.key, node);
                return insert(cq, node, carrier);
            }
        }
        return -1;
    }

    public final static Queue beQueue(String keywords) {
        char[] chars = keywords.toCharArray();
        Queue<Character> cq = new LinkedList<>();
        for (int i = 0; i < chars.length; i ++) {
            cq.offer(chars[i]);
        }
        return cq;
    }

    public int insert(String keywords) {
        char[] chars = keywords.toCharArray();
        Queue<Character> cq = new LinkedList<>();
        for (int i = 0; i < chars.length; i ++) {
            cq.offer(chars[i]);
        }
        return insert(cq, root, (CARRIER) keywords);
    }


    public int remove(char key, Node thead) {

        return -1;
    }

    /**
     * print all info for debug
     * @param father
     */
    public void printAll(Node father) {
        System.out.println(father.key + "|with dt: " + father.tail + "| fn: " + father.carrier);
        if (father.domains != null) {
            Iterator<Map.Entry<Character, Node>> iterator = father.domains.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Character, Node> next = iterator.next();
                printAll(next.getValue());
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    /**
     * fix the position last matched char
     * @param cq
     * @param father
     */
    protected Node fixPositionNode(Queue<Character> cq, Node father) {
        if (father == null || cq.size() == 0) {
            return father;
        }
        Character nowChar = cq.poll();
        if (!father.domains.containsKey(nowChar)) {
            return null;
        }
        return fixPositionNode(cq, father.domains.get(nowChar));
    }

//    public Node fixPositionNode(Queue<Character> cq, Node father) {
//        if (father == null || cq.size() == 0) {
//            return father;
//        }
//        Character nowChar = cq.poll();
//        if (!father.domains.containsKey(nowChar)) {
//            return father;
//        }
//        return fixPositionNode(cq, father.domains.get(nowChar));
//    }


    protected void ergodicAndSetBy(Node root, Collection<String> results) {
        if (root.key == null) {
            return;
        }
        if (results.size() >= DictTree.MAX_RETURN) {
            return;
        }
        if (root.tail) {
            results.add((String) root.carrier);
        }
        if (root.domains != null) {
            Iterator<Map.Entry<Character, Node>> iterator = root.domains.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Character, Node> next = iterator.next();
                ergodicAndSetBy(next.getValue(), results);
            }
        }
    }
    /**
     * compose
      * @param keywords
     * @return
     */
    public Collection<String> fetchSimilar(String keywords) {
        Set<String> results = new LinkedHashSet<>();
        // 4 root
        Node node = fixPositionNode(beQueue(keywords), root);
        if (node != null) {
            ergodicAndSetBy(node, results);
        }
        return results;
    }

}
