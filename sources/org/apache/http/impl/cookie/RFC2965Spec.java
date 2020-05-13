package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(threading = ThreadingBehavior.SAFE)
@Obsolete
public class RFC2965Spec extends RFC2109Spec {
    public RFC2965Spec() {
        this((String[]) null, false);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public RFC2965Spec(java.lang.String[] r5, boolean r6) {
        /*
            r4 = this;
            r0 = 10
            org.apache.http.cookie.CommonCookieAttributeHandler[] r1 = new org.apache.http.cookie.CommonCookieAttributeHandler[r0]
            r0 = 0
            org.apache.http.impl.cookie.RFC2965VersionAttributeHandler r2 = new org.apache.http.impl.cookie.RFC2965VersionAttributeHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 1
            org.apache.http.impl.cookie.RFC2965Spec$1 r2 = new org.apache.http.impl.cookie.RFC2965Spec$1
            r2.<init>()
            r1[r0] = r2
            r0 = 2
            org.apache.http.impl.cookie.RFC2965DomainAttributeHandler r2 = new org.apache.http.impl.cookie.RFC2965DomainAttributeHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 3
            org.apache.http.impl.cookie.RFC2965PortAttributeHandler r2 = new org.apache.http.impl.cookie.RFC2965PortAttributeHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 4
            org.apache.http.impl.cookie.BasicMaxAgeHandler r2 = new org.apache.http.impl.cookie.BasicMaxAgeHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 5
            org.apache.http.impl.cookie.BasicSecureHandler r2 = new org.apache.http.impl.cookie.BasicSecureHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 6
            org.apache.http.impl.cookie.BasicCommentHandler r2 = new org.apache.http.impl.cookie.BasicCommentHandler
            r2.<init>()
            r1[r0] = r2
            r2 = 7
            org.apache.http.impl.cookie.BasicExpiresHandler r3 = new org.apache.http.impl.cookie.BasicExpiresHandler
            if (r5 == 0) goto L_0x0062
            java.lang.Object r0 = r5.clone()
            java.lang.String[] r0 = (java.lang.String[]) r0
        L_0x0047:
            r3.<init>(r0)
            r1[r2] = r3
            r0 = 8
            org.apache.http.impl.cookie.RFC2965CommentUrlAttributeHandler r2 = new org.apache.http.impl.cookie.RFC2965CommentUrlAttributeHandler
            r2.<init>()
            r1[r0] = r2
            r0 = 9
            org.apache.http.impl.cookie.RFC2965DiscardAttributeHandler r2 = new org.apache.http.impl.cookie.RFC2965DiscardAttributeHandler
            r2.<init>()
            r1[r0] = r2
            r4.<init>((boolean) r6, (org.apache.http.cookie.CommonCookieAttributeHandler[]) r1)
            return
        L_0x0062:
            java.lang.String[] r0 = DATE_PATTERNS
            goto L_0x0047
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.cookie.RFC2965Spec.<init>(java.lang.String[], boolean):void");
    }

    RFC2965Spec(boolean oneHeader, CommonCookieAttributeHandler... handlers) {
        super(oneHeader, handlers);
    }

    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (header.getName().equalsIgnoreCase(SM.SET_COOKIE2)) {
            return createCookies(header.getElements(), adjustEffectiveHost(origin));
        }
        throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
    }

    /* access modifiers changed from: protected */
    public List<Cookie> parse(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
        return createCookies(elems, adjustEffectiveHost(origin));
    }

    private List<Cookie> createCookies(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
        List<Cookie> cookies = new ArrayList<>(elems.length);
        for (HeaderElement headerelement : elems) {
            String name = headerelement.getName();
            String value = headerelement.getValue();
            if (name == null || name.isEmpty()) {
                throw new MalformedCookieException("Cookie name may not be empty");
            }
            BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
            cookie.setPath(getDefaultPath(origin));
            cookie.setDomain(getDefaultDomain(origin));
            cookie.setPorts(new int[]{origin.getPort()});
            NameValuePair[] attribs = headerelement.getParameters();
            Map<String, NameValuePair> attribmap = new HashMap<>(attribs.length);
            for (int j = attribs.length - 1; j >= 0; j--) {
                NameValuePair param = attribs[j];
                attribmap.put(param.getName().toLowerCase(Locale.ROOT), param);
            }
            for (Map.Entry<String, NameValuePair> entry : attribmap.entrySet()) {
                NameValuePair attrib = entry.getValue();
                String s = attrib.getName().toLowerCase(Locale.ROOT);
                cookie.setAttribute(s, attrib.getValue());
                CookieAttributeHandler handler = findAttribHandler(s);
                if (handler != null) {
                    handler.parse(cookie, attrib.getValue());
                }
            }
            cookies.add(cookie);
        }
        return cookies;
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, SM.COOKIE);
        Args.notNull(origin, "Cookie origin");
        super.validate(cookie, adjustEffectiveHost(origin));
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        Args.notNull(cookie, SM.COOKIE);
        Args.notNull(origin, "Cookie origin");
        return super.match(cookie, adjustEffectiveHost(origin));
    }

    /* access modifiers changed from: protected */
    public void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version) {
        String s;
        int[] ports;
        super.formatCookieAsVer(buffer, cookie, version);
        if ((cookie instanceof ClientCookie) && (s = ((ClientCookie) cookie).getAttribute(ClientCookie.PORT_ATTR)) != null) {
            buffer.append("; $Port");
            buffer.append("=\"");
            if (!s.trim().isEmpty() && (ports = cookie.getPorts()) != null) {
                int len = ports.length;
                for (int i = 0; i < len; i++) {
                    if (i > 0) {
                        buffer.append(",");
                    }
                    buffer.append(Integer.toString(ports[i]));
                }
            }
            buffer.append("\"");
        }
    }

    private static CookieOrigin adjustEffectiveHost(CookieOrigin origin) {
        String host = origin.getHost();
        boolean isLocalHost = true;
        int i = 0;
        while (true) {
            if (i >= host.length()) {
                break;
            }
            char ch = host.charAt(i);
            if (ch == '.' || ch == ':') {
                isLocalHost = false;
            } else {
                i++;
            }
        }
        if (isLocalHost) {
            return new CookieOrigin(host + ".local", origin.getPort(), origin.getPath(), origin.isSecure());
        }
        return origin;
    }

    public int getVersion() {
        return 1;
    }

    public Header getVersionHeader() {
        CharArrayBuffer buffer = new CharArrayBuffer(40);
        buffer.append(SM.COOKIE2);
        buffer.append(": ");
        buffer.append("$Version=");
        buffer.append(Integer.toString(getVersion()));
        return new BufferedHeader(buffer);
    }

    public String toString() {
        return CookiePolicy.RFC_2965;
    }
}
