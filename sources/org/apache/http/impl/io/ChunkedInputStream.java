package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.MalformedChunkCodingException;
import org.apache.http.TruncatedChunkException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class ChunkedInputStream extends InputStream {
    private static final int BUFFER_SIZE = 2048;
    private static final int CHUNK_CRLF = 3;
    private static final int CHUNK_DATA = 2;
    private static final int CHUNK_INVALID = Integer.MAX_VALUE;
    private static final int CHUNK_LEN = 1;
    private final CharArrayBuffer buffer;
    private long chunkSize;
    private boolean closed;
    private final MessageConstraints constraints;
    private boolean eof;
    private Header[] footers;
    private final SessionInputBuffer in;
    private long pos;
    private int state;

    public ChunkedInputStream(SessionInputBuffer in2, MessageConstraints constraints2) {
        this.eof = false;
        this.closed = false;
        this.footers = new Header[0];
        this.in = (SessionInputBuffer) Args.notNull(in2, "Session input buffer");
        this.pos = 0;
        this.buffer = new CharArrayBuffer(16);
        this.constraints = constraints2 == null ? MessageConstraints.DEFAULT : constraints2;
        this.state = 1;
    }

    public ChunkedInputStream(SessionInputBuffer in2) {
        this(in2, (MessageConstraints) null);
    }

    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            return (int) Math.min((long) ((BufferInfo) this.in).length(), this.chunkSize - this.pos);
        }
        return 0;
    }

    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        } else if (this.eof) {
            return -1;
        } else {
            if (this.state != 2) {
                nextChunk();
                if (this.eof) {
                    return -1;
                }
            }
            int b = this.in.read();
            if (b == -1) {
                return b;
            }
            this.pos++;
            if (this.pos < this.chunkSize) {
                return b;
            }
            this.state = 3;
            return b;
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        } else if (this.eof) {
            return -1;
        } else {
            if (this.state != 2) {
                nextChunk();
                if (this.eof) {
                    return -1;
                }
            }
            int bytesRead = this.in.read(b, off, (int) Math.min((long) len, this.chunkSize - this.pos));
            if (bytesRead != -1) {
                this.pos += (long) bytesRead;
                if (this.pos < this.chunkSize) {
                    return bytesRead;
                }
                this.state = 3;
                return bytesRead;
            }
            this.eof = true;
            throw new TruncatedChunkException("Truncated chunk ( expected size: " + this.chunkSize + "; actual size: " + this.pos + ")");
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    private void nextChunk() throws IOException {
        if (this.state == CHUNK_INVALID) {
            throw new MalformedChunkCodingException("Corrupt data stream");
        }
        try {
            this.chunkSize = getChunkSize();
            if (this.chunkSize < 0) {
                throw new MalformedChunkCodingException("Negative chunk size");
            }
            this.state = 2;
            this.pos = 0;
            if (this.chunkSize == 0) {
                this.eof = true;
                parseTrailerHeaders();
            }
        } catch (MalformedChunkCodingException ex) {
            this.state = CHUNK_INVALID;
            throw ex;
        }
    }

    private long getChunkSize() throws IOException {
        switch (this.state) {
            case 1:
                break;
            case 3:
                this.buffer.clear();
                if (this.in.readLine(this.buffer) != -1) {
                    if (this.buffer.isEmpty()) {
                        this.state = 1;
                        break;
                    } else {
                        throw new MalformedChunkCodingException("Unexpected content at the end of chunk");
                    }
                } else {
                    throw new MalformedChunkCodingException("CRLF expected at end of chunk");
                }
            default:
                throw new IllegalStateException("Inconsistent codec state");
        }
        this.buffer.clear();
        if (this.in.readLine(this.buffer) == -1) {
            throw new ConnectionClosedException("Premature end of chunk coded message body: closing chunk expected");
        }
        int separator = this.buffer.indexOf(59);
        if (separator < 0) {
            separator = this.buffer.length();
        }
        String s = this.buffer.substringTrimmed(0, separator);
        try {
            return Long.parseLong(s, 16);
        } catch (NumberFormatException e) {
            throw new MalformedChunkCodingException("Bad chunk header: " + s);
        }
    }

    private void parseTrailerHeaders() throws IOException {
        try {
            this.footers = AbstractMessageParser.parseHeaders(this.in, this.constraints.getMaxHeaderCount(), this.constraints.getMaxLineLength(), (LineParser) null);
        } catch (HttpException ex) {
            IOException ioe = new MalformedChunkCodingException("Invalid footer: " + ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
    }

    public void close() throws IOException {
        if (!this.closed) {
            try {
                if (!this.eof && this.state != CHUNK_INVALID) {
                    do {
                    } while (read(new byte[2048]) >= 0);
                }
            } finally {
                this.eof = true;
                this.closed = true;
            }
        }
    }

    public Header[] getFooters() {
        return (Header[]) this.footers.clone();
    }
}
