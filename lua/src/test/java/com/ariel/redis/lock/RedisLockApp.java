package com.ariel.redis.lock;

import com.ariel.redis.RedissonFactory;
import org.junit.After;
import org.junit.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁性能测试
 */
public class RedisLockApp {

    private final RedissonClient redisson = new RedissonFactory().getRedissonClient();

    @After
    public void shutdown() {
        redisson.shutdown();
    }

    @Test
    public void testA() {
        // 7481
        run(redisson.getLock("123"), 1_00);
    }

    @Test
    public void testB() throws InterruptedException {
        // 19908
        RLock lock = redisson.getLock("123");
        RLock redLock = redisson.getRedLock(lock);
        run(redLock, 1_0000);
    }

    @Test
    public void testC() throws InterruptedException {
        // 11486
        run(redisson.getFairLock("1234"), 1_0000);
    }

    @Test
    public void testD() throws InterruptedException {
        // 11786
//        run(redisson.getSpinLock("1234"), 1_0000);
    }

    @Test
    public void testE() throws InterruptedException {
        // 13012
        run(redisson.getReadWriteLock("1234").writeLock(), 1_0000);
    }

    @Test
    public void testF() throws InterruptedException {
        // 分段锁
        int count = 10_0000;
        int threadNum = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                run(redisson.getLock(Thread.currentThread().getName()), count / threadNum);
                countDownLatch.countDown();
            }).start();
        }

        // 4889 33474
        long l = System.currentTimeMillis();
        countDownLatch.await();
        System.out.println(System.currentTimeMillis() - l);
    }

    void run(RLock lock, int count) {
        final int threadNum = 10;
        final int[] ints = {0};
        ints[0] = count;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);

        Runnable runnable = () -> {
            for (int j = 0; j < count / threadNum; j++) {
                lock.lock(1, TimeUnit.SECONDS);
                try {
                    ints[0]--;
                } finally {
                    lock.unlock();
                }
            }
            countDownLatch.countDown();
        };

        long l = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            new Thread(runnable, "MyThread-" + i).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(ints[0]);
        System.out.println(System.currentTimeMillis() - l);
    }

}
