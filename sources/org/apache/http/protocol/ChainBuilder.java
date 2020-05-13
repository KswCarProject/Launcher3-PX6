package org.apache.http.protocol;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

final class ChainBuilder<E> {
    private final LinkedList<E> list = new LinkedList<>();
    private final Map<Class<?>, E> uniqueClasses = new HashMap();

    private void ensureUnique(E e) {
        E previous = this.uniqueClasses.remove(e.getClass());
        if (previous != null) {
            this.list.remove(previous);
        }
        this.uniqueClasses.put(e.getClass(), e);
    }

    public ChainBuilder<E> addFirst(E e) {
        if (e != null) {
            ensureUnique(e);
            this.list.addFirst(e);
        }
        return this;
    }

    public ChainBuilder<E> addLast(E e) {
        if (e != null) {
            ensureUnique(e);
            this.list.addLast(e);
        }
        return this;
    }

    public ChainBuilder<E> addAllFirst(Collection<E> c) {
        if (c != null) {
            for (E e : c) {
                addFirst(e);
            }
        }
        return this;
    }

    public ChainBuilder<E> addAllFirst(E... c) {
        if (c != null) {
            for (E e : c) {
                addFirst(e);
            }
        }
        return this;
    }

    public ChainBuilder<E> addAllLast(Collection<E> c) {
        if (c != null) {
            for (E e : c) {
                addLast(e);
            }
        }
        return this;
    }

    public ChainBuilder<E> addAllLast(E... c) {
        if (c != null) {
            for (E e : c) {
                addLast(e);
            }
        }
        return this;
    }

    public LinkedList<E> build() {
        return new LinkedList<>(this.list);
    }
}
