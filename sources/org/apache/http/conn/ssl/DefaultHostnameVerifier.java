package org.apache.http.conn.ssl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.conn.util.PublicSuffixMatcher;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public final class DefaultHostnameVerifier implements HostnameVerifier {
    private final Log log;
    private final PublicSuffixMatcher publicSuffixMatcher;

    enum HostNameType {
        IPv4(7),
        IPv6(7),
        DNS(2);
        
        final int subjectType;

        private HostNameType(int subjectType2) {
            this.subjectType = subjectType2;
        }
    }

    public DefaultHostnameVerifier(PublicSuffixMatcher publicSuffixMatcher2) {
        this.log = LogFactory.getLog(getClass());
        this.publicSuffixMatcher = publicSuffixMatcher2;
    }

    public DefaultHostnameVerifier() {
        this((PublicSuffixMatcher) null);
    }

    public boolean verify(String host, SSLSession session) {
        try {
            verify(host, (X509Certificate) session.getPeerCertificates()[0]);
            return true;
        } catch (SSLException ex) {
            if (!this.log.isDebugEnabled()) {
                return false;
            }
            this.log.debug(ex.getMessage(), ex);
            return false;
        }
    }

    public void verify(String host, X509Certificate cert) throws SSLException {
        HostNameType hostType = determineHostFormat(host);
        List<SubjectName> subjectAlts = getSubjectAltNames(cert);
        if (subjectAlts == null || subjectAlts.isEmpty()) {
            String cn = extractCN(cert.getSubjectX500Principal().getName("RFC2253"));
            if (cn == null) {
                throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
            }
            matchCN(host, cn, this.publicSuffixMatcher);
            return;
        }
        switch (hostType) {
            case IPv4:
                matchIPAddress(host, subjectAlts);
                return;
            case IPv6:
                matchIPv6Address(host, subjectAlts);
                return;
            default:
                matchDNSName(host, subjectAlts, this.publicSuffixMatcher);
                return;
        }
    }

    static void matchIPAddress(String host, List<SubjectName> subjectAlts) throws SSLException {
        int i = 0;
        while (i < subjectAlts.size()) {
            SubjectName subjectAlt = subjectAlts.get(i);
            if (subjectAlt.getType() != 7 || !host.equals(subjectAlt.getValue())) {
                i++;
            } else {
                return;
            }
        }
        throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }

    static void matchIPv6Address(String host, List<SubjectName> subjectAlts) throws SSLException {
        String normalisedHost = normaliseAddress(host);
        int i = 0;
        while (i < subjectAlts.size()) {
            SubjectName subjectAlt = subjectAlts.get(i);
            if (subjectAlt.getType() != 7 || !normalisedHost.equals(normaliseAddress(subjectAlt.getValue()))) {
                i++;
            } else {
                return;
            }
        }
        throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }

    static void matchDNSName(String host, List<SubjectName> subjectAlts, PublicSuffixMatcher publicSuffixMatcher2) throws SSLException {
        String normalizedHost = host.toLowerCase(Locale.ROOT);
        int i = 0;
        while (i < subjectAlts.size()) {
            SubjectName subjectAlt = subjectAlts.get(i);
            if (subjectAlt.getType() != 2 || !matchIdentityStrict(normalizedHost, subjectAlt.getValue().toLowerCase(Locale.ROOT), publicSuffixMatcher2)) {
                i++;
            } else {
                return;
            }
        }
        throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }

    static void matchCN(String host, String cn, PublicSuffixMatcher publicSuffixMatcher2) throws SSLException {
        if (!matchIdentityStrict(host.toLowerCase(Locale.ROOT), cn.toLowerCase(Locale.ROOT), publicSuffixMatcher2)) {
            throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
        }
    }

    static boolean matchDomainRoot(String host, String domainRoot) {
        if (domainRoot == null || !host.endsWith(domainRoot)) {
            return false;
        }
        if (host.length() == domainRoot.length() || host.charAt((host.length() - domainRoot.length()) - 1) == '.') {
            return true;
        }
        return false;
    }

    private static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher2, boolean strict) {
        if (publicSuffixMatcher2 != null && host.contains(".") && !matchDomainRoot(host, publicSuffixMatcher2.getDomainRoot(identity, DomainType.ICANN))) {
            return false;
        }
        int asteriskIdx = identity.indexOf(42);
        if (asteriskIdx == -1) {
            return host.equalsIgnoreCase(identity);
        }
        String prefix = identity.substring(0, asteriskIdx);
        String suffix = identity.substring(asteriskIdx + 1);
        if (!prefix.isEmpty() && !host.startsWith(prefix)) {
            return false;
        }
        if (!suffix.isEmpty() && !host.endsWith(suffix)) {
            return false;
        }
        if (!strict || !host.substring(prefix.length(), host.length() - suffix.length()).contains(".")) {
            return true;
        }
        return false;
    }

    static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher2) {
        return matchIdentity(host, identity, publicSuffixMatcher2, false);
    }

    static boolean matchIdentity(String host, String identity) {
        return matchIdentity(host, identity, (PublicSuffixMatcher) null, false);
    }

    static boolean matchIdentityStrict(String host, String identity, PublicSuffixMatcher publicSuffixMatcher2) {
        return matchIdentity(host, identity, publicSuffixMatcher2, true);
    }

    static boolean matchIdentityStrict(String host, String identity) {
        return matchIdentity(host, identity, (PublicSuffixMatcher) null, true);
    }

    static String extractCN(String subjectPrincipal) throws SSLException {
        if (subjectPrincipal == null) {
            return null;
        }
        try {
            List<Rdn> rdns = new LdapName(subjectPrincipal).getRdns();
            for (int i = rdns.size() - 1; i >= 0; i--) {
                Attribute cn = rdns.get(i).toAttributes().get("cn");
                if (cn != null) {
                    try {
                        Object value = cn.get();
                        if (value != null) {
                            return value.toString();
                        }
                    } catch (NoSuchElementException | NamingException e) {
                    }
                }
            }
            return null;
        } catch (InvalidNameException e2) {
            throw new SSLException(subjectPrincipal + " is not a valid X500 distinguished name");
        }
    }

    static HostNameType determineHostFormat(String host) {
        if (InetAddressUtils.isIPv4Address(host)) {
            return HostNameType.IPv4;
        }
        String s = host;
        if (s.startsWith("[") && s.endsWith("]")) {
            s = host.substring(1, host.length() - 1);
        }
        if (InetAddressUtils.isIPv6Address(s)) {
            return HostNameType.IPv6;
        }
        return HostNameType.DNS;
    }

    static List<SubjectName> getSubjectAltNames(X509Certificate cert) {
        try {
            Collection<List<?>> entries = cert.getSubjectAlternativeNames();
            if (entries == null) {
                return Collections.emptyList();
            }
            List<SubjectName> result = new ArrayList<>();
            for (List<?> entry : entries) {
                Integer type = entry.size() >= 2 ? (Integer) entry.get(0) : null;
                if (type != null) {
                    result.add(new SubjectName((String) entry.get(1), type.intValue()));
                }
            }
            return result;
        } catch (CertificateParsingException e) {
            return Collections.emptyList();
        }
    }

    static String normaliseAddress(String hostname) {
        if (hostname == null) {
            return hostname;
        }
        try {
            return InetAddress.getByName(hostname).getHostAddress();
        } catch (UnknownHostException e) {
            return hostname;
        }
    }
}
