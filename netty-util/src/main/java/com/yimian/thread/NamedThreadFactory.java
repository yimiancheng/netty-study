package com.yimian.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final String prefix;
    final boolean isDaemon;
    final int priority;

    public NamedThreadFactory() {
        this("pool");
    }

    public NamedThreadFactory(String prefix) {
        this(prefix, false, Thread.NORM_PRIORITY);
    }

    public NamedThreadFactory(String prefix, boolean isDaemon, int priority) {
        SecurityManager sm = System.getSecurityManager();
        this.group = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.prefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
        this.isDaemon = isDaemon;
        this.priority = priority;
    }

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, r, prefix + threadNumber.getAndIncrement(), 0);
        thread.setDaemon(isDaemon);
        thread.setPriority(priority);
        return thread;
    }
}
