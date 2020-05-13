package com.szchoiceway.index;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class DeferredHandler {
    private Impl mHandler = new Impl();
    private MessageQueue mMessageQueue = Looper.myQueue();
    /* access modifiers changed from: private */
    public LinkedList<Pair<Runnable, Integer>> mQueue = new LinkedList<>();

    private class Impl extends Handler implements MessageQueue.IdleHandler {
        private Impl() {
        }

        public void handleMessage(Message msg) {
            synchronized (DeferredHandler.this.mQueue) {
                if (DeferredHandler.this.mQueue.size() != 0) {
                    Runnable r = (Runnable) ((Pair) DeferredHandler.this.mQueue.removeFirst()).first;
                    r.run();
                    synchronized (DeferredHandler.this.mQueue) {
                        DeferredHandler.this.scheduleNextLocked();
                    }
                }
            }
        }

        public boolean queueIdle() {
            handleMessage((Message) null);
            return false;
        }
    }

    private class IdleRunnable implements Runnable {
        Runnable mRunnable;

        IdleRunnable(Runnable r) {
            this.mRunnable = r;
        }

        public void run() {
            this.mRunnable.run();
        }
    }

    public void post(Runnable runnable) {
        post(runnable, 0);
    }

    public void post(Runnable runnable, int type) {
        synchronized (this.mQueue) {
            this.mQueue.add(new Pair(runnable, Integer.valueOf(type)));
            if (this.mQueue.size() == 1) {
                scheduleNextLocked();
            }
        }
    }

    public void postIdle(Runnable runnable) {
        postIdle(runnable, 0);
    }

    public void postIdle(Runnable runnable, int type) {
        post(new IdleRunnable(runnable), type);
    }

    public void cancelRunnable(Runnable runnable) {
        synchronized (this.mQueue) {
            do {
            } while (this.mQueue.remove(runnable));
        }
    }

    public void cancelAllRunnablesOfType(int type) {
        synchronized (this.mQueue) {
            ListIterator<Pair<Runnable, Integer>> iter = this.mQueue.listIterator();
            while (iter.hasNext()) {
                if (((Integer) iter.next().second).intValue() == type) {
                    iter.remove();
                }
            }
        }
    }

    public void cancel() {
        synchronized (this.mQueue) {
            this.mQueue.clear();
        }
    }

    public void flush() {
        LinkedList<Pair<Runnable, Integer>> queue = new LinkedList<>();
        synchronized (this.mQueue) {
            queue.addAll(this.mQueue);
            this.mQueue.clear();
        }
        Iterator it = queue.iterator();
        while (it.hasNext()) {
            ((Runnable) ((Pair) it.next()).first).run();
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleNextLocked() {
        if (this.mQueue.size() <= 0) {
            return;
        }
        if (((Runnable) this.mQueue.getFirst().first) instanceof IdleRunnable) {
            this.mMessageQueue.addIdleHandler(this.mHandler);
        } else {
            this.mHandler.sendEmptyMessage(1);
        }
    }
}
