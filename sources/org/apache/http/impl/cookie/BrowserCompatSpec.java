package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(threading = ThreadingBehavior.SAFE)
@Deprecated
public class BrowserCompatSpec extends CookieSpecBase {
    private static final String[] DEFAULT_DATE_PATTERNS = {"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z"};

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public BrowserCompatSpec(java.lang.String[] r5, org.apache.http.impl.cookie.BrowserCompatSpecFactory.SecurityLevel r6) {
        /*
            r4 = this;
            r0 = 7
            org.apache.http.cookie.CommonCookieAttributeHandler[] r1 = new org.apache.http.cookie.CommonCookieAttributeHandler[r0]
            r0 = 0
            org.apache.http.impl.cookie.BrowserCompatVersionAttributeHandler r2 = new org.apache.http.impl.cookie.BrowserCompatVersionAttributeHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 1
            org.apache.http.impl.cookie.BasicDomainHandler r2 = new org.apache.http.impl.cookie.BasicDomainHandler
            r2.<init>()
            r1[r0] = r2
            r2 = 2
            org.apache.http.impl.cookie.BrowserCompatSpecFactory$SecurityLevel r0 = org.apache.http.impl.cookie.BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_IE_MEDIUM
            if (r6 != r0) goto L_0x004b
            org.apache.http.impl.cookie.BrowserCompatSpec$1 r0 = new org.apache.http.impl.cookie.BrowserCompatSpec$1
            r0.<init>()
        L_0x001d:
            r1[r2] = r0
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
            if (r5 == 0) goto L_0x0051
            java.lang.Object r0 = r5.clone()
            java.lang.String[] r0 = (java.lang.String[]) r0
        L_0x0042:
            r3.<init>(r0)
            r1[r2] = r3
            r4.<init>((org.apache.http.cookie.CommonCookieAttributeHandler[]) r1)
            return
        L_0x004b:
            org.apache.http.impl.cookie.BasicPathHandler r0 = new org.apache.http.impl.cookie.BasicPathHandler
            r0.<init>()
            goto L_0x001d
        L_0x0051:
            java.lang.String[] r0 = DEFAULT_DATE_PATTERNS
            goto L_0x0042
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.cookie.BrowserCompatSpec.<init>(java.lang.String[], org.apache.http.impl.cookie.BrowserCompatSpecFactory$SecurityLevel):void");
    }

    public BrowserCompatSpec(String[] datepatterns) {
        this(datepatterns, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
    }

    public BrowserCompatSpec() {
        this((String[]) null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
    }

    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        CharArrayBuffer buffer;
        ParserCursor cursor;
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase(SM.SET_COOKIE)) {
            throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
        }
        HeaderElement[] helems = header.getElements();
        boolean versioned = false;
        boolean netscape = false;
        for (HeaderElement helem : helems) {
            if (helem.getParameterByName(ClientCookie.VERSION_ATTR) != null) {
                versioned = true;
            }
            if (helem.getParameterByName(ClientCookie.EXPIRES_ATTR) != null) {
                netscape = true;
            }
        }
        if (!netscape && versioned) {
            return parse(helems, origin);
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
        HeaderElement elem = parser.parseHeader(buffer, cursor);
        String name = elem.getName();
        String value = elem.getValue();
        if (name == null || name.isEmpty()) {
            throw new MalformedCookieException("Cookie name may not be empty");
        }
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setPath(getDefaultPath(origin));
        cookie.setDomain(getDefaultDomain(origin));
        NameValuePair[] attribs = elem.getParameters();
        for (int j = attribs.length - 1; j >= 0; j--) {
            NameValuePair attrib = attribs[j];
            String s2 = attrib.getName().toLowerCase(Locale.ROOT);
            cookie.setAttribute(s2, attrib.getValue());
            CookieAttributeHandler handler = findAttribHandler(s2);
            if (handler != null) {
                handler.parse(cookie, attrib.getValue());
            }
        }
        if (netscape) {
            cookie.setVersion(0);
        }
        return Collections.singletonList(cookie);
    }

    private static boolean isQuoteEnclosed(String s) {
        return s != null && s.startsWith("\"") && s.endsWith("\"");
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
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            if (cookie.getVersion() <= 0 || isQuoteEnclosed(cookieValue)) {
                buffer.append(cookieName);
                buffer.append("=");
                if (cookieValue != null) {
                    buffer.append(cookieValue);
                }
            } else {
                BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(buffer, (HeaderElement) new BasicHeaderElement(cookieName, cookieValue), false);
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
        return "compatibility";
    }
}
