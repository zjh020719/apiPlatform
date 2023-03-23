package com.xxfs.fsapigateway.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class TokenBucket {
    /**
     * 令牌桶的容量
     */
    private final int capacity;
    /**
     * 当前令牌数量
     */
    private final AtomicInteger tokens;
    /**
     * 令牌产生速率 tokens/s
     */
    private final int rate;

    /**
     * 构造函数
     *
     * @param capacity 令牌桶的容量
     * @param rate     令牌产生速率 tokens/s
     */
    public TokenBucket(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        // 初始时令牌桶满
        this.tokens = new AtomicInteger(capacity);
        // 定期填充令牌桶
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
        scheduler.scheduleAtFixedRate(() -> {
            while (true) { // 循环尝试更新令牌数量
                int currentTokens = tokens.get();
                int newTokens = Math.min(currentTokens + rate, capacity);
                if (tokens.compareAndSet(currentTokens, newTokens)) {
                    break;
                }
            }
            // 每隔一定时间往令牌桶中添加一个令牌
        }, 0, 1000 / rate, TimeUnit.MILLISECONDS);
    }

    public boolean tryAcquire(int numTokens) {
        // 循环尝试更新令牌数量
        while (true) {
            int currentTokens = tokens.get();
            log.info("token:{}", currentTokens);
            if (numTokens > currentTokens) {
                return false;
            }
            if (tokens.compareAndSet(currentTokens, currentTokens - numTokens)) {
                return true;
            }
        }
    }
}
