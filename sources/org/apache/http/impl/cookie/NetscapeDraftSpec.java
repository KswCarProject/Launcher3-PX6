package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(threading = ThreadingBehavior.SAFE)
@Obsolete
public class NetscapeDraftSpec extends CookieSpecBase {
    protected static final String EXPIRES_PATTERN = "EEE, dd-MMM-yy HH:mm:ss z";

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NetscapeDraftSpec(java.lang.String[] r7) {
        /*
            r6 = this;
            r4 = 1
            r5 = 0
            r0 = 5
            org.apache.http.cookie.CommonCookieAttributeHandler[] r1 = new org.apache.http.cookie.CommonCookieAttributeHandler[r0]
            org.apache.http.impl.cookie.BasicPathHandler r0 = new org.apache.http.impl.cookie.BasicPathHandler
            r0.<init>()
            r1[r5] = r0
            org.apache.http.impl.cookie.NetscapeDomainHandler r0 = new org.apache.http.impl.cookie.NetscapeDomainHandler
            r0.<init>()
            r1[r4] = r0
            r0 = 2
            org.apache.http.impl.cookie.BasicSecureHandler r2 = new org.apache.http.impl.cookie.BasicSecureHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 3
            org.apache.http.impl.cookie.BasicCommentHandler r2 = new org.apache.http.impl.cookie.BasicCommentHandler
            r2.<init>()
            r1[r0] = r2
            r2 = 4
            org.apache.http.impl.cookie.BasicExpiresHandler r3 = new org.apache.http.impl.cookie.BasicExpiresHandler
            if (r7 == 0) goto L_0x0037
            java.lang.Object r0 = r7.clone()
            java.lang.String[] r0 = (java.lang.String[]) r0
        L_0x002e:
            r3.<init>(r0)
            r1[r2] = r3
            r6.<init>((org.apache.http.cookie.CommonCookieAttributeHandler[]) r1)
            return
        L_0x0037:
            java.lang.String[] r0 = new java.lang.String[r4]
            java.lang.String r4 = "EEE, dd-MMM-yy HH:mm:ss z"
            r0[r5] = r4
            goto L_0x002e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.cookie.NetscapeDraftSpec.<init>(java.lang.String[]):void");
    }

    NetscapeDraftSpec(CommonCookieAttributeHandler... handlers) {
        super(handlers);
    }

    public NetscapeDraftSpec() {
        this((String[]) null);
    }

    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        CharArrayBuffer buffer;
        ParserCursor cursor;
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase(SM.SET_COOKIE)) {
            throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
        }
        NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
        if (header instanceof FormattedHeader) {
            buffer = ((FormattedHeader) header).getBuffer();
            cursor = new ParserCursor(((FormattedHeader) header).getValuePos(), buffer.length());
        } else {
            String s = header.getValue();
            if (s == null) {
                throw new MalformedCookieException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            cursor = new ParserCursor(0, buffer.length());
        }
        return parse(new HeaderElement[]{parser.parseHeader(buffer, cursor)}, origin);
    }

    public List<Header> formatCookies(List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        CharArrayBuffer buffer = new CharArrayBuffer(cookies.size() * 20);
        buffer.append(SM.COOKIE);
        buffer.append(": ");
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            if (i > 0) {
                buffer.append("; ");
            }
            buffer.append(cookie.getName());
            String s = cookie.getValue();
            if (s != null) {
                buffer.append("=");
                buffer.append(s);
            }
        }
        List<Header> headers = new ArrayList<>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }

    public int getVersion() {
        return 0;
    }

    public Header getVersionHeader() {
        return null;
    }

    public String toString() {
        return "netscape";
    }
}
