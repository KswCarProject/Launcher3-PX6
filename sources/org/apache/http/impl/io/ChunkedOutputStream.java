package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.io.SessionOutputBuffer;

public class ChunkedOutputStream extends OutputStream {
    private final byte[] cache;
    private int cachePosition;
    private boolean closed;
    private final SessionOutputBuffer out;
    private boolean wroteLastChunk;

    @Deprecated
    public ChunkedOutputStream(SessionOutputBuffer out2, int bufferSize) throws IOException {
        this(bufferSize, out2);
    }

    @Deprecated
    public ChunkedOutputStream(SessionOutputBuffer out2) throws IOException {
        this(2048, out2);
    }

    public ChunkedOutputStream(int bufferSize, SessionOutputBuffer out2) {
        this.cachePosition = 0;
        this.wroteLastChunk = false;
        this.closed = false;
        this.cache = new byte[bufferSize];
        this.out = out2;
    }

    /* access modifiers changed from: protected */
    public void flushCache() throws IOException {
        if (this.cachePosition > 0) {
            this.out.writeLine(Integer.toHexString(this.cachePosition));
            this.out.write(this.cache, 0, this.cachePosition);
            this.out.writeLine("");
            this.cachePosition = 0;
        }
    }

    /* access modifiers changed from: protected */
    public void flushCacheWithAppend(byte[] bufferToAppend, int off, int len) throws IOException {
        this.out.writeLine(Integer.toHexString(this.cachePosition + len));
        this.out.write(this.cache, 0, this.cachePosition);
        this.out.write(bufferToAppend, off, len);
        this.out.writeLine("");
        this.cachePosition = 0;
    }

    /* access modifiers changed from: protected */
    public void writeClosingChunk() throws IOException {
        this.out.writeLine("0");
        this.out.writeLine("");
    }

    public void finish() throws IOException {
        if (!this.wroteLastChunk) {
            flushCache();
            writeClosingChunk();
            this.wroteLastChunk = true;
        }
    }

    public void write(int b) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        this.cache[this.cachePosition] = (byte) b;
        this.cachePosition++;
        if (this.cachePosition == this.cache.length) {
            flushCache();
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] src, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        } else if (len >= this.cache.length - this.cachePosition) {
            flushCacheWithAppend(src, off, len);
        } else {
            System.arraycopy(src, off, this.cache, this.cachePosition, len);
            this.cachePosition += len;
        }
    }

    public void flush() throws IOException {
        flushCache();
        this.out.flush();
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            finish();
            this.out.flush();
        }
    }
}
