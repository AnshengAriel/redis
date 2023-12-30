package com.ariel.redis.bitset;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.After;
import org.junit.Test;

import java.util.Random;

public class GuavaBloom {

    public static final int TOTAL = 100_0000;
    public static final BloomFilter<Integer> bloomFilter =
            BloomFilter.create(Funnels.integerFunnel(), TOTAL);

    @Test
    public void testA() {
        // 可重复执行的操作，没有加锁的必要
        Random random = new Random();
        for (int i = 0; i < TOTAL; i++) {
            bloomFilter.put(random.nextInt());
        }
    }

    @After
    static public void testB() {
        int count = 0;
        Random random = new Random();
        for (int i = 0; i < TOTAL; i++) {
            if (bloomFilter.mightContain(random.nextInt())) {
                System.out.printf("第%s次命中了数据%n", i);
                count++;
            }
        }
        System.out.println(count);
    }

}
