package org.apache.http.impl;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import org.apache.http.config.ConnectionConfig;

public final class ConnSupport {
    public static CharsetDecoder createDecoder(ConnectionConfig cconfig) {
        if (cconfig == null) {
            return null;
        }
        Charset charset = cconfig.getCharset();
        CodingErrorAction malformed = cconfig.getMalformedInputAction();
        CodingErrorAction unmappable = cconfig.getUnmappableInputAction();
        if (charset == null) {
            return null;
        }
        CharsetDecoder newDecoder = charset.newDecoder();
        if (malformed == null) {
            malformed = CodingErrorAction.REPORT;
        }
        CharsetDecoder onMalformedInput = newDecoder.onMalformedInput(malformed);
        if (unmappable == null) {
            unmappable = CodingErrorAction.REPORT;
        }
        return onMalformedInput.onUnmappableCharacter(unmappable);
    }

    public static CharsetEncoder createEncoder(ConnectionConfig cconfig) {
        Charset charset;
        if (cconfig == null || (charset = cconfig.getCharset()) == null) {
            return null;
        }
        CodingErrorAction malformed = cconfig.getMalformedInputAction();
        CodingErrorAction unmappable = cconfig.getUnmappableInputAction();
        CharsetEncoder newEncoder = charset.newEncoder();
        if (malformed == null) {
            malformed = CodingErrorAction.REPORT;
        }
        CharsetEncoder onMalformedInput = newEncoder.onMalformedInput(malformed);
        if (unmappable == null) {
            unmappable = CodingErrorAction.REPORT;
        }
        return onMalformedInput.onUnmappableCharacter(unmappable);
    }
}
