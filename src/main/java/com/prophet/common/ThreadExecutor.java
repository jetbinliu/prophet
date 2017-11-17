package com.prophet.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * 线程池基础类
 * @author Mr.July
 *
 */
public class ThreadExecutor {
	private static final int POOL_SIZE = 64;
    private static ExecutorService executorService;

    private static ExecutorService getExecutor() {
        if (executorService == null || executorService.isShutdown()) {
            synchronized (ThreadFactory.class) {
                if (executorService == null || executorService.isShutdown()) {
                    executorService = Executors.newFixedThreadPool(POOL_SIZE);
                }
            }
        }
        return executorService;
    }

    /**
     * 不返回结果的execute方法
     * @param <T extends Runnable> thread
     */
    public static <T extends Runnable> void execute(T thread) {
        getExecutor().execute(thread);
    }

    /**
     * 可以返回结果的提交方法
     * @param Callable<T> task
     * @return 一个Future对象
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return getExecutor().submit(task);
    }

    /**
     * 不再使用线程池时，调用该方法关闭线程池即可
     */
    public static final void shutdown() {
        getExecutor().shutdown();
    }
}
