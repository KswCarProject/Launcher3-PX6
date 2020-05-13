package org.apache.http.conn.util;

import java.net.IDN;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE)
public final class PublicSuffixMatcher {
    private final Map<String, DomainType> exceptions;
    private final Map<String, DomainType> rules;

    public PublicSuffixMatcher(Collection<String> rules2, Collection<String> exceptions2) {
        this(DomainType.UNKNOWN, rules2, exceptions2);
    }

    public PublicSuffixMatcher(DomainType domainType, Collection<String> rules2, Collection<String> exceptions2) {
        Args.notNull(domainType, "Domain type");
        Args.notNull(rules2, "Domain suffix rules");
        this.rules = new ConcurrentHashMap(rules2.size());
        for (String rule : rules2) {
            this.rules.put(rule, domainType);
        }
        this.exceptions = new ConcurrentHashMap();
        if (exceptions2 != null) {
            for (String exception : exceptions2) {
                this.exceptions.put(exception, domainType);
            }
        }
    }

    public PublicSuffixMatcher(Collection<PublicSuffixList> lists) {
        Args.notNull(lists, "Domain suffix lists");
        this.rules = new ConcurrentHashMap();
        this.exceptions = new ConcurrentHashMap();
        for (PublicSuffixList list : lists) {
            DomainType domainType = list.getType();
            for (String rule : list.getRules()) {
                this.rules.put(rule, domainType);
            }
            List<String> exceptions2 = list.getExceptions();
            if (exceptions2 != null) {
                for (String exception : exceptions2) {
                    this.exceptions.put(exception, domainType);
                }
            }
        }
    }

    private static boolean hasEntry(Map<String, DomainType> map, String rule, DomainType expectedType) {
        DomainType domainType;
        if (map == null || (domainType = map.get(rule)) == null) {
            return false;
        }
        if (expectedType == null || domainType.equals(expectedType)) {
            return true;
        }
        return false;
    }

    private boolean hasRule(String rule, DomainType expectedType) {
        return hasEntry(this.rules, rule, expectedType);
    }

    private boolean hasException(String exception, DomainType expectedType) {
        return hasEntry(this.exceptions, exception, expectedType);
    }

    public String getDomainRoot(String domain) {
        return getDomainRoot(domain, (DomainType) null);
    }

    public String getDomainRoot(String domain, DomainType expectedType) {
        String nextSegment;
        if (domain == null) {
            return null;
        }
        if (domain.startsWith(".")) {
            return null;
        }
        String domainName = null;
        String segment = domain.toLowerCase(Locale.ROOT);
        while (segment != null) {
            if (!hasException(IDN.toUnicode(segment), expectedType)) {
                if (hasRule(IDN.toUnicode(segment), expectedType)) {
                    break;
                }
                int nextdot = segment.indexOf(46);
                if (nextdot != -1) {
                    nextSegment = segment.substring(nextdot + 1);
                } else {
                    nextSegment = null;
                }
                if (nextSegment != null && hasRule("*." + IDN.toUnicode(nextSegment), expectedType)) {
                    break;
                }
                if (nextdot != -1) {
                    domainName = segment;
                }
                segment = nextSegment;
            } else {
                return segment;
            }
        }
        return domainName;
    }

    public boolean matches(String domain) {
        return matches(domain, (DomainType) null);
    }

    public boolean matches(String domain, DomainType expectedType) {
        boolean z = true;
        if (domain == null) {
            return false;
        }
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        if (getDomainRoot(domain, expectedType) != null) {
            z = false;
        }
        return z;
    }
}
