package org.apache.http.message;

import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.util.Args;

public class BasicHttpResponse extends AbstractHttpMessage implements HttpResponse {
    private int code;
    private HttpEntity entity;
    private Locale locale;
    private final ReasonPhraseCatalog reasonCatalog;
    private String reasonPhrase;
    private StatusLine statusline;
    private ProtocolVersion ver;

    public BasicHttpResponse(StatusLine statusline2, ReasonPhraseCatalog catalog, Locale locale2) {
        this.statusline = (StatusLine) Args.notNull(statusline2, "Status line");
        this.ver = statusline2.getProtocolVersion();
        this.code = statusline2.getStatusCode();
        this.reasonPhrase = statusline2.getReasonPhrase();
        this.reasonCatalog = catalog;
        this.locale = locale2;
    }

    public BasicHttpResponse(StatusLine statusline2) {
        this.statusline = (StatusLine) Args.notNull(statusline2, "Status line");
        this.ver = statusline2.getProtocolVersion();
        this.code = statusline2.getStatusCode();
        this.reasonPhrase = statusline2.getReasonPhrase();
        this.reasonCatalog = null;
        this.locale = null;
    }

    public BasicHttpResponse(ProtocolVersion ver2, int code2, String reason) {
        Args.notNegative(code2, "Status code");
        this.statusline = null;
        this.ver = ver2;
        this.code = code2;
        this.reasonPhrase = reason;
        this.reasonCatalog = null;
        this.locale = null;
    }

    public ProtocolVersion getProtocolVersion() {
        return this.ver;
    }

    public StatusLine getStatusLine() {
        if (this.statusline == null) {
            this.statusline = new BasicStatusLine(this.ver != null ? this.ver : HttpVersion.HTTP_1_1, this.code, this.reasonPhrase != null ? this.reasonPhrase : getReason(this.code));
        }
        return this.statusline;
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setStatusLine(StatusLine statusline2) {
        this.statusline = (StatusLine) Args.notNull(statusline2, "Status line");
        this.ver = statusline2.getProtocolVersion();
        this.code = statusline2.getStatusCode();
        this.reasonPhrase = statusline2.getReasonPhrase();
    }

    public void setStatusLine(ProtocolVersion ver2, int code2) {
        Args.notNegative(code2, "Status code");
        this.statusline = null;
        this.ver = ver2;
        this.code = code2;
        this.reasonPhrase = null;
    }

    public void setStatusLine(ProtocolVersion ver2, int code2, String reason) {
        Args.notNegative(code2, "Status code");
        this.statusline = null;
        this.ver = ver2;
        this.code = code2;
        this.reasonPhrase = reason;
    }

    public void setStatusCode(int code2) {
        Args.notNegative(code2, "Status code");
        this.statusline = null;
        this.code = code2;
        this.reasonPhrase = null;
    }

    public void setReasonPhrase(String reason) {
        this.statusline = null;
        this.reasonPhrase = reason;
    }

    public void setEntity(HttpEntity entity2) {
        this.entity = entity2;
    }

    public void setLocale(Locale locale2) {
        this.locale = (Locale) Args.notNull(locale2, "Locale");
        this.statusline = null;
    }

    /* access modifiers changed from: protected */
    public String getReason(int code2) {
        if (this.reasonCatalog == null) {
            return null;
        }
        return this.reasonCatalog.getReason(code2, this.locale != null ? this.locale : Locale.getDefault());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStatusLine());
        sb.append(TokenParser.SP);
        sb.append(this.headergroup);
        if (this.entity != null) {
            sb.append(TokenParser.SP);
            sb.append(this.entity);
        }
        return sb.toString();
    }
}
