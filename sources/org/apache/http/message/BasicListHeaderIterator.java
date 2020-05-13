package org.apache.http.message;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

public class BasicListHeaderIterator implements HeaderIterator {
    protected final List<Header> allHeaders;
    protected int currentIndex = findNext(-1);
    protected String headerName;
    protected int lastIndex = -1;

    public BasicListHeaderIterator(List<Header> headers, String name) {
        this.allHeaders = (List) Args.notNull(headers, "Header list");
        this.headerName = name;
    }

    /* access modifiers changed from: protected */
    public int findNext(int pos) {
        int from = pos;
        if (from < -1) {
            return -1;
        }
        int to = this.allHeaders.size() - 1;
        boolean found = false;
        while (!found && from < to) {
            from++;
            found = filterHeader(from);
        }
        if (found) {
            return from;
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public boolean filterHeader(int index) {
        if (this.headerName == null) {
            return true;
        }
        return this.headerName.equalsIgnoreCase(this.allHeaders.get(index).getName());
    }

    public boolean hasNext() {
        return this.currentIndex >= 0;
    }

    public Header nextHeader() throws NoSuchElementException {
        int current = this.currentIndex;
        if (current < 0) {
            throw new NoSuchElementException("Iteration already finished.");
        }
        this.lastIndex = current;
        this.currentIndex = findNext(current);
        return this.allHeaders.get(current);
    }

    public final Object next() throws NoSuchElementException {
        return nextHeader();
    }

    public void remove() throws UnsupportedOperationException {
        Asserts.check(this.lastIndex >= 0, "No header to remove");
        this.allHeaders.remove(this.lastIndex);
        this.lastIndex = -1;
        this.currentIndex--;
    }
}
