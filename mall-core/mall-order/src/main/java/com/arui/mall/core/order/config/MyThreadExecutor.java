package com.arui.mall.core.order.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadExecutor {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            50,
            100,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100)
    );

    private MyThreadExecutor(){};

    public static ThreadPoolExecutor getInstance(){
        return executor;
    }
}