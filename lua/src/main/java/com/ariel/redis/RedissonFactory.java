package com.ariel.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonFactory {

    private static RedissonClient INSTANCE;

    private static final String ADDR = "redis://redis-17991.c301.ap-south-1-1.ec2.cloud.redislabs.com:17991";
    private static final String PASSWORD = "514eH4XCx7JQqlATlX2BYAdyrGwlXAah";
    private static final Integer DATA_BASE = 0;

    public static RedissonClient getRedissonClient() {
        if (INSTANCE == null) {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress(ADDR)
                    .setPassword(PASSWORD)
                    .setDatabase(DATA_BASE)
                    .setConnectionMinimumIdleSize(1)
                    .setConnectionPoolSize(1)
            ;
            INSTANCE = Redisson.create(config);
        }
        return INSTANCE;
    }

}
