package com.xxfs.fsapibackend.utils;

import com.xxfs.fsapicommon.common.MyThreadPool;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class MyThreadPoolShutdown implements ApplicationRunner {
    private final MyThreadPool threadPool = MyThreadPool.getInstance();

    @Override
    public void run(ApplicationArguments args) {
        // 注册 JVM 关闭钩子函数
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }


}