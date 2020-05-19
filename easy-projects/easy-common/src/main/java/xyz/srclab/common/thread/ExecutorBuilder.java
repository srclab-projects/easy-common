package xyz.srclab.common.thread;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * @author sunqian
 */
public class ExecutorBuilder {

    private int corePoolSize = 1;
    private int maximumPoolSize = 1;
    private int workQueueCapacity = Integer.MAX_VALUE;
    private @Nullable Duration keepAliveTime;
    private @Nullable BlockingQueue<Runnable> workQueue;
    private @Nullable ThreadFactory threadFactory;
    private @Nullable RejectedExecutionHandler rejectedExecutionHandler;

    public ExecutorBuilder setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ExecutorBuilder setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public ExecutorBuilder setWorkQueueCapacity(int workQueueCapacity) {
        this.workQueueCapacity = workQueueCapacity;
        return this;
    }

    public ExecutorBuilder setKeepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ExecutorBuilder setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    public ExecutorService build() {
        long keepTime;
        TimeUnit keepUnit;
        if (keepAliveTime == null) {
            keepTime = 0;
            keepUnit = TimeUnit.MILLISECONDS;
        } else {
            keepTime = keepAliveTime.toNanos();
            keepUnit = TimeUnit.NANOSECONDS;
        }
        if (workQueue == null) {
            workQueue = new LinkedBlockingQueue<>(workQueueCapacity);
        }
        if (threadFactory == null) {
            threadFactory = Executors.defaultThreadFactory();
        }
        return rejectedExecutionHandler == null ?
                new ThreadPoolExecutor(
                        corePoolSize, maximumPoolSize, keepTime, keepUnit, workQueue, threadFactory)
                :
                new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepTime,
                        keepUnit,
                        workQueue,
                        threadFactory,
                        rejectedExecutionHandler
                );
    }

    public ScheduledExecutorService buildScheduled() {
        if (threadFactory == null) {
            threadFactory = Executors.defaultThreadFactory();
        }
        return rejectedExecutionHandler == null ?
                new ScheduledThreadPoolExecutor(corePoolSize, threadFactory)
                :
                new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler);
    }
}
