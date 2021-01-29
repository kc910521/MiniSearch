package com.ck.common.mini.core;

import com.ck.common.mini.util.LiteTools;

import java.io.Serializable;
import java.util.*;

/**
 * @Author caikun
 * @Description dictionary trees with multi-fields
 * Recommended in chinese
 *
 * @Date 下午2:43 20-4-20
 *
 *
 *
 * @param <CARRIER> value for enhanced dict tree hold
 * @param <ORIGIN_CARRIER> the real value that user set
 */
public class SpellingDictTree<CARRIER extends Map<SpellingDictTree.HolderKey, ORIGIN_CARRIER>, ORIGIN_CARRIER> extends DictTree {

    @Override
    protected int putActionFrom(Node cnode, SpellingComponent spellingComponent) {
        cnode.setTail(true);
        CARRIER cnodeCarrier = (CARRIER) cnode.getCarrier();
        SpellingDictTree.HolderKey holderKey = new HolderKey(spellingComponent.getId(), spellingComponent.getOriginKey());
        if (cnodeCarrier == null) {
            // 还未有元素的初始化工作并直接返回1
            HashMap<SpellingDictTree.HolderKey, ORIGIN_CARRIER> carrier = new HashMap<>(8);
            carrier.put(holderKey, (ORIGIN_CARRIER) spellingComponent.getOriginCarrier());
            cnode.setCarrier(carrier);
            return 1;
        }
        // 判断是否可以插入
        return cnodeCarrier.put(holderKey, (ORIGIN_CARRIER) spellingComponent.getOriginCarrier()) == null ? 1 : 0;
    }


    @Override
    protected int removeActionFrom(final Node node, SpellingComponent spellingComponent) {
        CARRIER carrier = (CARRIER) node.getCarrier();
        if (carrier == null) {
            return 0;
        }
        SpellingDictTree.HolderKey holderKey = new SpellingDictTree.HolderKey(spellingComponent.getId(), spellingComponent.getOriginKey());
        // 如果component存在ID并且和carrier Id不对应，直接返回0
        if (!carrier.containsKey(holderKey)) {
            return 0;
        } else {
            carrier.remove(holderKey);
            if (carrier.isEmpty()) {
                node.setTail(false);
                // 子节点为空则可以准备向上删除
                if (node.getDomains() == null || node.getDomains().size() == 0) {
                    // 节点无叶子节点,准备向上删除
                    return 2;
                }
            }
        }
        return 1;
    }

    /**
     * 获取该跟 father 下的所有可以搜索到的节点，然后塞到results
     *
     * @param father
     * @param results 不能为空
     */
    protected void ergodicAndSetBy(Node father, Collection<ORIGIN_CARRIER> results, String originKeyPattern, int page, int pageSize) {
        assert results != null;
        if (father == null) {
            return;
        }
        if (father.getKey() == null) {
            return;
        }
        int hitAndDrop = page * pageSize;
        ergodicTailsInBreadth(father, hitAndDrop, pageSize, results, originKeyPattern);
    }

    /**
     * carrier这个map结构的key
     */
    public static class HolderKey implements Serializable {

        // not null
        private String id;

        private String originChars;

        public HolderKey(String id, String originChars) {
            this.id = id;
            this.originChars = originChars;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOriginChars() {
            return originChars;
        }

        public void setOriginChars(String originChars) {
            this.originChars = originChars;
        }

        @Override
        public int hashCode() {
            return id.hashCode() ^ originChars.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SpellingDictTree.HolderKey) {
                HolderKey hk = (HolderKey) obj;
                if (this.id.equals(hk.getId()) && this.originChars.equals(hk.getOriginChars())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return id + "_" + originChars;
        }
    }

    /**
     * 对father下的所有tail==true的节点信息都加入results，最多加入maxReturn个
     *
     * @param father
     * @param hitAndDrop
     * @param results
     */
    protected void ergodicTailsInBreadth(Node father, int hitAndDrop, int needSize, Collection<ORIGIN_CARRIER> results, String originKeyPattern) {
        assert results != null;
        Stack<Node> stack = new Stack<>();
        if (father != null) {
            stack.push(father);
            int hit = 0;
            while (stack.size() > 0) {
                if (results.size() >= needSize) {
                    // 判断长度
                    return;
                }
                Node<CARRIER> popNode = stack.pop();
                if (popNode.isTail()) {
                    CARRIER carrierMap = popNode.getCarrier();
                    if (carrierMap != null) {
                        Set<Map.Entry<SpellingDictTree.HolderKey, ORIGIN_CARRIER>> entries = carrierMap.entrySet();
                        for (Map.Entry<SpellingDictTree.HolderKey, ORIGIN_CARRIER> entry : entries) {
                            // todo:freematch且允许空格,修改匹配规则
                            if (canMatch(originKeyPattern, entry.getKey().getOriginChars()) && results.add(entry.getValue())) {
                                hit++;
                                if (hit <= hitAndDrop) {
                                    results.remove(entry.getValue());
                                }
                            }

                        }
                    }
                }
                if (popNode.getDomains() != null) {
                    Iterator<Map.Entry<Character, Node>> iterator = popNode.getDomains().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Character, Node> next = iterator.next();
                        stack.push(next.getValue());
                    }
                }
            }
        }
    }

    /**
     * compose
     *
     * @param cq
     * @return
     */
    public Collection<ORIGIN_CARRIER> fetchSimilar(Queue<Character> cq, String originKeyPattern, boolean strict, int page, int pageSize) {
        // 指定返回的类型
        List<ORIGIN_CARRIER> results = new ArrayList<>();
        Node root = getRoot();
        if (root == null || root.getDomains() == null || root.getDomains().isEmpty()) {
            return results;
        }
        // 4 root
        Node node = findPositionNode(cq, root, strict);
        if (node != null) {
            ergodicAndSetBy(node, results, originKeyPattern, page, pageSize);
        }
        return results;
    }

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


}
