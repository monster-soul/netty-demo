package com.edu.config;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author pengXiangJun
 * @date 2024年07月17日
 * @program
 * @description
 */

public class ThreadPoolSingleton {

    private volatile static ThreadPoolExecutor executor;

    private ThreadPoolSingleton() {
    }

    public static ThreadPoolExecutor getInstance() {
        if (executor == null) {
            synchronized (ThreadPoolExecutor.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(
                            5,
                            50,
                            10,
                            TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>(100),
                            Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }
        return executor;
    }
}
