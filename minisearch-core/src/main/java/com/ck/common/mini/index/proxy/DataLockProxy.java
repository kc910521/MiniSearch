package com.ck.common.mini.index.proxy;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.IndexInstance;
import com.ck.common.mini.index.LocalIndexInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * @Author caikun
 * @Description 本地数据锁代理
 * 读写都加锁，保证强一致
 * todo: 此处需要结构化设计
 *
 * @Date 下午6:29 21-11-22
 **/
public class DataLockProxy implements LocalIndexInstance, IndexInstance.TimingLocalReindex {

    /**
     * 被代理对象
     */
    private final LocalIndexInstance idx;
    /**
     * 写入等待超时时间
     */
    private static final int writeLockTimeoutSec = 3;

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
        Long stamp = null;
        try {
            stamp = lock.tryWriteLock(writeLockTimeoutSec, TimeUnit.SECONDS);
            ((IndexInstance.TimingLocalReindex) idx).reindexing();
        } catch (InterruptedException e) {
            logger.error("write lock timeout", e);
        } finally {
            if (stamp != null) {
                lock.unlockWrite(stamp);
            }
        }
    }

    @Override
    public void init(Map<String, Object> data) {
        Long stamp = null;
        try {
            stamp = lock.tryWriteLock(writeLockTimeoutSec, TimeUnit.SECONDS);
            idx.init(data);
        } catch (InterruptedException e) {
            logger.error("write lock timeout", e);
        } finally {
            if (stamp != null) {
                lock.unlockWrite(stamp);
            }
        }
    }

    @Override
    public <CARRIER> Collection<CARRIER> find(String keywords) {
        Collection<CARRIER> cols = null;
        long stamp = lock.tryOptimisticRead();
        try {
            cols = idx.find(keywords);
        } catch (Throwable e) {
            logger.warn("failed search.", e);
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
            logger.warn("failed search.", e);
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

        Long stamp = null;
        int i = 0;
        try {
            stamp = lock.tryWriteLock(writeLockTimeoutSec, TimeUnit.SECONDS);
            i = idx.addWithId(id, keywords, carrier);
        } catch (InterruptedException e) {
            logger.error("write lock timeout", e);
        } finally {
            if (stamp != null) {
                lock.unlockWrite(stamp);
            }
        }
        return i;
    }

    @Override
    public int add(String keywords, Object carrier) {
        Long stamp = null;
        int i = 0;
        try {
            stamp = lock.tryWriteLock(writeLockTimeoutSec, TimeUnit.SECONDS);
            i = idx.add(keywords, carrier);
        } catch (InterruptedException e) {
            logger.error("write lock timeout", e);
        } finally {
            if (stamp != null) {
                lock.unlockWrite(stamp);
            }
        }
        return i;
    }

    @Override
    public int add(String keywords) {
        Long stamp = null;
        int i = 0;
        try {
            stamp = lock.tryWriteLock(writeLockTimeoutSec, TimeUnit.SECONDS);
            i = idx.add(keywords);
        } catch (InterruptedException e) {
            logger.error("write lock timeout", e);
        } finally {
            if (stamp != null) {
                lock.unlockWrite(stamp);
            }
        }
        return i;
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
