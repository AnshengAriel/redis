package com.ariel.redis.lua;

import com.ariel.redis.LuaUtil;
import org.junit.Test;

public class A {

    @Test
    public void t() {
        LuaUtil.execFile("test.lua");
    }

}
