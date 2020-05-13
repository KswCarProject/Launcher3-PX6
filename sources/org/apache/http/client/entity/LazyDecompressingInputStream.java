package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;

class LazyDecompressingInputStream extends InputStream {
    private final InputStreamFactory inputStreamFactory;
    private final InputStream wrappedStream;
    private InputStream wrapperStream;

    public LazyDecompressingInputStream(InputStream wrappedStream2, InputStreamFactory inputStreamFactory2) {
        this.wrappedStream = wrappedStream2;
        this.inputStreamFactory = inputStreamFactory2;
    }

    private void initWrapper() throws IOException {
        if (this.wrapperStream == null) {
            this.wrapperStream = this.inputStreamFactory.create(this.wrappedStream);
        }
    }

    public int read() throws IOException {
        initWrapper();
        return this.wrapperStream.read();
    }

    public int read(byte[] b) throws IOException {
        initWrapper();
        return this.wrapperStream.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        initWrapper();
        return this.wrapperStream.read(b, off, len);
    }

    public long skip(long n) throws IOException {
        initWrapper();
        return this.wrapperStream.skip(n);
    }

    public boolean markSupported() {
        return false;
    }

    public int available() throws IOException {
        initWrapper();
        return this.wrapperStream.available();
    }

    public void close() throws IOException {
        try {
            if (this.wrapperStream != null) {
                this.wrapperStream.close();
            }
        } finally {
            this.wrappedStream.close();
        }
    }
}
