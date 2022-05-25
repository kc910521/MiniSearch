package com.ck.common.mini.core;

import com.ck.common.mini.util.LiteTools;

import javax.annotation.concurrent.NotThreadSafe;
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
@NotThreadSafe
public class SpellingDictTree<CARRIER extends Map<SpellingDictTree.HolderKey, ORIGIN_CARRIER>, ORIGIN_CARRIER> extends DictTree {

    public SpellingDictTree(String indexName) {
        super(indexName);
    }

    @Override
    protected int putActionFrom(Node cnode, SpellingComponent spellingComponent) {
        cnode.setTail(true);
        CARRIER cnodeCarrier = (CARRIER) cnode.getCarrier();
        SpellingDictTree.HolderKey holderKey = new HolderKey(spellingComponent.getId(), spellingComponent.getOriginKey());
        holderKey.setAspectChars(LiteTools.toUnDupSortedChars(holderKey.getOriginChars()));
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
    protected void ergodicAndSetBy(Node father, Collection<ORIGIN_CARRIER> results, char[] sortedOnlyBigChars, ORIGIN_CARRIER condition, int page, int pageSize) {
        assert results != null;
        if (father == null) {
            return;
        }
        if (father.getKey() == null) {
            return;
        }
        int hitAndDrop = page * pageSize;
        ergodicTailsInBreadth(father, hitAndDrop, pageSize, results, sortedOnlyBigChars, condition);
    }

    /**
     * carrier这个map结构的key
     */
    public static class HolderKey implements Serializable {

        // not null
        private String id;

        private String originChars;

        // 比对特征值判断是否和用户的输入类似，最简单的实现就是原值排序后压缩
        private char[] aspectChars;

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

        public char[] getAspectChars() {
            return aspectChars;
        }

        public void setAspectChars(char[] aspectChars) {
            this.aspectChars = aspectChars;
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
     * @param hitAndDrop 需要丢弃的数据个数
     * @param results 结果集
     * @param needSize 结果集内最多需要返回的数
     * @param sortedOnlyBigChars
     */
    protected void ergodicTailsInBreadth(Node father, int hitAndDrop, int needSize, Collection<ORIGIN_CARRIER> results, char[] sortedOnlyBigChars, ORIGIN_CARRIER condition) {
        assert results != null;
        if (father == null) {
            return;
        }
        Node orgFather = father;
        int hit = searchAndFill(orgFather, hitAndDrop, needSize, results, sortedOnlyBigChars, condition, true);
        int filledSize = results.size();
        int stillNeededSize = needSize - filledSize;
        if (stillNeededSize > 0) {
            // 能对应上中文的数据不充足，填充错字结果
            int hitAndDrop4wrongZh = hitAndDrop - hit;
            hitAndDrop4wrongZh = hitAndDrop4wrongZh < 0 ? 0 : hitAndDrop4wrongZh;
            // todo:当前为低效率的重新遍历。可优化：记忆节点等
            searchAndFill(father, hitAndDrop4wrongZh, needSize, results, sortedOnlyBigChars, condition, false);
        }
    }

    /**
     *
     * @param father
     * @param hitAndDrop
     * @param needSize
     * @param results
     * @param sortedOnlyBigChars
     * @param ks 和canMatch同或，为true则正向搜索，为false则搜同或以外的
     * @return hit
     */
    private int searchAndFill(Node father, int hitAndDrop, int needSize, Collection<ORIGIN_CARRIER> results, char[] sortedOnlyBigChars, ORIGIN_CARRIER condition, boolean ks) {
        Stack<Node> stack = new Stack<>();
        // 命中个数
        int hit = 0;
        if (father != null) {
            stack.push(father);
            while (stack.size() > 0) {
                if (results.size() >= needSize) {
                    // 判断长度
                    break;
                }
                Node<CARRIER> popNode = stack.pop();
                if (popNode.isTail()) {
                    CARRIER carrierMap = popNode.getCarrier();
                    if (carrierMap != null) {
                        Set<Map.Entry<SpellingDictTree.HolderKey, ORIGIN_CARRIER>> entries = carrierMap.entrySet();
                        for (Map.Entry<SpellingDictTree.HolderKey, ORIGIN_CARRIER> entry : entries) {
                            if (results.size() >= needSize) {
                                // 判断长度
                                return hit;
                            }
                            ORIGIN_CARRIER value = entry.getValue();
                            if (!(canMatch(sortedOnlyBigChars, entry.getKey()) ^ ks)
                                    && !results.contains(value)
                                    && filter(condition, (Serializable) value)
                                    && results.add(value)) {
                                hit++;
                                if (hit <= hitAndDrop) {
                                    results.remove(value);
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
        return hit;
    }


    /**
     * compose
     *
     * @param cq
     * @return
     */
    public Collection<ORIGIN_CARRIER> fetchSimilar(Queue<Character> cq, char[] sortedOnlyBigChars, ORIGIN_CARRIER condition, boolean strict, int page, int pageSize) {
        // 指定返回的类型
        List<ORIGIN_CARRIER> results = new ArrayList<>(pageSize > 10000 ? 10000 : pageSize);
        Node root = getRoot();
        if (root == null || root.getDomains() == null || root.getDomains().isEmpty()) {
            return results;
        }
        // 4 root
        Node node = findPositionNode(cq, root, strict);
        if (node != null) {
            ergodicAndSetBy(node, results, sortedOnlyBigChars, condition, page, pageSize);
        }
        return results;
    }

    public Collection<ORIGIN_CARRIER> fetchSimilar(Queue<Character> cq, char[] sortedOnlyBigChars, boolean strict, int page, int pageSize) {
        return this.fetchSimilar(cq, sortedOnlyBigChars, null, strict, page, pageSize);
    }

    /**
     * 匹配sortedOnlyBigChars内完全存在于 holderKey 的 sortedChars的情况
     *
     * @param sortedOnlyBigChars 仅包括了大Char字符排序char
     * @param holderKey  但前节点的一个冲突单元
     * @return
     */
    private static final boolean canMatch(char[] sortedOnlyBigChars, SpellingDictTree.HolderKey holderKey) {
        char[] sortedOriginChars = holderKey.getAspectChars();
        if (sortedOnlyBigChars == null || sortedOnlyBigChars.length == 0) {
            return true;
        }
        if (sortedOriginChars == null || sortedOriginChars.length == 0) {
            return true;
        }
        return charsContains(sortedOnlyBigChars, sortedOriginChars);
    }

    // todo：需要优化的算法

    /**
     * 判断sortedOnlyBigChars 每个char，在sortedOriginChars 中是否按顺序存在即可，已经排好序；
     * 可能会相等
     * @param sortedOnlyBigChars
     * @param sortedOriginChars
     * @return
     */
    private static boolean charsContains(char[] sortedOnlyBigChars, char[] sortedOriginChars) {
        int bigCharLowIndex = 0;
        int originLowIndex = 0;
        final int originHighIndex = sortedOriginChars.length - 1;
        for ( ;bigCharLowIndex < sortedOnlyBigChars.length && originLowIndex <= originHighIndex;) {
            char bigChar = sortedOnlyBigChars[bigCharLowIndex];
            int indexOf = getIndexOf(bigChar, sortedOriginChars, originLowIndex);
            if (indexOf >= 0) {
                originLowIndex = indexOf + 1;
                bigCharLowIndex ++;
            } else {
                return false;
            }
        }
        return bigCharLowIndex >= sortedOnlyBigChars.length - 1;
    }

    private static int getIndexOf(char c, char[] chars, int lowIndex) {
        int v = Arrays.binarySearch(Arrays.copyOfRange(chars, lowIndex, chars.length) , c);
        return v < 0 ? -1 : v + lowIndex;
    }

    public static void main(String[] args) {
//        char[] sortedOnlyBigChars = new char[]{'a'};
//        char[] sortedOriginChars = new char[]{'b'};
//        Arrays.sort(sortedOnlyBigChars);
//        Arrays.sort(sortedOriginChars);
//        System.out.println(Arrays.binarySearch(sortedOnlyBigChars ,'c'));
//        System.out.println(charsContains(sortedOnlyBigChars, sortedOriginChars));

        // true ~ true = true
        // false ~ true = false
        // true ~ false = false
        // false ~ false = true

        System.out.println(true ^ true);
        System.out.println(false ^ true);
        System.out.println(true ^ false);
        System.out.println(false ^ false);
    }


}
