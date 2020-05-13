package org.apache.http.impl.client;

import org.apache.http.params.AbstractHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public class ClientParamsStack extends AbstractHttpParams {
    protected final HttpParams applicationParams;
    protected final HttpParams clientParams;
    protected final HttpParams overrideParams;
    protected final HttpParams requestParams;

    public ClientParamsStack(HttpParams aparams, HttpParams cparams, HttpParams rparams, HttpParams oparams) {
        this.applicationParams = aparams;
        this.clientParams = cparams;
        this.requestParams = rparams;
        this.overrideParams = oparams;
    }

    public ClientParamsStack(ClientParamsStack stack) {
        this(stack.getApplicationParams(), stack.getClientParams(), stack.getRequestParams(), stack.getOverrideParams());
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public ClientParamsStack(ClientParamsStack stack, HttpParams aparams, HttpParams cparams, HttpParams rparams, HttpParams oparams) {
        this(aparams == null ? stack.getApplicationParams() : aparams, cparams == null ? stack.getClientParams() : cparams, rparams == null ? stack.getRequestParams() : rparams, oparams == null ? stack.getOverrideParams() : oparams);
    }

    public final HttpParams getApplicationParams() {
        return this.applicationParams;
    }

    public final HttpParams getClientParams() {
        return this.clientParams;
    }

    public final HttpParams getRequestParams() {
        return this.requestParams;
    }

    public final HttpParams getOverrideParams() {
        return this.overrideParams;
    }

    public Object getParameter(String name) {
        Args.notNull(name, "Parameter name");
        Object result = null;
        if (this.overrideParams != null) {
            result = this.overrideParams.getParameter(name);
        }
        if (result == null && this.requestParams != null) {
            result = this.requestParams.getParameter(name);
        }
        if (result == null && this.clientParams != null) {
            result = this.clientParams.getParameter(name);
        }
        if (result != null || this.applicationParams == null) {
            return result;
        }
        return this.applicationParams.getParameter(name);
    }

    public HttpParams setParameter(String name, Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Setting parameters in a stack is not supported.");
    }

    public boolean removeParameter(String name) {
        throw new UnsupportedOperationException("Removing parameters in a stack is not supported.");
    }

    public HttpParams copy() {
        return this;
    }
}
