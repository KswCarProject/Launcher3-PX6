package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookiePathComparator;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.TokenParser;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(threading = ThreadingBehavior.SAFE)
@Obsolete
public class RFC2109Spec extends CookieSpecBase {
    static final String[] DATE_PATTERNS = {"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy"};
    private final boolean oneHeader;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public RFC2109Spec(java.lang.String[] r5, boolean r6) {
        /*
            r4 = this;
            r0 = 7
            org.apache.http.cookie.CommonCookieAttributeHandler[] r1 = new org.apache.http.cookie.CommonCookieAttributeHandler[r0]
            r0 = 0
            org.apache.http.impl.cookie.RFC2109VersionHandler r2 = new org.apache.http.impl.cookie.RFC2109VersionHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 1
            org.apache.http.impl.cookie.RFC2109Spec$1 r2 = new org.apache.http.impl.cookie.RFC2109Spec$1
            r2.<init>()
            r1[r0] = r2
            r0 = 2
            org.apache.http.impl.cookie.RFC2109DomainHandler r2 = new org.apache.http.impl.cookie.RFC2109DomainHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 3
            org.apache.http.impl.cookie.BasicMaxAgeHandler r2 = new org.apache.http.impl.cookie.BasicMaxAgeHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 4
            org.apache.http.impl.cookie.BasicSecureHandler r2 = new org.apache.http.impl.cookie.BasicSecureHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 5
            org.apache.http.impl.cookie.BasicCommentHandler r2 = new org.apache.http.impl.cookie.BasicCommentHandler
            r2.<init>()
            r1[r0] = r2
            r2 = 6
            org.apache.http.impl.cookie.BasicExpiresHandler r3 = new org.apache.http.impl.cookie.BasicExpiresHandler
            if (r5 == 0) goto L_0x0049
            java.lang.Object r0 = r5.clone()
            java.lang.String[] r0 = (java.lang.String[]) r0
        L_0x003e:
            r3.<init>(r0)
            r1[r2] = r3
            r4.<init>((org.apache.http.cookie.CommonCookieAttributeHandler[]) r1)
            r4.oneHeader = r6
            return
        L_0x0049:
            java.lang.String[] r0 = DATE_PATTERNS
            goto L_0x003e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.cookie.RFC2109Spec.<init>(java.lang.String[], boolean):void");
    }

    public RFC2109Spec() {
        this((String[]) null, false);
    }

    protected RFC2109Spec(boolean oneHeader2, CommonCookieAttributeHandler... handlers) {
        super(handlers);
        this.oneHeader = oneHeader2;
    }

    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (header.getName().equalsIgnoreCase(SM.SET_COOKIE)) {
            return parse(header.getElements(), origin);
        }
        throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, SM.COOKIE);
        String name = cookie.getName();
        if (name.indexOf(32) != -1) {
            throw new CookieRestrictionViolationException("Cookie name may not contain blanks");
        } else if (name.startsWith("$")) {
            throw new CookieRestrictionViolationException("Cookie name may not start with $");
        } else {
            super.validate(cookie, origin);
        }
    }

    public List<Header> formatCookies(List<Cookie> cookies) {
        List<Cookie> cookieList;
        Args.notEmpty(cookies, "List of cookies");
        if (cookies.size() > 1) {
            cookieList = new ArrayList<>(cookies);
            Collections.sort(cookieList, CookiePathComparator.INSTANCE);
        } else {
            cookieList = cookies;
        }
        if (this.oneHeader) {
            return doFormatOneHeader(cookieList);
        }
        return doFormatManyHeaders(cookieList);
    }

    private List<Header> doFormatOneHeader(List<Cookie> cookies) {
        int version = Integer.MAX_VALUE;
        for (Cookie cookie : cookies) {
            if (cookie.getVersion() < version) {
                version = cookie.getVersion();
            }
        }
        CharArrayBuffer buffer = new CharArrayBuffer(cookies.size() * 40);
        buffer.append(SM.COOKIE);
        buffer.append(": ");
        buffer.append("$Version=");
        buffer.append(Integer.toString(version));
        for (Cookie cooky : cookies) {
            buffer.append("; ");
            formatCookieAsVer(buffer, cooky, version);
        }
        List<Header> headers = new ArrayList<>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }

    private List<Header> doFormatManyHeaders(List<Cookie> cookies) {
        List<Header> headers = new ArrayList<>(cookies.size());
        for (Cookie cookie : cookies) {
            int version = cookie.getVersion();
            CharArrayBuffer buffer = new CharArrayBuffer(40);
            buffer.append("Cookie: ");
            buffer.append("$Version=");
            buffer.append(Integer.toString(version));
            buffer.append("; ");
            formatCookieAsVer(buffer, cookie, version);
            headers.add(new BufferedHeader(buffer));
        }
        return headers;
    }

    /* access modifiers changed from: protected */
    public void formatParamAsVer(CharArrayBuffer buffer, String name, String value, int version) {
        buffer.append(name);
        buffer.append("=");
        if (value == null) {
            return;
        }
        if (version > 0) {
            buffer.append((char) TokenParser.DQUOTE);
            buffer.append(value);
            buffer.append((char) TokenParser.DQUOTE);
            return;
        }
        buffer.append(value);
    }

    /* access modifiers changed from: protected */
    public void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version) {
        formatParamAsVer(buffer, cookie.getName(), cookie.getValue(), version);
        if (cookie.getPath() != null && (cookie instanceof ClientCookie) && ((ClientCookie) cookie).containsAttribute(ClientCookie.PATH_ATTR)) {
            buffer.append("; ");
            formatParamAsVer(buffer, "$Path", cookie.getPath(), version);
        }
        if (cookie.getDomain() != null && (cookie instanceof ClientCookie) && ((ClientCookie) cookie).containsAttribute(ClientCookie.DOMAIN_ATTR)) {
            buffer.append("; ");
            formatParamAsVer(buffer, "$Domain", cookie.getDomain(), version);
        }
    }

    public int getVersion() {
        return 1;
    }

    public Header getVersionHeader() {
        return null;
    }

    public String toString() {
        return CookiePolicy.RFC_2109;
    }
}
