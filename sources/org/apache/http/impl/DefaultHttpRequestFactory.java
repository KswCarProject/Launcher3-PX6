package org.apache.http.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultHttpRequestFactory implements HttpRequestFactory {
    public static final DefaultHttpRequestFactory INSTANCE = new DefaultHttpRequestFactory();
    private static final String[] RFC2616_COMMON_METHODS = {HttpGet.METHOD_NAME};
    private static final String[] RFC2616_ENTITY_ENC_METHODS = {HttpPost.METHOD_NAME, HttpPut.METHOD_NAME};
    private static final String[] RFC2616_SPECIAL_METHODS = {HttpHead.METHOD_NAME, HttpOptions.METHOD_NAME, HttpDelete.METHOD_NAME, HttpTrace.METHOD_NAME, "CONNECT"};
    private static final String[] RFC5789_ENTITY_ENC_METHODS = {HttpPatch.METHOD_NAME};

    private static boolean isOneOf(String[] methods, String method) {
        for (String method2 : methods) {
            if (method2.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    public HttpRequest newHttpRequest(RequestLine requestline) throws MethodNotSupportedException {
        Args.notNull(requestline, "Request line");
        String method = requestline.getMethod();
        if (isOneOf(RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        }
        if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        if (isOneOf(RFC5789_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }

    public HttpRequest newHttpRequest(String method, String uri) throws MethodNotSupportedException {
        if (isOneOf(RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (isOneOf(RFC5789_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
}
