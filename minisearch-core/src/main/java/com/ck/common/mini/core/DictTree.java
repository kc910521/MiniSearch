package com.ck.common.mini.core;


import com.ck.common.mini.core.condition.ConditionMatcher;

import java.io.Serializable;
import java.util.*;

/**
 * @Author caikun
 * @Description dictionary trees
 * Recommended in alphabet
 * @Date 下午2:43 20-4-20
 **/
public class DictTree<CARRIER extends Serializable> {

    private String indexName;

    public DictTree(String indexName) {
        this.indexName = indexName;
    }

    private Node root = new Node(64);

    private ConditionMatcher conditionMatcher = null;

    static class Node<CARRIER> {

        private Character key;

        // {nodeKey, nodeInfo}
        private HashMap<Character, DictTree.Node> domains;

        private boolean tail;

        private CARRIER carrier;

        Node() {
            domains = new HashMap<Character, DictTree.Node>();
        }

        Node(int i) {
            domains = new HashMap<Character, DictTree.Node>(i);
        }


        public CARRIER getCarrier() {
            return carrier;
        }

        public Character getKey() {
            return key;
        }

        public HashMap<Character, Node> getDomains() {
            return domains;
        }

        public boolean isTail() {
            return tail;
        }

        public void setKey(Character key) {
            this.key = key;
        }

        public void setDomains(HashMap<Character, Node> domains) {
            this.domains = domains;
        }

        public void setTail(boolean tail) {
            this.tail = tail;
        }

        public void setCarrier(CARRIER carrier) {
            this.carrier = carrier;
        }
    }

    /**
     * ready for GC ?
     */
    public void clear() {
        setRoot(null);
        setRoot(new Node());
        System.gc();

    }


    /**
     * 传入上1级别节点 father
     * 判断如果key不存在于 father的 domains，则插入
     *
     * @param cq
     * @param root
     * @return 1 success ;2 do nothing
     */
    protected int insert(Queue<Character> cq, final Node root, SpellingComponent<CARRIER> spellingComponent) {
        assert root != null && cq != null && cq.size() != 0;
//        Map<Character, DictTree.Node> domains = root.domains;
        char nchar;
        Node cnode = null;
        Node father = root;
        while (cq.size() != 0) {
            nchar = cq.poll();
            if (father.domains == null) {
                father.domains = new HashMap(16);
            }
            // father has no right subNode
            if (!father.domains.containsKey(nchar)) {
                // insert one layer
                cnode = new Node();
                cnode.key = nchar;
                // generate one layer for one char
                father.domains.put(cnode.key, cnode);
                father = cnode;
            } else {
                // has the node for nchar
                Node nextNode = (Node) father.domains.get(nchar);
                father = nextNode;
                cnode = nextNode;
            }
        }
        // 当前nchar应该为最后一个字符
        // cnode为最后一个带最后一个nchar的节点
        if (cnode != null) {
            // cnode.carrier = carrier;
            this.putActionFrom(cnode, spellingComponent);
        } else {
            return 2;
        }
        return 1;
    }

    /**
     * 赋值动作
     *
     * @param cnode             为最后一个带最后一个nchar的节点
     * @param spellingComponent {carrier为 存储的承载对象}
     * @return
     */
    protected int putActionFrom(final Node cnode, final SpellingComponent<CARRIER> spellingComponent) {
        cnode.carrier = spellingComponent.getOriginCarrier();
        cnode.tail = true;
        return 1;
    }

    /**
     * @param node
     * @param spellingComponent
     * @return 1:已经删除originKey且node不需要向上删除; 2:需要向上删除; 0:未找到合适内容，立即返回
     */
    protected int removeActionFrom(final Node<CARRIER> node, SpellingComponent<CARRIER> spellingComponent) {
        CARRIER carrier = node.getCarrier();
        if (carrier == null) {
            return 0;
        } else {
            // todo:如果需要在分词的同时正确的删除，请修改此处，判断是否应当删除; 可尝试在Node中加入originKey
            node.setCarrier(null);
            node.setTail(false);
            // 子节点为空则可以准备向上删除
            if (node.getDomains() == null || node.getDomains().size() == 0) {
                // 节点无叶子节点,准备向上删除
                return 2;
            }
        }
        return 1;
    }

    /**
     * insert keywords with carrier
     *
     * @param cq
     * @param spellingComponent
     * @return
     */
    public int insert(Queue<Character> cq, SpellingComponent<CARRIER> spellingComponent) {
        return insert(cq, root, spellingComponent);
    }


