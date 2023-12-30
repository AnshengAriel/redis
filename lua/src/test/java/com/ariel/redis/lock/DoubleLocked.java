package com.ariel.redis.lock;

import com.ariel.redis.RedissonFactory;
import org.junit.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 双检加锁
 */
public class DoubleLocked {

    @Test
    void testA() {
        System.out.println(get("a", "b"));
        System.out.println(get("a", "c"));
    }

    String get(String key, String value) {
        RedissonClient redisson = new RedissonFactory().getRedissonClient();
        RBucket<String> bucket = redisson.getBucket(key);
        if (bucket.isExists()) {
            return bucket.get();
        }else {
            synchronized (this) {
                bucket = redisson.getBucket(key);
                // 双重检查
                if (!bucket.isExists()) {
                    bucket.set(value, 5, TimeUnit.MINUTES);
                }
                return bucket.get();
            }
        }
    }

}
