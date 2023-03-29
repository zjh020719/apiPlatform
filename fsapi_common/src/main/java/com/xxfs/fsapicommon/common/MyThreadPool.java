package com.xxfs.fsapicommon.common;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zjh
 */
public class MyThreadPool {
    private static volatile MyThreadPool instance = null;  // 使用 volatile 关键字保证线程安全
    private static final int DEFAULT_POOL_SIZE = 10;  // 默认线程池大小
    private ExecutorService executorService;

    private MyThreadPool(int poolSize) {
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    public static MyThreadPool getInstance() {
        if (instance == null) {
            synchronized (MyThreadPool.class) {  // 双重检查锁机制
                if (instance == null) {
                    instance = new MyThreadPool(DEFAULT_POOL_SIZE);
                }
            }
        }
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }


    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public boolean awaitTermination(long timeout, @NotNull java.util.concurrent.TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }
}
