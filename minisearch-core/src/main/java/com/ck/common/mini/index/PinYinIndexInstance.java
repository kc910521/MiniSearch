package com.ck.common.mini.index;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.core.SpellingComponent;
import com.ck.common.mini.core.SpellingDictTree;
import com.ck.common.mini.util.LiteTools;
import com.ck.common.mini.workshop.nlp.NLPAdmin;
import com.ck.common.mini.workshop.nlp.NLPWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.ck.common.mini.util.LiteTools.beQueue;
import static com.ck.common.mini.util.LiteTools.getPingYin;

/**
 * @Author caikun
 * @Description 针对中文的，优化过的搜索实例
 * @Date 上午11:49 20-4-28
 **/
@ThreadSafe
public class PinYinIndexInstance implements LocalIndexInstance, IndexInstance.TimingLocalReindex {

    private static final String PT_PREFIX = "^";
    private static final String PT_AMPLE_ONE_AT_LEAST = "(.+)";
    private static final String PT_AMPLE_ANY = "(.*)";
    private static final int lockTimeout = 3;

    private static final Logger logger = LoggerFactory.getLogger(PinYinIndexInstance.class);

    private SpellingDictTree spellingDictTree;

    private SpellingDictTree spellingDictTreeBack;

    private String instancerName;

    private MiniSearchConfigure miniSearchConfigure;

    private NLPWorker nlpWorker;

    private final ReentrantReadWriteLock rrw = new ReentrantReadWriteLock();

    private RebuildWorker rebuildWorker;

    public PinYinIndexInstance(String instancerName) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = new MiniSearchConfigure();
        this.spellingDictTree = new SpellingDictTree();
        this.nlpWorker = NLPAdmin.pickBy(this.miniSearchConfigure);
    }

    public PinYinIndexInstance(String instancerName, MiniSearchConfigure miniSearchConfigure) {
        this.instancerName = instancerName;
        this.miniSearchConfigure = miniSearchConfigure;
        this.spellingDictTree = new SpellingDictTree();
        this.nlpWorker = NLPAdmin.pickBy(this.miniSearchConfigure);
    }

    @Override
    public void init(Map<String, Object> data) {
        try {
            rrw.writeLock().tryLock(lockTimeout, TimeUnit.MINUTES);
            this.spellingDictTree.clear();
            Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                add(next.getKey(), next.getValue());
            }
            logger.info("init success");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            rrw.writeLock().unlock();
        }
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        try {
            rrw.readLock().tryLock(lockTimeout, TimeUnit.MINUTES);
            return this.find(keywords, 0, miniSearchConfigure.getMaxFetchNum());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            rrw.readLock().unlock();
        }

    }

    /**
     * @param keywords
     * @param page      from 0
     * @param pageSize
     * @param <CARRIER>
     * @return
     */
    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize) {
        if (keywords == null || keywords.trim().length() == 0) {
            return Collections.emptySet();
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        Collection result = null;
        try {
            rrw.readLock().tryLock(lockTimeout, TimeUnit.MINUTES);
            result = this.spellingDictTree.fetchSimilar(beQueue(getPingYin(keywords)), catchBigChars(keywords), miniSearchConfigure.isStrict(), page, pageSize);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            rrw.readLock().unlock();
        }
        return result;
    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        if (!(carrier instanceof Serializable)) {
            System.err.println("The carrier is not a instance of Serializable");
        }
        if (miniSearchConfigure.isIgnoreSymbol()) {
            keywords = keywords.replaceAll(miniSearchConfigure.getSymbolPattern(), "");
        }
        if (keywords == null || "".equals(keywords.trim())) {
            return -1;
        }
        SpellingComponent spellingComponent = new SpellingComponent(keywords, (Serializable) carrier);
        if (id != null) {
            spellingComponent.setId(id);
        }
        int rs = 0;
        List<String> subKeywords = nlpWorker.work(keywords);
        try {
            rrw.writeLock().tryLock(lockTimeout, TimeUnit.MINUTES);
            for (String kw : subKeywords) {
                rs += this.spellingDictTree.insert(beQueue(getPingYin(kw)), spellingComponent);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            rrw.writeLock().unlock();
        }

        return rs;
    }

    /**
     * 仅搂出中文，可以修改为也搂出其他占位符即可更加精确的匹配
     * @param keywords
     * @return
     */
    protected char[] catchBigChars(String keywords) {
        char[] chars = keywords.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : chars) {
            if ((c >= 0x4e00) && (c <= 0x9fa5)) {
                // chinese
                stringBuilder.append(c);
            }
        }
        return LiteTools.defUnDupSort(stringBuilder.toString().toCharArray());
    }
//
//    protected String catchPattern(String keywords) {
//        char[] chars = keywords.toCharArray();
//        List<String> stringList = new LinkedList<>();
//        if (miniSearchConfigure.isFreeMatch()) {
//            stringList.add(PT_AMPLE_ANY);
//        } else {
//            stringList.add(PT_PREFIX);
//        }
//        for (char c : chars) {
//            // 中文直接追加
//            // 英文判断上个节点是否为 PT_AMPLE_ONE_AT_LEAST 是continue 否则加入
//            if ((c >= 0x4e00) && (c <= 0x9fa5)) {
//                // chinese
//                stringList.add(String.valueOf(c));
//            } else {
//                String last = ((LinkedList<String>) stringList).getLast();
//                if (last.equals(PT_AMPLE_ONE_AT_LEAST)) {
//
//                } else {
//                    stringList.add(PT_AMPLE_ONE_AT_LEAST);
//                }
//            }
//        }
//        stringList.add(PT_AMPLE_ANY);
//
//        return Joiner.on("").join(stringList);
//    }

    @Override
    public int add(String keywords, Object carrier) {
        return addWithId(null, keywords, carrier);
    }

    @Override
    public int add(String keywords) {
        return this.add(keywords, keywords);
    }

    @Override
    public int remove(String keywords) {
        return this.removeWithId(null, keywords);
    }

    @Override
    public synchronized int removeWithId(String id, String keywords) {
        List<String> subKeywords = nlpWorker.work(keywords);
        SpellingComponent spellingComponent = new SpellingComponent(keywords);
        if (id != null) {
            spellingComponent.setId(id);
        }
        int i = 0;
        try {
            rrw.writeLock().tryLock(lockTimeout, TimeUnit.MINUTES);
            for (String kw : subKeywords) {
                i += this.spellingDictTree.removeToLastTail(beQueue(getPingYin(kw)), this.spellingDictTree.getRoot(), spellingComponent);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            rrw.writeLock().unlock();
        }
        return i;
    }

    @Deprecated
    @Override
    public void printAll() {
        try {
            rrw.readLock().tryLock(lockTimeout, TimeUnit.MINUTES);
            this.spellingDictTree.printChild(this.spellingDictTree.getRoot());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            rrw.readLock().unlock();
        }
    }

    @Override
    public MiniSearchConfigure getMiniSearchConfigure() {
        return miniSearchConfigure;
    }

    @Override
    public String getInstanceName() {
        return instancerName;
    }

    @Override
    public void reindexing() {
        if (this.rebuildWorker != null) {
            this.rebuildWorker.doWork(this);
        }
    }

    @Override
    public void setRebuildWorker(RebuildWorker rebuildWorker) {
        this.rebuildWorker = rebuildWorker;
    }
}
