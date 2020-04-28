package com.duoku.common.mini.core;

import com.duoku.common.mini.config.MiniSearchConfigure;

import java.io.Serializable;
import java.util.*;

/**
 * @Author caikun
 * @Description dictionary trees
 * mention the concurrent issues
 * or try to override the class
 * @Date 下午2:43 20-4-20
 **/
public class SpellingDictTree<CARRIER extends Serializable> {

    private MiniSearchConfigure miniSearchConfigure = null;

    public SpellingDictTree() {
        this.miniSearchConfigure = new MiniSearchConfigure();
    }

    public SpellingDictTree(MiniSearchConfigure miniSearchConfigure) {
        this.miniSearchConfigure = miniSearchConfigure;
    }

    private Node root = new Node();

    public class Node {

        private Character key;

        // {nodeKey, nodeInfo}
        private HashMap<Character, Node> domains;

        private boolean tail;

        // {originChars, entity}
        private Map<String, CARRIER> carrierMap = new HashMap<>();

        public Map<String, CARRIER> getCarrierMap() {
            return carrierMap;
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
            if (father.carrierMap != null) {
                father.carrierMap.clear();
                father.carrierMap = null;// help gc
            }
        }
    }

    public void clear() {
        clear(root);
    }


    /**
     * 传入上1级别节点 father
     * 判断如果key不存在于 father的 domains，则插入
     *
     * @param cq
     * @param father
     * @return 1 success ;2 do nothing
     */
    protected int insert(Queue<Character> cq, Node father, SpellingComponent<CARRIER> carrier) {
        if (cq.size() == 0) {
            father.tail = true;
            father.carrierMap.put(carrier.getOriginKey(), carrier.getCarrier());
            return 2;
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
        return 1;
    }


    /**
     * insert keywords with carrier
     *
     * @param cq
     * @param carrierMap
     * @return
     */
    public int insert(Queue<Character> cq, SpellingComponent<CARRIER> carrierMap) {
        return insert(cq, root, carrierMap);
    }


    /**
     * remove by keys from Q
     * from bottom to up
     * <p>
     * 如果last还有下一个节点- 设置该节点tail为false.
     * 如果无下一个节点：1设置该节点tail为false
     * 2.删除本节点-》出递归
     *
     * @param cq
     * @param father
     * @return
     */
    public int removeToLastTail(Queue<Character> cq, final Node father, String originKey) {
        if (father == null) {
            return -1;
        }
        if (cq.size() == 0) {
            // 已经便利到尾部
            // 将当前节点设置为非尾部
            father.tail = false;
            if (father.domains != null && !father.domains.isEmpty()) {
                // 非叶子，啥也不干
                return 0;
            }
            // 叶子节点，进入递归删除,
            return 1;
        }
        Character nowChar = cq.poll();
        if (father.domains == null || !father.domains.containsKey(nowChar)) {
            return -1;
        }
        Node targetChild = father.domains.get(nowChar);
        int i = removeToLastTail(cq, targetChild, originKey);

        if (i == 1) {
            // 第一次进入已经是倒数第二层
            // targetChild为最后一层
            // 删除下面的
            if (targetChild.carrierMap != null) {
                targetChild.carrierMap.remove(originKey);
                if (targetChild.carrierMap == null || targetChild.carrierMap.isEmpty()) {
                    targetChild.carrierMap = null;//help GC
                }
            }
            if (!father.tail) {
                // 继续
                return 1;
            } else {
                // 撤销
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * print all info for debug
     *
     * @param father
     */
    public void printChild(Node father) {
        System.out.println("key:" + father.key + "|isTail: " + father.tail + "|carrierMap: " + father.carrierMap);
        if (father.domains != null) {
            Iterator<Map.Entry<Character, Node>> iterator = father.domains.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Character, Node> next = iterator.next();
                printChild(next.getValue());
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    /**
     * fix the position last matched char
     *
     * @param cq
     * @param father
     */
    protected Node fixPositionNode(Queue<Character> cq, Node father, boolean strict) {
        if (father == null || cq.size() == 0) {
            return father;
        }
        Character nowChar = cq.poll();
        if (father.domains == null || !father.domains.containsKey(nowChar)) {
            return strict ? null : father;
        }
        return fixPositionNode(cq, father.domains.get(nowChar), strict);
    }

    protected void ergodicAndSetBy(Node root, Collection<CARRIER> results) {
        if (root.key == null) {
            return;
        }
        if (results.size() >= miniSearchConfigure.getMaxFetchNum()) {
            return;
        }
        if (root.tail) {
            if (root.carrierMap != null) {
                root.carrierMap.forEach((k, v) -> {
                    results.add(v);
                });
            }
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
     *
     * @param cq
     * @return
     */
    public Collection<CARRIER> fetchSimilar(Queue<Character> cq) {
        Set<CARRIER> results = new LinkedHashSet<>();
        if (root == null || root.domains == null || root.domains.isEmpty()) {
            return results;
        }
        // 4 root
        Node node = fixPositionNode(cq, root, miniSearchConfigure.isStrict());
        if (node != null) {
            ergodicAndSetBy(node, results);
        }
        return results;
    }


}
