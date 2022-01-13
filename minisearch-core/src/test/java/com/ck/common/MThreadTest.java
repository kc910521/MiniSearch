package com.ck.common;

import java.util.concurrent.locks.StampedLock;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午3:49 21-11-23
 **/
public class MThreadTest {

    public static void main(String[] args) {

        final StampedLock stampedLock = new StampedLock();

        new Thread(() -> {
            long l = stampedLock.tryOptimisticRead();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean validate = stampedLock.validate(l);
            System.out.println(validate + ",>>");
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("a");
        }).start();


        new Thread(() -> {
            System.out.println("b");
            long l = stampedLock.writeLock();
            System.out.println("wlock get" + l);
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                stampedLock.unlockWrite(l);
            }

        }).start();


    }


}
