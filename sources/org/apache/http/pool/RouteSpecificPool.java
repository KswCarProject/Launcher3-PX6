package org.apache.http.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.http.pool.PoolEntry;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

abstract class RouteSpecificPool<T, C, E extends PoolEntry<T, C>> {
    private final LinkedList<E> available = new LinkedList<>();
    private final Set<E> leased = new HashSet();
    private final LinkedList<Future<E>> pending = new LinkedList<>();
    private final T route;

    /* access modifiers changed from: protected */
    public abstract E createEntry(C c);

    RouteSpecificPool(T route2) {
        this.route = route2;
    }

    public final T getRoute() {
        return this.route;
    }

    public int getLeasedCount() {
        return this.leased.size();
    }

    public int getPendingCount() {
        return this.pending.size();
    }

    public int getAvailableCount() {
        return this.available.size();
    }

    public int getAllocatedCount() {
        return this.available.size() + this.leased.size();
    }

    public E getFree(Object state) {
        if (!this.available.isEmpty()) {
            if (state != null) {
                Iterator<E> it = this.available.iterator();
                while (it.hasNext()) {
                    E entry = (PoolEntry) it.next();
                    if (state.equals(entry.getState())) {
                        it.remove();
                        this.leased.add(entry);
                        return entry;
                    }
                }
            }
            Iterator<E> it2 = this.available.iterator();
            while (it2.hasNext()) {
                E entry2 = (PoolEntry) it2.next();
                if (entry2.getState() == null) {
                    it2.remove();
                    this.leased.add(entry2);
                    return entry2;
                }
            }
        }
        return null;
    }

    public E getLastUsed() {
        if (!this.available.isEmpty()) {
            return (PoolEntry) this.available.getLast();
        }
        return null;
    }

    public boolean remove(E entry) {
        Args.notNull(entry, "Pool entry");
        if (this.available.remove(entry) || this.leased.remove(entry)) {
            return true;
        }
        return false;
    }

    public void free(E entry, boolean reusable) {
        Args.notNull(entry, "Pool entry");
        Asserts.check(this.leased.remove(entry), "Entry %s has not been leased from this pool", (Object) entry);
        if (reusable) {
            this.available.addFirst(entry);
        }
    }

    public E add(C conn) {
        E entry = createEntry(conn);
        this.leased.add(entry);
        return entry;
    }

    public void queue(Future<E> future) {
        if (future != null) {
            this.pending.add(future);
        }
    }

    public Future<E> nextPending() {
        return this.pending.poll();
    }

    public void unqueue(Future<E> future) {
        if (future != null) {
            this.pending.remove(future);
        }
    }

    public void shutdown() {
        Iterator i$ = this.pending.iterator();
        while (i$.hasNext()) {
            ((Future) i$.next()).cancel(true);
        }
        this.pending.clear();
        Iterator i$2 = this.available.iterator();
        while (i$2.hasNext()) {
            ((PoolEntry) i$2.next()).close();
        }
        this.available.clear();
        for (E entry : this.leased) {
            entry.close();
        }
        this.leased.clear();
    }

    public String toString() {
        return "[route: " + this.route + "][leased: " + this.leased.size() + "][available: " + this.available.size() + "][pending: " + this.pending.size() + "]";
    }
}
