package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ProtocolException;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HTTP;

@Deprecated
public class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest {
    /* access modifiers changed from: private */
    public boolean consumed;
    private HttpEntity entity;

    public EntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request) throws ProtocolException {
        super(request);
        setEntity(request.getEntity());
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public void setEntity(HttpEntity entity2) {
        this.entity = entity2 != null ? new EntityWrapper(entity2) : null;
        this.consumed = false;
    }

    public boolean expectContinue() {
        Header expect = getFirstHeader("Expect");
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    public boolean isRepeatable() {
        return this.entity == null || this.entity.isRepeatable() || !this.consumed;
    }

    class EntityWrapper extends HttpEntityWrapper {
        EntityWrapper(HttpEntity entity) {
            super(entity);
        }

        public void consumeContent() throws IOException {
            boolean unused = EntityEnclosingRequestWrapper.this.consumed = true;
            super.consumeContent();
        }

        public InputStream getContent() throws IOException {
            boolean unused = EntityEnclosingRequestWrapper.this.consumed = true;
            return super.getContent();
        }

        public void writeTo(OutputStream outstream) throws IOException {
            boolean unused = EntityEnclosingRequestWrapper.this.consumed = true;
            super.writeTo(outstream);
        }
    }
}
