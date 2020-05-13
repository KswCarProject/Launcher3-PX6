package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.protocol.HttpContext;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultCookieSpecProvider implements CookieSpecProvider {
    private final CompatibilityLevel compatibilityLevel;
    private volatile CookieSpec cookieSpec;
    private final String[] datepatterns;
    private final boolean oneHeader;
    private final PublicSuffixMatcher publicSuffixMatcher;

    public enum CompatibilityLevel {
        DEFAULT,
        IE_MEDIUM_SECURITY
    }

    public DefaultCookieSpecProvider(CompatibilityLevel compatibilityLevel2, PublicSuffixMatcher publicSuffixMatcher2, String[] datepatterns2, boolean oneHeader2) {
        this.compatibilityLevel = compatibilityLevel2 == null ? CompatibilityLevel.DEFAULT : compatibilityLevel2;
        this.publicSuffixMatcher = publicSuffixMatcher2;
        this.datepatterns = datepatterns2;
        this.oneHeader = oneHeader2;
    }

    public DefaultCookieSpecProvider(CompatibilityLevel compatibilityLevel2, PublicSuffixMatcher publicSuffixMatcher2) {
        this(compatibilityLevel2, publicSuffixMatcher2, (String[]) null, false);
    }

    public DefaultCookieSpecProvider(PublicSuffixMatcher publicSuffixMatcher2) {
        this(CompatibilityLevel.DEFAULT, publicSuffixMatcher2, (String[]) null, false);
    }

    public DefaultCookieSpecProvider() {
        this(CompatibilityLevel.DEFAULT, (PublicSuffixMatcher) null, (String[]) null, false);
    }

    public CookieSpec create(HttpContext context) {
        CommonCookieAttributeHandler basicPathHandler;
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    RFC2965Spec strict = new RFC2965Spec(this.oneHeader, new RFC2965VersionAttributeHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2965DomainAttributeHandler(), this.publicSuffixMatcher), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler());
                    RFC2109Spec obsoleteStrict = new RFC2109Spec(this.oneHeader, new RFC2109VersionHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler());
                    CommonCookieAttributeHandler[] commonCookieAttributeHandlerArr = new CommonCookieAttributeHandler[5];
                    commonCookieAttributeHandlerArr[0] = PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher);
                    if (this.compatibilityLevel == CompatibilityLevel.IE_MEDIUM_SECURITY) {
                        basicPathHandler = new BasicPathHandler() {
                            public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
                            }
                        };
                    } else {
                        basicPathHandler = new BasicPathHandler();
                    }
                    commonCookieAttributeHandlerArr[1] = basicPathHandler;
                    commonCookieAttributeHandlerArr[2] = new BasicSecureHandler();
                    commonCookieAttributeHandlerArr[3] = new BasicCommentHandler();
                    commonCookieAttributeHandlerArr[4] = new BasicExpiresHandler(this.datepatterns != null ? (String[]) this.datepatterns.clone() : new String[]{"EEE, dd-MMM-yy HH:mm:ss z"});
                    this.cookieSpec = new DefaultCookieSpec(strict, obsoleteStrict, new NetscapeDraftSpec(commonCookieAttributeHandlerArr));
                }
            }
        }
        return this.cookieSpec;
    }
}
