package org.apache.http.client;

import org.apache.http.ProtocolException;

public class NonRepeatableRequestException extends ProtocolException {
    private static final long serialVersionUID = 82685265288806048L;

    public NonRepeatableRequestException() {
    }

    public NonRepeatableRequestException(String message) {
        super(message);
    }

    public NonRepeatableRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
