package com.ariel.redis.bitset;

import com.ariel.redis.RedissonFactory;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;

import java.util.Random;

/**
 * 布隆过滤器
 */

public class Bloom {

    
    private RedissonClient redisson;
    public static final String NAME = "bloom9";
    public static final int TOTAL = 10_0000;
    public static final int KEY_SIZE = (1 << 22) - 1;

    @Test
    public void testA() {
        // 插入批量数据
        RBitSet bitSet = redisson.getBitSet(NAME);
        Random random = new Random();
        for (int i = 0; i < TOTAL; i++) {
            insert(bitSet, random.nextLong());
        }
    }

    @Test
    public void testB() {
        // 查询命中机率列表，命中次数越低越好，但需要更大空间
        // Key大小：1 << 32 用户数：1 0000 命中次数：0
        // Key大小：1 << 24 用户数：10 0000 命中次数：7
        // Key大小：1 << 22 用户数：10 0000 命中次数：220
        // Key大小：1 << 20 用户数：10 0000 命中次数：3066
        RBitSet bitSet = redisson.getBitSet(NAME);
        Random random = new Random();
        int count = 0;
        for (int i = 0; i < TOTAL; i++) {
            if (find(bitSet, random.nextLong())) {
                count++;
                System.out.printf("第%s次命中了数据%n", i);
            }
        }
        System.out.println(count);
    }

    public void insert(RBitSet set, long id) {
        set.set(hashcodeByMode(id), true);
        set.set(hashcodeByFix(id), true);
    }

    public boolean find(RBitSet set, long id) {
        return set.get(hashcodeByMode(id)) && set.get(hashcodeByFix(id));
    }

    public long hashcodeByMode(long id) {
        return abs(id % KEY_SIZE);
    }

    public long hashcodeByFix(long id) {
        return abs((id ^ (id >>> 32)) & KEY_SIZE);
    }

    public long abs(long hashcode) {
        return hashcode > 0 ? hashcode : - hashcode;
    }

}
