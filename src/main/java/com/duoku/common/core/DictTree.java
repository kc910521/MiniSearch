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

    private TreeConfigure treeConfigure = null;

    public DictTree() {
        this.treeConfigure = new TreeConfigure();
    }

    public DictTree(TreeConfigure treeConfigure) {
        this.treeConfigure = treeConfigure;
    }

    private Node root = new Node();

    public class Node {

        private Character key;

        // {nodeKey, nodeInfo}
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

    public synchronized void clear(Node father) {
        if (father.domains != null) {
            Iterator<Map.Entry<Character, Node>> iterator = father.domains.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Character, Node> next = iterator.next();
                clear(next.getValue());
                iterator.remove();
            }
        } else {
            father.carrier = null;// help gc
        }
    }

    public void clear() {
        clear(root);
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


    /**
     * insert keywords with carrier
     *
     * @param keywords
     * @param carrier
     * @return
     */
    public int insert(String keywords, CARRIER carrier) {
        return insert(beQueue(keywords), root, carrier);
    }


    /**
     * '
     * remove by keys from Q
     * from bottom to up
     *
     * @param cq
     * @param root
     * @return
     */
    public int remove(Queue<Character> cq, Node root) {
//        if (root == null) {
//            // no exactly path found
//            return -1;
//        }
//        if (cq == null || cq.size() == 0) {
//            return 1;
//        }
//        Character key = cq.poll();
//        if (1 == remove(cq, root.domains.get(key))) {
//            Node node = root.domains.get(key);
//
//            node.carrier = null;// help GC
//            node.domains.remove(key);
//            return 1;
//        } else {
//            return 0;
//        }
        throw new RuntimeException("method not supported now.");
    }

    /**
     * print all info for debug
     * @param father
     */
    public void printAll(Node father) {
        System.out.println("key:" + father.key + "|isTail: " + father.tail + "|carrier: " + father.carrier);
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
            return treeConfigure.isFullMatch() ? null : father;
        }
        return fixPositionNode(cq, father.domains.get(nowChar));
    }

    protected void ergodicAndSetBy(Node root, Collection<CARRIER> results) {
        if (root.key == null) {
            return;
        }
        if (results.size() >= treeConfigure.getMaxFetchNum()) {
            return;
        }
        if (root.tail) {
            results.add(root.carrier);
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
    public Collection<CARRIER> fetchSimilar(String keywords) {
        Set<CARRIER> results = new LinkedHashSet<>();
        // 4 root
        Node node = fixPositionNode(beQueue(keywords), root);
        if (node != null) {
            ergodicAndSetBy(node, results);
        }
        return results;
    }

    public final static Queue beQueue(String keywords) {
        char[] chars = keywords.toCharArray();
        Queue<Character> cq = new LinkedList<>();
        for (int i = 0; i < chars.length; i++) {
            cq.offer(chars[i]);
        }
        return cq;
    }
}
