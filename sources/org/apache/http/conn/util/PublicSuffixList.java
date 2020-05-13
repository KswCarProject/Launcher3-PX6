package org.apache.http.conn.util;

import java.util.Collections;
import java.util.List;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class PublicSuffixList {
    private final List<String> exceptions;
    private final List<String> rules;
    private final DomainType type;

    public PublicSuffixList(DomainType type2, List<String> rules2, List<String> exceptions2) {
        this.type = (DomainType) Args.notNull(type2, "Domain type");
        this.rules = Collections.unmodifiableList((List) Args.notNull(rules2, "Domain suffix rules"));
        this.exceptions = Collections.unmodifiableList(exceptions2 == null ? Collections.emptyList() : exceptions2);
    }

    public PublicSuffixList(List<String> rules2, List<String> exceptions2) {
        this(DomainType.UNKNOWN, rules2, exceptions2);
    }

    public DomainType getType() {
        return this.type;
    }

    public List<String> getRules() {
        return this.rules;
    }

    public List<String> getExceptions() {
        return this.exceptions;
    }
}
