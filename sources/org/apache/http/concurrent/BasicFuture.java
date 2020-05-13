package org.apache.http.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.util.Args;

public class BasicFuture<T> implements Future<T>, Cancellable {
    private final FutureCallback<T> callback;
    private volatile boolean cancelled;
    private volatile boolean completed;
    private volatile Exception ex;
    private volatile T result;

    public BasicFuture(FutureCallback<T> callback2) {
        this.callback = callback2;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isDone() {
        return this.completed;
    }

    private T getResult() throws ExecutionException {
        if (this.ex == null) {
            return this.result;
        }
        throw new ExecutionException(this.ex);
    }

    public synchronized T get() throws InterruptedException, ExecutionException {
        while (!this.completed) {
            wait();
        }
        return getResult();
    }

    public synchronized T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        T result2;
        Args.notNull(unit, "Time unit");
        long msecs = unit.toMillis(timeout);
        long startTime = msecs <= 0 ? 0 : System.currentTimeMillis();
        long waitTime = msecs;
        if (this.completed) {
            result2 = getResult();
        } else if (waitTime <= 0) {
            throw new TimeoutException();
        } else {
            do {
                wait(waitTime);
                if (this.completed) {
                    result2 = getResult();
                } else {
                    waitTime = msecs - (System.currentTimeMillis() - startTime);
                }
            } while (waitTime > 0);
            throw new TimeoutException();
        }
        return result2;
    }

    public boolean completed(T result2) {
        boolean z = true;
        synchronized (this) {
            if (this.completed) {
                z = false;
            } else {
                this.completed = true;
                this.result = result2;
                notifyAll();
                if (this.callback != null) {
                    this.callback.completed(result2);
                }
            }
        }
        return z;
    }

    public boolean failed(Exception exception) {
        boolean z = true;
        synchronized (this) {
            if (this.completed) {
                z = false;
            } else {
                this.completed = true;
                this.ex = exception;
                notifyAll();
                if (this.callback != null) {
                    this.callback.failed(exception);
                }
            }
        }
        return z;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean z = true;
        synchronized (this) {
            if (this.completed) {
                z = false;
            } else {
                this.completed = true;
                this.cancelled = true;
                notifyAll();
                if (this.callback != null) {
                    this.callback.cancelled();
                }
            }
        }
        return z;
    }

    public boolean cancel() {
        return cancel(true);
    }
}
