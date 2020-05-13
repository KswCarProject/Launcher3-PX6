package org.apache.http.pool;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.pool.PoolEntry;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public abstract class AbstractConnPool<T, C, E extends PoolEntry<T, C>> implements ConnPool<T, E>, ConnPoolControl<T> {
    private final LinkedList<E> available = new LinkedList<>();
    /* access modifiers changed from: private */
    public final Condition condition = this.lock.newCondition();
    private final ConnFactory<T, C> connFactory;
    private volatile int defaultMaxPerRoute;
    private volatile boolean isShutDown;
    private final Set<E> leased = new HashSet();
    /* access modifiers changed from: private */
    public final Lock lock = new ReentrantLock();
    private final Map<T, Integer> maxPerRoute = new HashMap();
    private volatile int maxTotal;
    private final LinkedList<Future<E>> pending = new LinkedList<>();
    private final Map<T, RouteSpecificPool<T, C, E>> routeToPool = new HashMap();
    /* access modifiers changed from: private */
    public volatile int validateAfterInactivity;

    /* access modifiers changed from: protected */
    public abstract E createEntry(T t, C c);

    public AbstractConnPool(ConnFactory<T, C> connFactory2, int defaultMaxPerRoute2, int maxTotal2) {
        this.connFactory = (ConnFactory) Args.notNull(connFactory2, "Connection factory");
        this.defaultMaxPerRoute = Args.positive(defaultMaxPerRoute2, "Max per route value");
        this.maxTotal = Args.positive(maxTotal2, "Max total value");
    }

    /* access modifiers changed from: protected */
    public void onLease(E e) {
    }

    /* access modifiers changed from: protected */
    public void onRelease(E e) {
    }

    /* access modifiers changed from: protected */
    public void onReuse(E e) {
    }

    /* access modifiers changed from: protected */
    public boolean validate(E e) {
        return true;
    }

    public boolean isShutdown() {
        return this.isShutDown;
    }

    public void shutdown() throws IOException {
        if (!this.isShutDown) {
            this.isShutDown = true;
            this.lock.lock();
            try {
                Iterator i$ = this.available.iterator();
                while (i$.hasNext()) {
                    ((PoolEntry) i$.next()).close();
                }
                for (E entry : this.leased) {
                    entry.close();
                }
                for (RouteSpecificPool<T, C, E> pool : this.routeToPool.values()) {
                    pool.shutdown();
                }
                this.routeToPool.clear();
                this.leased.clear();
                this.available.clear();
            } finally {
                this.lock.unlock();
            }
        }
    }

    private RouteSpecificPool<T, C, E> getPool(final T route) {
        RouteSpecificPool<T, C, E> pool = this.routeToPool.get(route);
        if (pool != null) {
            return pool;
        }
        RouteSpecificPool<T, C, E> pool2 = new RouteSpecificPool<T, C, E>(route) {
            /* access modifiers changed from: protected */
            public E createEntry(C conn) {
                return AbstractConnPool.this.createEntry(route, conn);
            }
        };
        this.routeToPool.put(route, pool2);
        return pool2;
    }

    public Future<E> lease(final T route, final Object state, final FutureCallback<E> callback) {
        Args.notNull(route, "Route");
        Asserts.check(!this.isShutDown, "Connection pool shut down");
        return new Future<E>() {
            private volatile boolean cancelled;
            private volatile boolean done;
            private volatile E entry;

            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean result = true;
                this.cancelled = true;
                AbstractConnPool.this.lock.lock();
                try {
                    AbstractConnPool.this.condition.signalAll();
                    synchronized (this) {
                        if (this.done) {
                            result = false;
                        }
                        this.done = true;
                        if (callback != null) {
                            callback.cancelled();
                        }
                    }
                    return result;
                } finally {
                    AbstractConnPool.this.lock.unlock();
                }
            }

            public boolean isCancelled() {
                return this.cancelled;
            }

            public boolean isDone() {
                return this.done;
            }

            public E get() throws InterruptedException, ExecutionException {
                try {
                    return get(0, TimeUnit.MILLISECONDS);
                } catch (TimeoutException ex) {
                    throw new ExecutionException(ex);
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
                r9.entry = r8;
                r9.done = true;
                r9.this$0.onLease(r9.entry);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:25:0x0067, code lost:
                if (r5 == null) goto L_0x0070;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:26:0x0069, code lost:
                r5.completed(r9.entry);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x0070, code lost:
                r1 = r9.entry;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public E get(long r10, java.util.concurrent.TimeUnit r12) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
                /*
                    r9 = this;
                    E r1 = r9.entry
                    if (r1 == 0) goto L_0x0007
                    E r1 = r9.entry
                L_0x0006:
                    return r1
                L_0x0007:
                    monitor-enter(r9)
                L_0x0008:
                    org.apache.http.pool.AbstractConnPool r1 = org.apache.http.pool.AbstractConnPool.this     // Catch:{ IOException -> 0x0043 }
                    java.lang.Object r2 = r3     // Catch:{ IOException -> 0x0043 }
                    java.lang.Object r3 = r4     // Catch:{ IOException -> 0x0043 }
                    r4 = r10
                    r6 = r12
                    r7 = r9
                    org.apache.http.pool.PoolEntry r8 = r1.getPoolEntryBlocking(r2, r3, r4, r6, r7)     // Catch:{ IOException -> 0x0043 }
                    org.apache.http.pool.AbstractConnPool r1 = org.apache.http.pool.AbstractConnPool.this     // Catch:{ IOException -> 0x0043 }
                    int r1 = r1.validateAfterInactivity     // Catch:{ IOException -> 0x0043 }
                    if (r1 <= 0) goto L_0x0059
                    long r2 = r8.getUpdated()     // Catch:{ IOException -> 0x0043 }
                    org.apache.http.pool.AbstractConnPool r1 = org.apache.http.pool.AbstractConnPool.this     // Catch:{ IOException -> 0x0043 }
                    int r1 = r1.validateAfterInactivity     // Catch:{ IOException -> 0x0043 }
                    long r4 = (long) r1     // Catch:{ IOException -> 0x0043 }
                    long r2 = r2 + r4
                    long r4 = java.lang.System.currentTimeMillis()     // Catch:{ IOException -> 0x0043 }
                    int r1 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r1 > 0) goto L_0x0059
                    org.apache.http.pool.AbstractConnPool r1 = org.apache.http.pool.AbstractConnPool.this     // Catch:{ IOException -> 0x0043 }
                    boolean r1 = r1.validate(r8)     // Catch:{ IOException -> 0x0043 }
                    if (r1 != 0) goto L_0x0059
                    r8.close()     // Catch:{ IOException -> 0x0043 }
                    org.apache.http.pool.AbstractConnPool r1 = org.apache.http.pool.AbstractConnPool.this     // Catch:{ IOException -> 0x0043 }
                    r2 = 0
                    r1.release(r8, (boolean) r2)     // Catch:{ IOException -> 0x0043 }
                    goto L_0x0008
                L_0x0043:
                    r0 = move-exception
                    r1 = 1
                    r9.done = r1     // Catch:{ all -> 0x0056 }
                    org.apache.http.concurrent.FutureCallback r1 = r5     // Catch:{ all -> 0x0056 }
                    if (r1 == 0) goto L_0x0050
                    org.apache.http.concurrent.FutureCallback r1 = r5     // Catch:{ all -> 0x0056 }
                    r1.failed(r0)     // Catch:{ all -> 0x0056 }
                L_0x0050:
                    java.util.concurrent.ExecutionException r1 = new java.util.concurrent.ExecutionException     // Catch:{ all -> 0x0056 }
                    r1.<init>(r0)     // Catch:{ all -> 0x0056 }
                    throw r1     // Catch:{ all -> 0x0056 }
                L_0x0056:
                    r1 = move-exception
                    monitor-exit(r9)     // Catch:{ all -> 0x0056 }
                    throw r1
                L_0x0059:
                    r9.entry = r8     // Catch:{ IOException -> 0x0043 }
                    r1 = 1
                    r9.done = r1     // Catch:{ IOException -> 0x0043 }
                    org.apache.http.pool.AbstractConnPool r1 = org.apache.http.pool.AbstractConnPool.this     // Catch:{ IOException -> 0x0043 }
                    E r2 = r9.entry     // Catch:{ IOException -> 0x0043 }
                    r1.onLease(r2)     // Catch:{ IOException -> 0x0043 }
                    org.apache.http.concurrent.FutureCallback r1 = r5     // Catch:{ IOException -> 0x0043 }
                    if (r1 == 0) goto L_0x0070
                    org.apache.http.concurrent.FutureCallback r1 = r5     // Catch:{ IOException -> 0x0043 }
                    E r2 = r9.entry     // Catch:{ IOException -> 0x0043 }
                    r1.completed(r2)     // Catch:{ IOException -> 0x0043 }
                L_0x0070:
                    E r1 = r9.entry     // Catch:{ IOException -> 0x0043 }
                    monitor-exit(r9)     // Catch:{ all -> 0x0056 }
                    goto L_0x0006
                */
                throw new UnsupportedOperationException("Method not decompiled: org.apache.http.pool.AbstractConnPool.AnonymousClass2.get(long, java.util.concurrent.TimeUnit):org.apache.http.pool.PoolEntry");
            }
        };
    }

    public Future<E> lease(T route, Object state) {
        return lease(route, state, (FutureCallback) null);
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public E getPoolEntryBlocking(T route, Object state, long timeout, TimeUnit tunit, Future<E> future) throws IOException, InterruptedException, TimeoutException {
        RouteSpecificPool<T, C, E> pool;
        E entry;
        boolean success;
        Date deadline = null;
        if (timeout > 0) {
            deadline = new Date(System.currentTimeMillis() + tunit.toMillis(timeout));
        }
        this.lock.lock();
        try {
            pool = getPool(route);
            while (true) {
                Asserts.check(!this.isShutDown, "Connection pool shut down");
                while (true) {
                    entry = pool.getFree(state);
                    if (entry != null) {
                        if (entry.isExpired(System.currentTimeMillis())) {
                            entry.close();
                        }
                        if (!entry.isClosed()) {
                            break;
                        }
                        this.available.remove(entry);
                        pool.free(entry, false);
                    } else {
                        break;
                    }
                }
                if (entry != null) {
                    this.available.remove(entry);
                    this.leased.add(entry);
                    onReuse(entry);
                    this.lock.unlock();
                    return entry;
                }
                int maxPerRoute2 = getMax(route);
                int excess = Math.max(0, (pool.getAllocatedCount() + 1) - maxPerRoute2);
                if (excess > 0) {
                    for (int i = 0; i < excess; i++) {
                        E lastUsed = pool.getLastUsed();
                        if (lastUsed == null) {
                            break;
                        }
                        lastUsed.close();
                        this.available.remove(lastUsed);
                        pool.remove(lastUsed);
                    }
                }
                if (pool.getAllocatedCount() < maxPerRoute2) {
                    int freeCapacity = Math.max(this.maxTotal - this.leased.size(), 0);
                    if (freeCapacity > 0) {
                        if (this.available.size() > freeCapacity - 1 && !this.available.isEmpty()) {
                            E lastUsed2 = (PoolEntry) this.available.removeLast();
                            lastUsed2.close();
                            getPool(lastUsed2.getRoute()).remove(lastUsed2);
                        }
                        E entry2 = pool.add(this.connFactory.create(route));
                        this.leased.add(entry2);
                        this.lock.unlock();
                        return entry2;
                    }
                }
                if (future.isCancelled()) {
                    throw new InterruptedException("Operation interrupted");
                }
                pool.queue(future);
                this.pending.add(future);
                if (deadline != null) {
                    success = this.condition.awaitUntil(deadline);
                } else {
                    this.condition.await();
                    success = true;
                }
                if (future.isCancelled()) {
                    throw new InterruptedException("Operation interrupted");
                }
                pool.unqueue(future);
                this.pending.remove(future);
                if (!success && deadline != null && deadline.getTime() <= System.currentTimeMillis()) {
                    throw new TimeoutException("Timeout waiting for connection");
                }
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    public void release(E entry, boolean reusable) {
        this.lock.lock();
        try {
            if (this.leased.remove(entry)) {
                RouteSpecificPool<T, C, E> pool = getPool(entry.getRoute());
                pool.free(entry, reusable);
                if (!reusable || this.isShutDown) {
                    entry.close();
                } else {
                    this.available.addFirst(entry);
                }
                onRelease(entry);
                Future<E> future = pool.nextPending();
                if (future != null) {
                    this.pending.remove(future);
                } else {
                    future = this.pending.poll();
                }
                if (future != null) {
                    this.condition.signalAll();
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    private int getMax(T route) {
        Integer v = this.maxPerRoute.get(route);
        if (v != null) {
            return v.intValue();
        }
        return this.defaultMaxPerRoute;
    }

    public void setMaxTotal(int max) {
        Args.positive(max, "Max value");
        this.lock.lock();
        try {
            this.maxTotal = max;
        } finally {
            this.lock.unlock();
        }
    }

    public int getMaxTotal() {
        this.lock.lock();
        try {
            return this.maxTotal;
        } finally {
            this.lock.unlock();
        }
    }

    public void setDefaultMaxPerRoute(int max) {
        Args.positive(max, "Max per route value");
        this.lock.lock();
        try {
            this.defaultMaxPerRoute = max;
        } finally {
            this.lock.unlock();
        }
    }

    public int getDefaultMaxPerRoute() {
        this.lock.lock();
        try {
            return this.defaultMaxPerRoute;
        } finally {
            this.lock.unlock();
        }
    }

    public void setMaxPerRoute(T route, int max) {
        Args.notNull(route, "Route");
        Args.positive(max, "Max per route value");
        this.lock.lock();
        try {
            this.maxPerRoute.put(route, Integer.valueOf(max));
        } finally {
            this.lock.unlock();
        }
    }

    public int getMaxPerRoute(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            return getMax(route);
        } finally {
            this.lock.unlock();
        }
    }

    public PoolStats getTotalStats() {
        this.lock.lock();
        try {
            return new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
        } finally {
            this.lock.unlock();
        }
    }

    public PoolStats getStats(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            RouteSpecificPool<T, C, E> pool = getPool(route);
            return new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), getMax(route));
        } finally {
            this.lock.unlock();
        }
    }

    public Set<T> getRoutes() {
        this.lock.lock();
        try {
            return new HashSet(this.routeToPool.keySet());
        } finally {
            this.lock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public void enumAvailable(PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            Iterator<E> it = this.available.iterator();
            while (it.hasNext()) {
                E entry = (PoolEntry) it.next();
                callback.process(entry);
                if (entry.isClosed()) {
                    getPool(entry.getRoute()).remove(entry);
                    it.remove();
                }
            }
            purgePoolMap();
        } finally {
            this.lock.unlock();
        }
    }

    /* access modifiers changed from: protected */
    public void enumLeased(PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            for (E entry : this.leased) {
                callback.process(entry);
            }
        } finally {
            this.lock.unlock();
        }
    }

    private void purgePoolMap() {
        Iterator<Map.Entry<T, RouteSpecificPool<T, C, E>>> it = this.routeToPool.entrySet().iterator();
        while (it.hasNext()) {
            RouteSpecificPool<T, C, E> pool = it.next().getValue();
            if (pool.getPendingCount() + pool.getAllocatedCount() == 0) {
                it.remove();
            }
        }
    }

    public void closeIdle(long idletime, TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        long time = tunit.toMillis(idletime);
        if (time < 0) {
            time = 0;
        }
        final long deadline = System.currentTimeMillis() - time;
        enumAvailable(new PoolEntryCallback<T, C>() {
            public void process(PoolEntry<T, C> entry) {
                if (entry.getUpdated() <= deadline) {
                    entry.close();
                }
            }
        });
    }

    public void closeExpired() {
        final long now = System.currentTimeMillis();
        enumAvailable(new PoolEntryCallback<T, C>() {
            public void process(PoolEntry<T, C> entry) {
                if (entry.isExpired(now)) {
                    entry.close();
                }
            }
        });
    }

    public int getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }

    public void setValidateAfterInactivity(int ms) {
        this.validateAfterInactivity = ms;
    }

    public String toString() {
        return "[leased: " + this.leased + "][available: " + this.available + "][pending: " + this.pending + "]";
    }
}
