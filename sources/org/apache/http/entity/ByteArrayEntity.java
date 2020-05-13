package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.Args;

public class ByteArrayEntity extends AbstractHttpEntity implements Cloneable {
    private final byte[] b;
    @Deprecated
    protected final byte[] content;
    private final int len;
    private final int off;

    public ByteArrayEntity(byte[] b2, ContentType contentType) {
        Args.notNull(b2, "Source byte array");
        this.content = b2;
        this.b = b2;
        this.off = 0;
        this.len = this.b.length;
        if (contentType != null) {
            setContentType(contentType.toString());
        }
    }

    public ByteArrayEntity(byte[] b2, int off2, int len2, ContentType contentType) {
        Args.notNull(b2, "Source byte array");
        if (off2 < 0 || off2 > b2.length || len2 < 0 || off2 + len2 < 0 || off2 + len2 > b2.length) {
            throw new IndexOutOfBoundsException("off: " + off2 + " len: " + len2 + " b.length: " + b2.length);
        }
        this.content = b2;
        this.b = b2;
        this.off = off2;
        this.len = len2;
        if (contentType != null) {
            setContentType(contentType.toString());
        }
    }

    public ByteArrayEntity(byte[] b2) {
        this(b2, (ContentType) null);
    }

    public ByteArrayEntity(byte[] b2, int off2, int len2) {
        this(b2, off2, len2, (ContentType) null);
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return (long) this.len;
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(this.b, this.off, this.len);
    }

    public void writeTo(OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        outstream.write(this.b, this.off, this.len);
        outstream.flush();
    }

    public boolean isStreaming() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