    public int removeToLastTail(Queue<Character> cq, final Node<CARRIER> root, SpellingComponent<CARRIER> spellingComponent) {
        assert root != null;
        Deque<Node> deque = new LinkedList<Node>();
        Node positionNode = this.findPositionNode(cq, root, true, deque);
        if (positionNode != null && deque.size() != 0) {
            Node<CARRIER> pop = deque.pop();
            // 处理目标节点的删除
            // 循环处理向上删除
            if (this.removeActionFrom(pop, spellingComponent) == 2) {
                while (deque.size() > 0) {

                    if ((pop.getDomains() == null || pop.getDomains().size() == 0) && !pop.isTail()) {
                        // 无子节点,，可以删
                        Node<CARRIER> pop2 = deque.pop();
                        pop2.setDomains(null);
                    } else {
                        return 1;
                    }
                }
            }
            return 1;
        }
        return 0;

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
    @Deprecated
    private int removeToLastTailRecursion(Queue<Character> cq, final Node father) {
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
        Node targetChild = (Node) father.domains.get(nowChar);
        int i = removeToLastTailRecursion(cq, targetChild);

        if (i == 1) {
            // 第一次进入已经是倒数第二层
            // targetChild为最后一层
            // 删除下面的
            // help GC
            targetChild.carrier = null;
            father.domains.remove(targetChild);
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
     * @param root
     */
    public void printChild(Node root) {
        System.out.println("ready for print all node !");
        printChildDepth(root);
        System.out.println("### it is over to print all node ###");
//        printChildBreadth(root);
    }

    protected void printChildDepth(Node<CARRIER> root) {
        if (root == null) {
            throw new RuntimeException("How could you ...");
        }
        Deque<Node> deque = new ArrayDeque<>();
        deque.addLast(root);
        while (deque.size() > 0) {
            Node pop = deque.pop();
            if (pop.carrier != null) {
                System.out.println(pop.getKey() + "-> " + pop.carrier);
            }
            if (pop.getDomains() != null) {
                Iterator<Map.Entry<Character, DictTree.Node>> iterator = pop.getDomains().entrySet().iterator();
                while (iterator.hasNext()) {
                    deque.addLast(iterator.next().getValue());
                }
            }
        }
    }

    public <CONDITION> boolean filter(CONDITION condtion, CARRIER carrier) {
        if (conditionMatcher != null) {
            return conditionMatcher.match(condtion, carrier);
        }
        return true;
    }

    protected void printChildBreadth(Node<CARRIER> root) {
        if (root == null) {
            throw new RuntimeException("How could you ...");
        }
        Deque<Node> deque = new ArrayDeque<>();
        deque.addFirst(root);
        while (deque.size() > 0) {
            // removeFirst
            Node pop = deque.pop();
            if (pop.carrier != null) {
                System.out.println(pop.getKey() + "-> " + pop.carrier);
            }
            if (pop.getDomains() != null) {
                Iterator<Map.Entry<Character, DictTree.Node>> iterator = pop.getDomains().entrySet().iterator();
                while (iterator.hasNext()) {
                    deque.addFirst(iterator.next().getValue());
                }
            }
        }
    }


    public Node getRoot() {
        return root;
    }

    protected void setRoot(Node root) {
        this.root = root;
    }

    /**
     * find the position last matched char
     *
     * @param cq
     * @param root
     */
    protected Node findPositionNode(Queue<Character> cq, Node root, boolean strict) {
        assert root != null && cq != null && cq.size() != 0;
        Node father = root;
        while (cq.size() != 0) {
            if (father.domains == null) {
                return null;
            }
            Character nowChar = cq.poll();
            if (father.domains.containsKey(nowChar)) {
                // match & continue
                father = (Node) father.domains.get(nowChar);
            } else {
                // 不匹配
                if (strict) {
                    father = null;
                }
                return father;
            }
        }
        return father;
    }

    /**
     * like findPositionNode
     * @param cq
     * @param root
     * @param strict
     * @param pathQ Node in path that met, have last node but not have root node
     * @return
     */
    protected Node findPositionNode(Queue<Character> cq, Node root, boolean strict, Deque<Node> pathQ) {
        assert root != null && cq != null && cq.size() != 0 && pathQ != null;
        Node father = root;
        while (cq.size() != 0) {
            if (father.domains == null) {
                return null;
            }
            Character nowChar = cq.poll();
            if (father.domains.containsKey(nowChar)) {
                // match & continue
                father = (Node) father.domains.get(nowChar);
                pathQ.addFirst(father);
            } else {
                // 不匹配
                if (strict) {
                    father = null;
                }
                return father;
            }
        }
        return father;
    }

    /**
     * 获取该跟 father 下的所有可以搜索到的节点，然后塞到results
     *
     * @param father
     * @param results 不能为空
     */
    protected void ergodicAndSetBy(Node father, Collection<CARRIER> results, int page, int pageSize) {
        assert results != null;
        if (father == null) {
            return;
        }
        if (father.getKey() == null) {
            return;
        }
        int hitAndDrop = page * pageSize;
        ergodicTailsInBreadth(father, hitAndDrop, pageSize, results);
    }

    /**
     * 对father下的所有tail==true的节点信息都加入results，最多加入maxReturn个
     *
     * @param father
     * @param hitAndDrop 需要丢弃的结果数
     * @param needSize 需要保留的结果数
     * @param results
     */
    protected void ergodicTailsInBreadth(Node father, int hitAndDrop, int needSize, Collection<CARRIER> results) {
        assert results != null;
        Stack<Node> stack = new Stack<>();
        int hit = 0;
        if (father != null) {
            stack.push(father);
            while (stack.size() > 0) {
                // 判断长度
                if (results.size() >= needSize) {
                    break;
                }
                Node<CARRIER> popNode = stack.pop();
                if (popNode.isTail()) {
                    CARRIER carrier = popNode.getCarrier();
                    if (results.add(carrier)) {
                        hit++;
                        // 未能高于hitAndDrop，移除刚刚插入的
                        if (hit <= hitAndDrop) {
                            results.remove(carrier);
                        } else {
                            // 高于HitDrop，则插入结果集（无操作）
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

    public Collection<CARRIER> fetchSimilar(Queue<Character> cq, boolean strict, int page, int pageSize) {
        Set<CARRIER> results = new LinkedHashSet<>();
        if (root == null || root.domains == null || root.domains.isEmpty()) {
            return results;
        }
        // 4 root
        Node node = findPositionNode(cq, root, strict);
        if (node != null) {
            ergodicAndSetBy(node, results, page, pageSize);
        }
        return results;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setConditionMatcher(ConditionMatcher conditionMatcher) {
        this.conditionMatcher = conditionMatcher;
    }
}
