package com.ariel.redis.lua;

import com.ariel.redis.LuaUtil;
import com.ariel.redis.RedissonFactory;
import org.junit.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;

import java.util.List;

public class KeywordTest {

    private final RedissonClient redissonClient = RedissonFactory.getRedissonClient();

    @Test
    public void callGet() {
        RBucket<Object> rBucket = redissonClient.getBucket("hello");
        rBucket.set("world");
        Object result = redissonClient.getScript().eval(
                "hello",
                RScript.Mode.READ_ONLY,
                "return redis.call('GET',KEYS[1])",
                RScript.ReturnType.VALUE,
                List.of("hello"),
                List.of()
        );
        System.out.println(result);
    }

    @Test
    public void callEcho() {
        Object result = redissonClient.getScript(StringCodec.INSTANCE).eval(
                RScript.Mode.READ_ONLY,
                "return redis.call('ECHO', 'Echo, echo... eco... o...')",
                RScript.ReturnType.VALUE
        );
        System.out.println(result);
    }

    @Test
    public void callEchoError() {
        Object result = redissonClient.getScript(StringCodec.INSTANCE).eval(
                RScript.Mode.READ_ONLY,
                "return redis.call('ECHO', 'Echo,', 'echo... ', 'eco... ', 'o...')",
                RScript.ReturnType.VALUE
        );
        System.out.println(result);
    }

    @Test
    public void pcall() {
        Object result = redissonClient.getScript(StringCodec.INSTANCE).eval(
                RScript.Mode.READ_ONLY,
                LuaUtil.readAsString("pcall.lua"),
                RScript.ReturnType.VALUE
        );
        System.out.println(result);
    }

}
