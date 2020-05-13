package org.apache.http.message;

import java.util.NoSuchElementException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.util.Args;

public class BasicHeaderIterator implements HeaderIterator {
    protected final Header[] allHeaders;
    protected int currentIndex = findNext(-1);
    protected String headerName;

    public BasicHeaderIterator(Header[] headers, String name) {
        this.allHeaders = (Header[]) Args.notNull(headers, "Header array");
        this.headerName = name;
    }

    /* access modifiers changed from: protected */
    public int findNext(int pos) {
        int from = pos;
        if (from < -1) {
            return -1;
        }
        int to = this.allHeaders.length - 1;
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
        return this.headerName == null || this.headerName.equalsIgnoreCase(this.allHeaders[index].getName());
    }

    public boolean hasNext() {
        return this.currentIndex >= 0;
    }

    public Header nextHeader() throws NoSuchElementException {
        int current = this.currentIndex;
        if (current < 0) {
            throw new NoSuchElementException("Iteration already finished.");
        }
        this.currentIndex = findNext(current);
        return this.allHeaders[current];
    }

    public final Object next() throws NoSuchElementException {
        return nextHeader();
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Removing headers is not supported.");
    }
}
