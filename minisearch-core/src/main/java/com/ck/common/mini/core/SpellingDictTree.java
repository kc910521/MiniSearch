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
public class SpellingDictTree<CARRIER extends Map> extends DictTree {

    private MiniSearchConfigure miniSearchConfigure = null;

    public SpellingDictTree() {
        this.miniSearchConfigure = new MiniSearchConfigure();
    }

    public SpellingDictTree(MiniSearchConfigure miniSearchConfigure) {
        this.miniSearchConfigure = miniSearchConfigure;
    }


    @Override
    protected int putActionFrom(Node cnode, SpellingComponent spellingComponent) {
        cnode.setTail(true);
        CARRIER cnodeCarrier = (CARRIER) cnode.getCarrier();
        if (cnodeCarrier == null) {
            HashMap<Object, Object> objectObjectHashMap = new HashMap<>(8);
            objectObjectHashMap.put(spellingComponent.getOriginKey(), spellingComponent.getCarrier());
            cnode.setCarrier(objectObjectHashMap);
            return 1;
        }
        return cnodeCarrier.put(spellingComponent.getOriginKey(), spellingComponent.getCarrier()) == null ? 0 : 1;
    }


    @Override
    protected int removeActionFrom(final Node node, String originKey) {
        CARRIER carrier = (CARRIER) node.getCarrier();
        if (carrier == null || !carrier.containsKey(originKey)) {
            return 0;
        } else {
            carrier.remove(originKey);
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
     * remove by keys from Q
     * from bottom to up
     * <p>
     * 如果last还有下一个节点- 设置该节点tail为false.
     * 如果无下一个节点：1设置该节点tail为false
     * 2.删除本节点-》出递归
     *
     * @param cq
     * @param father
     * @param originKey 实际的key值
     * @return
     */
    @Deprecated
    private int removeToLastTailRecursion(Queue<Character> cq, final Node<CARRIER> father, String originKey) {
        if (father == null) {
            return -1;
        }
        if (cq.size() == 0) {
            // 已经便利到尾部
            // 将当前节点设置为非尾部
            // 需要判断小子节点是否吻合
            if (father.getCarrier() == null || father.getCarrier().isEmpty() || father.getCarrier().containsKey(originKey)) {
                father.setTail(false);
            }
            if (father.getDomains() != null && !father.getDomains().isEmpty()) {
                // 非叶子，啥也不干
                return 0;
            }
            // 叶子节点，进入递归删除,
            return 1;
        }
        Character nowChar = cq.poll();
        if (father.getDomains() == null || !father.getDomains().containsKey(nowChar)) {
            return -1;
        }
        Node<CARRIER> targetChild = (Node) father.getDomains().get(nowChar);
        int i = removeToLastTailRecursion(cq, targetChild, originKey);

        if (i == 1) {
            // 第一次进入已经是倒数第二层
            // targetChild为最后一层
            // 删除下面的
            if (targetChild.getCarrier() != null) {
                targetChild.getCarrier().remove(originKey);
                if (targetChild.getCarrier().isEmpty()) {
                    targetChild.setCarrier(null);//help GC
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

    // todo: 此处有额外计算，可以考虑后续缓存; private now


    /**
     * 获取该跟 father 下的所有可以搜索到的节点，然后塞到results
     *
     * @param father
     * @param results 不能为空
     */
    protected void ergodicAndSetBy(Node father, Collection<CARRIER> results, String originKeyPattern) {
        assert results != null;
        if (father == null) {
            return;
        }
        if (father.getKey() == null) {
            return;
        }
        int maxRs = miniSearchConfigure.getMaxFetchNum();
        ergodicTailsInBreadth(father, maxRs, results, originKeyPattern);
    }

    /**
     * 对father下的所有tail==true的节点信息都加入results，最多加入maxReturn个
     *
     * @param father
     * @param maxReturn
     * @param results
     */
    protected void ergodicTailsInBreadth(Node father, int maxReturn, Collection<CARRIER> results, String originKeyPattern) {
        assert results != null;
        Stack<Node> stack = new Stack<>();
        if (father != null) {
            stack.push(father);
            while (stack.size() > 0) {
                Node popNode = stack.pop();
                if (popNode.isTail()) {
                    Object carrierObject = popNode.getCarrier();
                    if (carrierObject != null) {
                        CARRIER carrier = (CARRIER) carrierObject;
                        Set<Map.Entry<String, CARRIER>> entries = carrier.entrySet();
                        for (Map.Entry<String, CARRIER> entry : entries) {
                            // todo:freematch且允许空格,修改匹配规则
                            if (canMatch(originKeyPattern, entry.getKey())) {
                                results.add(entry.getValue());
                            }
                            if (results.size() >= maxReturn) {
                                // 判断长度
                                return;
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
    public Collection<CARRIER> fetchSimilar(Queue<Character> cq, String originKeyPattern) {
        Set<CARRIER> results = new LinkedHashSet<>();
        Node root = getRoot();
        if (root == null || root.getDomains() == null || root.getDomains().isEmpty()) {
            return results;
        }
        // 4 root
        Node node = findPositionNode(cq, root, miniSearchConfigure.isStrict());
        if (node != null) {
            ergodicAndSetBy(node, results, originKeyPattern);
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
