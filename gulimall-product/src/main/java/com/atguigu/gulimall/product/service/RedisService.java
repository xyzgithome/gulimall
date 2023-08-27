package com.atguigu.gulimall.product.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取分布式所
     *
     * @param key         锁的key
     * @param value       锁的value
     * @param expireTime  锁失效时间 s
     * @param acquireTime 获得锁所需要时间 ms
     * @return 是否占锁成功
     */
    public Boolean getDistributeLock(String key, String value, long expireTime, long acquireTime) {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < startTime + acquireTime) {
            Boolean isLock = redisTemplate.opsForValue()
                    .setIfAbsent(key, value, expireTime, TimeUnit.MINUTES);
            if ((Objects.isNull(isLock) ? false : isLock)) {
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                log.error("getDistributeLock sleep interrupted, msg={}", e.getMessage());
            }
        }
        return false;
    }

    public Boolean releaseLock(String key, String value) {
        // 判断当前删除的锁是不是之前自己加的锁, 是的话返回1，不是的话返回0;
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        // 删除锁
        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), value);

        return Objects.equals(result, 1L);
    }
}


