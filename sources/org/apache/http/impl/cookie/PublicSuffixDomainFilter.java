package org.apache.http.impl.cookie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixList;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class PublicSuffixDomainFilter implements CommonCookieAttributeHandler {
    private final CommonCookieAttributeHandler handler;
    private final Map<String, Boolean> localDomainMap = createLocalDomainMap();
    private final PublicSuffixMatcher publicSuffixMatcher;

    private static Map<String, Boolean> createLocalDomainMap() {
        ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<>();
        map.put(".localhost.", Boolean.TRUE);
        map.put(".test.", Boolean.TRUE);
        map.put(".local.", Boolean.TRUE);
        map.put(".local", Boolean.TRUE);
        map.put(".localdomain", Boolean.TRUE);
        return map;
    }

    public PublicSuffixDomainFilter(CommonCookieAttributeHandler handler2, PublicSuffixMatcher publicSuffixMatcher2) {
        this.handler = (CommonCookieAttributeHandler) Args.notNull(handler2, "Cookie handler");
        this.publicSuffixMatcher = (PublicSuffixMatcher) Args.notNull(publicSuffixMatcher2, "Public suffix matcher");
    }

    public PublicSuffixDomainFilter(CommonCookieAttributeHandler handler2, PublicSuffixList suffixList) {
        Args.notNull(handler2, "Cookie handler");
        Args.notNull(suffixList, "Public suffix list");
        this.handler = handler2;
        this.publicSuffixMatcher = new PublicSuffixMatcher(suffixList.getRules(), suffixList.getExceptions());
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        String host = cookie.getDomain();
        int i = host.indexOf(46);
        if (i >= 0) {
            if (!this.localDomainMap.containsKey(host.substring(i)) && this.publicSuffixMatcher.matches(host)) {
                return false;
            }
        } else if (!host.equalsIgnoreCase(origin.getHost()) && this.publicSuffixMatcher.matches(host)) {
            return false;
        }
        return this.handler.match(cookie, origin);
    }

    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        this.handler.parse(cookie, value);
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        this.handler.validate(cookie, origin);
    }

    public String getAttributeName() {
        return this.handler.getAttributeName();
    }

    public static CommonCookieAttributeHandler decorate(CommonCookieAttributeHandler handler2, PublicSuffixMatcher publicSuffixMatcher2) {
        Args.notNull(handler2, "Cookie attribute handler");
        return publicSuffixMatcher2 != null ? new PublicSuffixDomainFilter(handler2, publicSuffixMatcher2) : handler2;
    }
}
