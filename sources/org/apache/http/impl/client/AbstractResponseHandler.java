package org.apache.http.impl.client;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public abstract class AbstractResponseHandler<T> implements ResponseHandler<T> {
    public abstract T handleEntity(HttpEntity httpEntity) throws IOException;

    public T handleResponse(HttpResponse response) throws HttpResponseException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        } else if (entity == null) {
            return null;
        } else {
            return handleEntity(entity);
        }
    }
}
