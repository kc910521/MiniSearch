package com.ck.common.mini.core;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.util.LiteTools;

import java.io.Serializable;
import java.util.*;

/**
 * @Author caikun
 * @Description dictionary trees with multi-fields
 * Recommended in chinese
 *
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

    private SpNode root = new SpNode();

    static class SpNode<CARRIER> extends DictTree.Node {

        private HashMap<Character, SpellingDictTree.SpNode> domains;
        // {originChars, entity}
        private Map<String, CARRIER> carrierMap = new HashMap<>();

        public Map<String, CARRIER> getCarrierMap() {
            return carrierMap;
        }
    }

    public synchronized void clear(SpNode father) {
        if (father.domains != null) {
            Iterator<Map.Entry<Character, SpNode>> iterator = father.domains.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Character, SpNode> next = iterator.next();
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
    protected int insert(Queue<Character> cq, SpNode father, SpellingComponent<CARRIER> spellingComponent) {
        if (cq.size() == 0) {
            father.setTail(true);
            if (father.carrierMap == null) {
                father.carrierMap = new HashMap<>();
            }
            father.carrierMap.put(spellingComponent.getOriginKey(), spellingComponent.getCarrier());
            return 2;
        }
        char nchar = cq.poll();
        if (father.domains == null) {
            father.domains = new HashMap<>();
            SpNode cnode = new SpNode();
            cnode.setKey(nchar);
            father.domains.put(cnode.getKey(), cnode);
            insert(cq, cnode, spellingComponent);
        } else {
            if (father.domains.containsKey(nchar)) {
                SpNode cnode = (SpNode) father.domains.get(nchar);
                return insert(cq, cnode, spellingComponent);
            } else {
                // 不存在
                SpNode SpNode = new SpNode();
                SpNode.setKey(nchar);
                father.domains.put(SpNode.getKey(), SpNode);
                return insert(cq, SpNode, spellingComponent);
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
    public int removeToLastTail(Queue<Character> cq, final SpNode father, String originKey) {
        if (father == null) {
            return -1;
        }
        if (cq.size() == 0) {
            // 已经便利到尾部
            // 将当前节点设置为非尾部
            // 需要判断小子节点是否吻合
            if (father.carrierMap == null || father.carrierMap.isEmpty() || father.carrierMap.containsKey(originKey)) {
                father.setTail(false);
            }
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
        SpNode targetChild = (SpNode) father.domains.get(nowChar);
        int i = removeToLastTail(cq, targetChild, originKey);

        if (i == 1) {
            // 第一次进入已经是倒数第二层
            // targetChild为最后一层
            // 删除下面的
            if (targetChild.carrierMap != null) {
                targetChild.carrierMap.remove(originKey);
                if (targetChild.carrierMap.isEmpty()) {
                    targetChild.carrierMap = null;//help GC
                } else {
                    // 多match，所以直接结束,撤销
                    targetChild.setTail(true);
                    return 0;
                }
            }
            if (!father.isTail()) {
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
    public void printChild(SpNode father) {
//        System.out.println("key:" + father.key + "|isTail: " + father.tail + "|carrierMap: " + father.carrierMap);
        if (father.domains != null) {
            Iterator<Map.Entry<Character, SpNode>> iterator = father.domains.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Character, SpNode> next = iterator.next();
                printChild(next.getValue());
            }
        }
    }

    public SpNode getRoot() {
        return root;
    }

    /**
     * fix the position last matched char
     *
     * @param cq
     * @param father
     */
    protected SpNode fixPositionNode(Queue<Character> cq, SpNode father, boolean strict) {
        if (father == null || cq.size() == 0) {
            return father;
        }
        Character nowChar = cq.poll();
        if (father.domains == null || !father.domains.containsKey(nowChar)) {
            return strict ? null : father;
        }
        return fixPositionNode(cq, (SpNode) father.domains.get(nowChar), strict);
    }

    // todo: 此处有额外计算，可以考虑后续缓存; private now

    /**
     * 匹配原始串和当前节点所有key
     *
     * @param originKeyPattern 原始串处理结果 eg:(.+)什(.*)
     * @param resultItemKey    前节点key
     * @return
     */
    private static final boolean canMatch(String originKeyPattern, String resultItemKey) {
        return LiteTools.match(originKeyPattern, resultItemKey);
    }

    /**
     * 遍历并加入节点到results
     *
     * @param root
     * @param results
     */
    protected void ergodicAndSetBy(SpNode root, Collection<CARRIER> results, String originKeyPattern) {
        if (root.getKey() == null) {
            return;
        }
        if (results.size() >= miniSearchConfigure.getMaxFetchNum()) {
            return;
        }
        if (root.isTail()) {
            if (root.carrierMap != null) {
                Set<Map.Entry<String, CARRIER>> entries = root.carrierMap.entrySet();
                for (Map.Entry<String, CARRIER> entry : entries) {
                    if (results.size() < miniSearchConfigure.getMaxFetchNum()) {
                        // 此处处理字符匹配
                        // todo:freematch且允许空格,修改匹配规则
                        if (canMatch(originKeyPattern, entry.getKey())) {
                            results.add(entry.getValue());
                        }
                    } else {
                        return;
                    }
                }
            }
        }
        if (root.domains != null) {
            Iterator<Map.Entry<Character, SpNode>> iterator = root.domains.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Character, SpNode> next = iterator.next();
                ergodicAndSetBy(next.getValue(), results, originKeyPattern);
            }
        }
    }

    /**
     * compose
     *
     * @param cq
     * @return
     */
    public Collection<CARRIER> fetchSimilar(Queue<Character> cq, String originKeyPattern) {
        Set<CARRIER> results = new LinkedHashSet<>();
        if (root == null || root.domains == null || root.domains.isEmpty()) {
            return results;
        }
        // 4 root
        SpNode SpNode = fixPositionNode(cq, root, miniSearchConfigure.isStrict());
        if (SpNode != null) {
            ergodicAndSetBy(SpNode, results, originKeyPattern);
        }
        return results;
    }


}
