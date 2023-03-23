package com.xxfs.fsapibackend;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
class FsapiBackendApplicationTests {
    class TokenBucket {
        private final int capacity; // 令牌桶的容量
        private final AtomicInteger tokens; // 当前令牌数量
        private final int rate; // 令牌产生速率 tokens/s

        public TokenBucket(int capacity, int rate) {
            this.capacity = capacity;
            this.rate = rate;
            this.tokens = new AtomicInteger(capacity); // 初始时令牌桶满
            // 定期填充令牌桶
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                while (true) { // 循环尝试更新令牌数量
                    int currentTokens = tokens.get();
                    int newTokens = Math.min(currentTokens + rate, capacity);
                    if (tokens.compareAndSet(currentTokens, newTokens)) {
                        break;
                    }
                }
            }, 0, 1000 / rate, TimeUnit.MILLISECONDS); // 每隔一定时间往令牌桶中添加一个令牌
        }

        public boolean tryAcquire(int numTokens) {
            while (true) { // 循环尝试更新令牌数量
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
    @Test
    void contextLoads() {
        TokenBucket tokenBucket = new TokenBucket(1000, 100);
        tokenBucket.tryAcquire(1);
    }

}
