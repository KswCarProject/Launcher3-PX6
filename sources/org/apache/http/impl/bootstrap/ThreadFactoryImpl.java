package org.apache.http.impl.bootstrap;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

class ThreadFactoryImpl implements ThreadFactory {
    private final AtomicLong count;
    private final ThreadGroup group;
    private final String namePrefix;

    ThreadFactoryImpl(String namePrefix2, ThreadGroup group2) {
        this.namePrefix = namePrefix2;
        this.group = group2;
        this.count = new AtomicLong();
    }

    ThreadFactoryImpl(String namePrefix2) {
        this(namePrefix2, (ThreadGroup) null);
    }

    public Thread newThread(Runnable target) {
        return new Thread(this.group, target, this.namePrefix + "-" + this.count.incrementAndGet());
    }
}
