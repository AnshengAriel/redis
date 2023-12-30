package com.ariel.redis;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LuaUtil {

    private static final RedissonClient redissonClient = RedissonFactory.getRedissonClient();

    public static void execFile(String classPath) {
        StringBuilder b = new StringBuilder();
        try (InputStream in = ClassLoader.getSystemResourceAsStream(classPath)) {
            b.append(readAsString(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        execCommand(b.toString());
    }

    public static void execCommand(String command) {
        RScript script = redissonClient.getScript();
        Object result = script.eval(RScript.Mode.READ_WRITE, command, RScript.ReturnType.BOOLEAN);
        System.out.println(result);
    }

    private static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int length = 1024;
        byte[] bytes = new byte[length];
        while ((length = in.read(bytes)) != -1) {
            out.write(bytes, 0, length);
        }
        return out.toByteArray();
    }

    private static String readAsString(InputStream in) {
        try {
            return new String(readAllBytes(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readAsString(String classPath) {
        StringBuilder b = new StringBuilder();
        try (InputStream in = ClassLoader.getSystemResourceAsStream(classPath)) {
            b.append(readAsString(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return b.toString();
    }

}
