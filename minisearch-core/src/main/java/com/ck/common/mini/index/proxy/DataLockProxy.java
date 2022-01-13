package com.ck.common.mini.index.proxy;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.index.LocalIndexInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * @Author caikun
 * @Description 本地数据锁代理
 * @Date 下午6:29 21-11-22
 **/
public class DataLockProxy implements LocalIndexInstance, IndexInstance.TimingLocalReindex {

    /**
     * 被代理对象
     */
    private final LocalIndexInstance idx;

    private static final int lockTimeout = 3;

    private final StampedLock lock = new StampedLock();

    private static final Logger logger = LoggerFactory.getLogger(DataLockProxy.class);

    public DataLockProxy(LocalIndexInstance idx) {
        this.idx = idx;
    }

    @Override
    public void reindexing() {
        if (!(idx instanceof IndexInstance.TimingLocalReindex)) {
            logger.warn("not a TimingLocalReindex");
            return;
        }
        long stamp = lock.writeLock();
        try {
            ((IndexInstance.TimingLocalReindex) idx).reindexing();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void init(Map<String, Object> data) {
        long stamp = lock.writeLock();
        try {
            idx.init(data);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        Collection<CARRIER> cols = null;
        long stamp = lock.tryOptimisticRead();
        try {
            cols = idx.find(keywords);
        } catch (Throwable e) {
            logger.warn("", e);
        }
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                cols = idx.find(keywords);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return cols;
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords, int page, int pageSize) {
        Collection<CARRIER> cols = null;
        long stamp = lock.tryOptimisticRead();
        try {
            cols = idx.find(keywords, page, pageSize);
        } catch (Throwable e) {
            logger.warn("", e);
        }
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                cols = idx.find(keywords, page, pageSize);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return cols;
    }

    @Override
    public <CARRIER> Collection<CARRIER> findByCondition(String keywords, Object condition, int page, int pageSize) {
        Collection<CARRIER> cols = null;
        long stamp = lock.tryOptimisticRead();
        try {
            cols = idx.findByCondition(keywords, condition, page, pageSize);
        } catch (Throwable e) {
            logger.warn("", e);
        }
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                cols = idx.findByCondition(keywords, condition, page, pageSize);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return cols;
    }

    @Override
    public int addWithId(String id, String keywords, Object carrier) {
        long stamp = lock.writeLock();
        try {
            return idx.addWithId(id, keywords, carrier);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public int add(String keywords, Object carrier) {
        long stamp = lock.writeLock();
        try {
            return idx.add(keywords, carrier);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public int add(String keywords) {
        long stamp = lock.writeLock();
        try {
            return idx.add(keywords);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public int remove(String keywords) {
        long stamp = lock.writeLock();
        try {
            return idx.remove(keywords);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public int removeWithId(String id, String keywords) {
        long stamp = lock.writeLock();
        try {
            return idx.removeWithId(id, keywords);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void printAll() {
        long stamp = lock.readLock();
        try {
            idx.printAll();
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public MiniSearchConfigure getMiniSearchConfigure() {
        return idx.getMiniSearchConfigure();
    }

    @Override
    public String getInstanceName() {
        return idx.getInstanceName();
    }

    @Override
    public void setRebuildWorker(RebuildWorker rebuildWorker) {
        idx.setRebuildWorker(rebuildWorker);
    }


}
